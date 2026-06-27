-- ============================================
-- 为 AI应用开发实战学习路径添加思维导图和外部资源
-- 路径ID: 8，课程ID: 67-72
-- ============================================

USE aicode_db;

-- ============================================
-- 课程1：大语言模型基础与生态概览 (id=67)
-- ============================================
UPDATE learning_path_course SET content_markdown = CONCAT(
'```mermaid
mindmap
  root((LLM 基础与生态))
    核心概念
      Transformer 架构
      预训练与微调
      Token 与上下文窗口
    主流模型
      OpenAI GPT-4o / o3
      Anthropic Claude 4
      开源 Llama 3 / Mistral
      DeepSeek / Qwen
    开发环境
      Python + OpenAI SDK
      API Key 配置
      .env 环境变量
    调用流程
      构建 Messages
      设置 Model/Params
      解析 Response
      错误处理与重试
```

', content_markdown)
WHERE id = 67;

-- 课程1 外部资源
INSERT IGNORE INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(67, 'OpenAI API 官方文档', 'LINK', 'https://platform.openai.com/docs', 'OpenAI API 完整参考文档，含快速入门指南', 1, NOW()),
(67, 'Anthropic Claude API 文档', 'LINK', 'https://docs.anthropic.com/en/docs', 'Claude API 使用文档与最佳实践', 2, NOW()),
(67, 'Hugging Face 模型库', 'LINK', 'https://huggingface.co/models', '开源模型仓库，浏览和下载预训练模型', 3, NOW()),
(67, 'Python dotenv 官方文档', 'LINK', 'https://pypi.org/project/python-dotenv/', '环境变量管理工具，安全存储 API Key', 4, NOW()),
(67, 'DeepSeek API 文档', 'LINK', 'https://platform.deepseek.com/docs', 'DeepSeek 模型 API 调用指南（国产免费替代方案）', 5, NOW());


-- ============================================
-- 课程2：高级Prompt工程技巧 (id=68)
-- ============================================
UPDATE learning_path_course SET content_markdown = CONCAT(
'```mermaid
mindmap
  root((Prompt Engineering))
    核心技巧
      角色设定 Persona
      思维链 CoT
      少样本 Few-Shot
      结构化输出 JSON
    进阶策略
      分步指令分解
      否定指令避开
      格式约束
      温度与Top-P调参
    实战模板
      代码审查 Prompt
      文档摘要 Prompt
      数据分析 Prompt
      翻译润色 Prompt
    评估迭代
      A/B 测试
      评分标准设计
      Prompt 版本管理
```

', content_markdown)
WHERE id = 68;

INSERT IGNORE INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(68, 'OpenAI Prompt Engineering Guide', 'LINK', 'https://platform.openai.com/docs/guides/prompt-engineering', 'OpenAI 官方 Prompt Engineering 最佳实践指南', 1, NOW()),
(68, 'Anthropic Prompt Engineering 文档', 'LINK', 'https://docs.anthropic.com/en/docs/build-with-claude/prompt-engineering', 'Claude Prompt Engineering 完整指南与示例', 2, NOW()),
(68, 'LangSmith Prompt Hub', 'LINK', 'https://smith.langchain.com/hub', '社区共享的高质量 Prompt 模板库', 3, NOW()),
(68, 'Learn Prompting 教程', 'LINK', 'https://learnprompting.org/zh-Hans/docs/category/-prompt-engineering', '免费 Prompt Engineering 交互式教程', 4, NOW()),
(68, '提示词编写示例集', 'LINK', 'https://github.com/f/awesome-chatgpt-prompts', 'GitHub 热门 ChatGPT Prompt 合集', 5, NOW());


-- ============================================
-- 课程3：构建基于RAG的知识库应用 (id=69)
-- ============================================
UPDATE learning_path_course SET content_markdown = CONCAT(
'```mermaid
mindmap
  root((RAG 检索增强生成))
    原理
      LLM 知识截止问题
      私有数据无法访问
      检索+生成两阶段
    文档处理
      格式解析 PDF/MD
      文本分块策略
      元数据提取
    向量化
      Embedding 模型
      向量维度与距离
      批量嵌入
    向量数据库
      Chroma / Pinecone
      Milvus / Qdrant
      FAISS 本地方案
    检索生成
      相似度检索 Top-K
      Context 注入
      引用溯源
```

', content_markdown)
WHERE id = 69;

INSERT IGNORE INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(69, 'LangChain RAG 官方教程', 'LINK', 'https://python.langchain.com/docs/tutorials/rag/', 'LangChain 官方 RAG 构建教程，含完整代码', 1, NOW()),
(69, 'Chroma 向量数据库文档', 'LINK', 'https://docs.trychroma.com/', '轻量级本地向量数据库，适合学习和原型开发', 2, NOW()),
(69, 'OpenAI Embeddings 指南', 'LINK', 'https://platform.openai.com/docs/guides/embeddings', 'Embedding 模型使用指南与最佳实践', 3, NOW()),
(69, 'RAG 从入门到生产实践', 'LINK', 'https://zhuanlan.zhihu.com/p/16623938646', '知乎深度好文：RAG 落地的 10 个关键问题', 4, NOW()),
(69, 'Milvus 向量数据库', 'LINK', 'https://milvus.io/docs', '生产级分布式向量数据库，适合企业场景', 5, NOW());


-- ============================================
-- 课程4：Agent开发与工具调用 (id=70)
-- ============================================
UPDATE learning_path_course SET content_markdown = CONCAT(
'```mermaid
mindmap
  root((Agent 智能体开发))
    Agent 概念
      感知-思考-行动循环
      LLM 作为"大脑"
      工具作为"手脚"
    Function Calling
      工具定义 Schema
      参数绑定与校验
      结果回传
    工具类型
      搜索工具
      计算工具
      API 调用工具
      数据库查询工具
    Agent 框架
      OpenAI Assistants API
      LangChain Agent
      AutoGen / CrewAI
      MCP 协议
    生产化
      有限状态机控制
      人工审批节点
      错误恢复策略
```

', content_markdown)
WHERE id = 70;

INSERT IGNORE INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(70, 'OpenAI Function Calling 官方指南', 'LINK', 'https://platform.openai.com/docs/guides/function-calling', 'Function Calling 官方文档与示例', 1, NOW()),
(70, 'LangChain Agent 教程', 'LINK', 'https://python.langchain.com/docs/tutorials/agents/', 'LangChain Agent 构建完整教程', 2, NOW()),
(70, 'Anthropic Tool Use 文档', 'LINK', 'https://docs.anthropic.com/en/docs/build-with-claude/tool-use', 'Claude Tool Use（工具调用）官方文档', 3, NOW()),
(70, 'MCP 协议规范', 'LINK', 'https://modelcontextprotocol.io/', 'Model Context Protocol 官方文档与 SDK', 4, NOW()),
(70, 'AutoGen 框架', 'LINK', 'https://microsoft.github.io/autogen/', '微软多 Agent 协作框架', 5, NOW());


-- ============================================
-- 课程5：应用后端架构与API设计 (id=71)
-- ============================================
UPDATE learning_path_course SET content_markdown = CONCAT(
'```mermaid
mindmap
  root((后端架构与 API))
    框架选择
      FastAPI Python
      Spring Boot Java
      Node.js Express
    API 设计
      RESTful 规范
      请求/响应模型
      Streaming 响应
      版本管理
    核心功能
      用户认证 JWT
      会话管理
      速率限制
      Token 用量追踪
    异步处理
      async/await
      任务队列 Celery
      WebSocket 实时推送
    AI 集成
      LLM 客户端封装
      重试与退避策略
      流式输出 SSE
      Prompt 模板管理
```

', content_markdown)
WHERE id = 71;

INSERT IGNORE INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(71, 'FastAPI 官方文档', 'LINK', 'https://fastapi.tiangolo.com/', 'FastAPI 框架官方文档（含中文翻译）', 1, NOW()),
(71, 'JWT 认证最佳实践', 'LINK', 'https://jwt.io/introduction', 'JSON Web Token 机制详解与调试工具', 2, NOW()),
(71, 'Celery 分布式任务队列', 'LINK', 'https://docs.celeryq.dev/en/stable/', 'Python 异步任务队列，适用于 AI 请求异步处理', 3, NOW()),
(71, 'SSE (Server-Sent Events) MDN 文档', 'LINK', 'https://developer.mozilla.org/zh-CN/docs/Web/API/Server-sent_events', '流式输出技术标准，用于 AI 逐字返回', 4, NOW()),
(71, 'Postman API 调试工具', 'LINK', 'https://www.postman.com/', 'API 调试与测试工具，支持自动生成文档', 5, NOW());


-- ============================================
-- 课程6：部署、监控与优化实战 (id=72)
-- ============================================
UPDATE learning_path_course SET content_markdown = CONCAT(
'```mermaid
mindmap
  root((部署监控与优化))
    容器化
      Dockerfile 编写
      Docker Compose
      多阶段构建
      镜像优化
    云服务部署
      Vercel Serverless
      AWS ECS / Lambda
      阿里云函数计算
      Railway / Render
    监控体系
      Token 消耗统计
      响应延迟追踪
      错误率告警
      LangSmith Trace
    性能优化
      Prompt 缓存 Redis
      模型 distillation
      批量推理
      KV Cache 复用
    CI/CD
      GitHub Actions
      自动化测试
      蓝绿部署
```

', content_markdown)
WHERE id = 72;

INSERT IGNORE INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(72, 'Docker 从入门到实践', 'LINK', 'https://yeasy.gitbook.io/docker_practice/', '中文 Docker 教程，涵盖基础到进阶', 1, NOW()),
(72, 'GitHub Actions 官方文档', 'LINK', 'https://docs.github.com/zh/actions', 'CI/CD 自动化流水线配置指南', 2, NOW()),
(72, 'LangSmith 监控平台', 'LINK', 'https://smith.langchain.com/', 'LLM 应用调试与监控平台，追踪每次调用', 3, NOW()),
(72, 'Vercel AI SDK 部署指南', 'LINK', 'https://sdk.vercel.ai/docs/guides/providers', 'AI 应用一键部署到 Vercel Serverless', 4, NOW()),
(72, 'Redis 缓存最佳实践', 'LINK', 'https://redis.io/docs/manual/', 'Redis 官方文档，用于 Prompt 缓存加速', 5, NOW());


-- ============================================
-- 更新路径的学习路径的课程数统计
-- ============================================
UPDATE learning_path SET course_count = (SELECT COUNT(*) FROM learning_path_course WHERE path_id = 8) WHERE id = 8;
