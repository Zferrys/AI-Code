# AI-Code 部署与运维文档

## 服务器信息

| 项目 | 内容 |
|------|------|
| 主机 | kr-direct（43.155.213.186） |
| 系统 | Ubuntu 24.04 |
| 用户 | ubuntu |
| 项目目录 | `/opt/aicode/` |

---

## 一、首次部署

### 前提条件

服务器已装好 Docker + Docker Compose：

```bash
# 验证
docker --version        # 29.6.1+
docker compose version  # v5.2.0+
```

### 部署步骤

```bash
# 1. SSH 到服务器
ssh kr-direct

# 2. 拉代码（首次）
cd /opt
git clone git@github.com:Zferrys/AI-Code.git aicode
cd aicode

# 3. 配置环境变量
# .env 文件已存在的话检查一下，没有则创建
cat > .env << 'EOF'
MYSQL_ROOT_PASSWORD=你的MySQL密码
REDIS_PASSWORD=
JWT_SECRET=你的32位以上随机密钥
PRODUCTION=true
CORS_ALLOWED_ORIGINS=http://你的域名或IP
EOF

# 4. 构建并启动
sudo docker compose up -d

# 5. 确认所有服务正常运行
sudo docker compose ps
# 应该看到 4 个容器全部 Up 状态
```

首次启动后 MySQL 会自动执行 `database/init.sql` 初始化表结构和种子数据。

---

## 二、更新流程

当本地代码有修改需要部署到服务器时：

```bash
# 1. 本地提交并推送到 GitHub
cd /d/javacode/Java/Pro/AI-Code
git add -A
git commit -m "改动说明"
git push origin main

# 2. SSH 到服务器拉取更新
ssh kr-direct
cd /opt/aicode
git pull

# 3. 重新构建有改动的服务
#   如果改了后端代码：
sudo docker compose build backend
#   如果改了前端代码或 nginx 配置：
sudo docker compose build frontend
#   如果改了 docker-compose.yml 或 .env：
sudo docker compose up -d  # 重建所有

# 4. 重启
sudo docker compose up -d

# 5. 查看日志确认正常
sudo docker compose logs --tail=20 backend
sudo docker compose logs --tail=20 frontend
```

### 快速更新（只改前端或后端之一）

```bash
# 只改后端
sudo docker compose build backend && sudo docker compose up -d backend

# 只改前端
sudo docker compose build frontend && sudo docker compose up -d frontend

# 只改数据库初始化脚本
sudo docker compose down
sudo rm -rf /opt/aicode/mysql-data  # 删除旧数据（谨慎！）
sudo docker compose up -d           # 重新初始化
```

---

## 三、日常维护

### 查看服务状态

```bash
ssh kr-direct
cd /opt/aicode

# 查看所有容器状态
sudo docker compose ps

# 查看实时日志
sudo docker compose logs -f backend     # 后端日志
sudo docker compose logs -f frontend    # 前端日志
sudo docker compose logs -f mysql       # 数据库日志

# 查看资源占用
sudo docker stats
```

### 备份数据库

```bash
# 导出所有数据
sudo docker exec aicode-mysql mysqldump -uroot -p你的密码 aicode_db > backup_$(date +%Y%m%d).sql

# 导出到项目目录
sudo docker exec aicode-mysql mysqldump -uroot -p你的密码 aicode_db > /opt/aicode/backups/backup_$(date +%Y%m%d).sql
```

### 恢复数据库

```bash
sudo docker exec -i aicode-mysql mysql -uroot -p你的密码 aicode_db < backup_20260627.sql
```

### 查看数据库

```bash
sudo docker exec -it aicode-mysql mysql -uroot -p你的密码 aicode_db
# 进入 MySQL 后：
# show tables;
# SELECT * FROM user;
# SELECT * FROM sys_log ORDER BY create_time DESC LIMIT 10;
```

### 重启单个服务

```bash
sudo docker compose restart backend   # 重启后端
sudo docker compose restart frontend  # 重启前端
sudo docker compose restart mysql     # 重启数据库
```

### 完全停止

```bash
# 停止所有服务（保留数据）
sudo docker compose down

# 停止并删除数据卷（会丢失所有数据！）
sudo docker compose down -v
```

### 查看容器日志排查问题

```bash
# 后端启动日志
sudo docker logs aicode-backend

# 后端实时日志
sudo docker logs -f aicode-backend

# 查看最近 50 行
sudo docker logs --tail=50 aicode-backend
```

---

## 四、服务器运维

### 磁盘空间

```bash
df -h /
# 项目文件 + Docker 镜像通常占 3-5GB
# 日志和数据库会随时间增长
```

### Docker 清理

```bash
# 清理无用的镜像和缓存（安全操作）
sudo docker system prune -f

# 查看磁盘占用
sudo docker system df
```

### 更新管理员密码

```bash
# 生成新密码的 BCrypt 哈希，然后执行 SQL：
sudo docker exec aicode-mysql mysql -uroot -p你的密码 aicode_db \
  -e "UPDATE user SET password='新BCrypt哈希' WHERE username='zferry';"
```

---

## 五、端口说明

| 端口 | 绑定 | 服务 | 说明 |
|------|------|------|------|
| 80 | `0.0.0.0` | 前端 Nginx | 公开访问 |
| 8080 | `127.0.0.1` | 后端 Tomcat | 仅内网 |
| 3306 | `127.0.0.1` | MySQL | 仅内网 |
| 6379 | `127.0.0.1` | Redis | 仅内网 |
| 36162 | `0.0.0.0` | 主机 Nginx | sing-box，独立运行 |

---

## 六、目录结构

```
/opt/aicode/
├── .env                    # 环境变量（不要提交到 git）
├── docker-compose.yml      # 容器编排
├── backend/                # 后端源码 + Dockerfile
├── frontend/               # 前端源码 + Dockerfile + nginx.conf
├── sites/xingxing/         # xingxing 静态页面
├── database/init.sql       # 数据库初始化
└── docs/                   # 文档
```

---

## 七、常见问题

**Q: 启动后访问 80 端口无响应**
A: 检查主机 nginx 是否占用了 80 端口：`sudo ss -tlnp | grep ':80 '`。如果被占用，停掉冲突服务。

**Q: 后端连不上 MySQL**
A: MySQL 启动需要 10-20 秒，后端有 `depends_on` 等待，但第一次启动可能需要更久。查看日志：`sudo docker logs aicode-backend`。

**Q: .env 修改后不生效**
A: 修改 .env 后需要 `sudo docker compose down && sudo docker compose up -d` 重建容器。

**Q: 如何不使用 sudo 运行 docker**
A: 已添加 ubuntu 用户到 docker 组，但需要重新登录生效。当前执行 `newgrp docker` 或重新 SSH。
