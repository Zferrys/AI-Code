# AI-Code 安全机制说明

## 数学验证码

### 接口

```
GET /api/captcha
返回: { captchaId: "uuid", captchaImage: "data:image/png;base64,..." }
```

- 无需认证，直接调用
- 每次返回一个随机数学算式图片（加法或减法，如 "23 + 15 = ?"）
- 答案在服务端内存中保存 3 分钟，一次性使用，验证后立即删除

### 登录页验证码逻辑

```
登录请求 POST /api/user/login
参数: { username, password, captchaId?, captchaAnswer? }
```

- **正常情况（同一 IP 输错密码 < 3 次）**：不显示验证码，前端不传 captchaId/captchaAnswer，后端跳过验证
- **输错 3 次后**：前端显示验证码图片 + 输入框，后续每次登录都需填写正确验证码
- 用户可点击图片刷新验证码
- 验证码校验失败时后端返回 400 "验证码错误或已过期，请重新获取"

### 注册页验证码逻辑

```
注册请求 POST /api/user/register
参数: { username, password, email, captchaId, captchaAnswer }
```

- **始终显示验证码**，不填无法提交
- 注册成功不影响登录验证码的计数

---

## 登录/注册限流

| 端点 | 限流规则 | 存储 |
|------|---------|------|
| `POST /api/user/login` | 同一 IP 连续失败 5 次，封锁 15 分钟 | 内存 ConcurrentHashMap |
| `POST /api/user/register` | 同一 IP 每小时最多 3 次 | 内存 ConcurrentHashMap |

登录限制在服务重启后重置（内存存储）。

---

## 密码策略

| 要求 | 说明 |
|------|------|
| 最小长度 | 12 位 |
| 小写字母 | 至少 1 个 |
| 大写字母 | 至少 1 个 |
| 数字 | 至少 1 个 |
| 特殊字符 | 至少 1 个（!@#$%^&* 等） |
| 存储 | BCrypt 加密 |

---

## JWT 认证

- Token 有效期：2 小时
- 密钥：通过环境变量 `JWT_SECRET` 配置，启动时检查长度 ≥32 位
- 密钥长度不足时服务拒绝启动
- 所有 `/api/admin/**` 接口要求角色为 `ADMIN`，否则返回 403

---

## 文件上传

- 头像上传限制 5MB
- 格式通过 `ImageIO.read()` 自动检测，不使用用户传入的文件扩展名
- 有透明通道 → `.png`，否则 → `.jpg`
- 文件名为 UUID，不可预测

---

## CORS

- 开发环境：默认允许 localhost:3000/8080 等来源
- 生产环境：设 `PRODUCTION=true` 环境变量，未配置 `CORS_ALLOWED_ORIGINS` 时服务拒绝启动

---

## 管理员接口权限

| 保护层 | 措施 |
|--------|------|
| 路由守卫（前端） | 非 ADMIN 角色跳转首页（UX 设计，可被绕过） |
| AuthInterceptor（后端） | 所有 `/api/admin/**` 请求校验 JWT 中 role=ADMIN，否则 403 |

**前端路由守卫不是安全边界，后端拦截器才是。**

---

## AI 错误信息处理

- AI API 返回的错误详情仅记录在服务端日志，不暴露给前端
- 前端统一显示"AI 服务暂时不可用"
- 日志中敏感信息通过 `StringUtil.maskSensitive()` 脱敏（过滤 Bearer token、api_key、password 等）

---

## XSS 防护

- 用户输入入库前通过 `Jsoup.clean(input, Safelist.none())` 过滤（问答内容、代码审查）
- Markdown 渲染时先 HTML 转义再处理标记语法
- 代码块在渲染前提取为占位符，渲染完成后恢复，保证非代码内容全部转义

---

## 配置管理

- 数据库密码通过环境变量 `JDBC_PASSWORD` 注入，代码中无默认值
- JWT 密钥通过环境变量 `JWT_SECRET` 注入
- Redis 地址支持环境变量 `REDIS_HOST` 覆盖（默认 127.0.0.1）
- 系统配置（sys_config）更新有白名单限制：只允许修改 `ai.api.*` 和 `site.*` 开头的配置项

---

## 环境变量清单

| 变量 | 必须 | 说明 |
|------|------|------|
| `JDBC_PASSWORD` | 是 | MySQL 密码 |
| `JWT_SECRET` | 是 | JWT 签名密钥，≥32 位 |
| `CORS_ALLOWED_ORIGINS` | 生产环境 | 允许跨域的来源，逗号分隔 |
| `REDIS_HOST` | 否 | Redis 主机（默认 127.0.0.1） |
| `REDIS_PASSWORD` | 否 | Redis 密码 |
| `PRODUCTION` | 否 | 设为 true 启用生产环境安全校验 |
