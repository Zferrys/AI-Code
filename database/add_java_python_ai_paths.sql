-- ============================================
-- 新增学习路径：Java+AI 企业级应用开发实战
-- 难度：INTERMEDIATE 预计天数：90
-- ============================================

INSERT INTO learning_path (title, description, difficulty, estimated_days, cover_image, course_count, status, create_time, update_time)
VALUES ('Java+AI 企业级应用开发实战', '从Java后端开发者转型AI应用工程师的系统路线。聚焦Spring AI框架，覆盖大模型集成、Tool Calling、RAG知识库、Agent智能体等核心能力，最终实战构建企业级智能客服系统。适合有Java基础、希望进入AI应用开发领域的开发者。', 'INTERMEDIATE', 90, NULL, 0, 'DRAFT', NOW(), NOW());

SET @path_java_ai = LAST_INSERT_ID();

-- ========== 课程1：Java AI 开发概述与环境搭建 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程1：Java AI 开发生态概览', '了解Java在AI领域的定位，对比Python与Java的AI开发差异，搭建Spring AI开发环境，完成第一个AI对话接口。', 1, 'ARTICLE', 60,
'```mermaid
mindmap
  root((Java AI 开发生态))
    Java 在 AI 中的定位
      企业后端存量巨大
      Python做实验 Java做生产
      AI应用工程师非算法工程师
    主流框架对比
      Spring AI 首选
      LangChain4j 备选
      Spring AI Alibaba 国内扩展
    环境搭建
      JDK 17+ / Spring Boot 3.x
      Maven / Gradle
      Spring AI Starter
    第一个对话
      配置 Model Endpoint
      ChatClient 调用
      流式 Streaming 输出
      错误处理与重试
    AI 与传统开发的区别
      从确定逻辑到概率输出
      Prompt 即代码
      需要评测与兜底
```

## 学习目标
理解Java在AI应用开发中的核心定位，掌握Spring AI框架的基本概念，成功搭建开发环境并完成第一个AI对话接口。

## 核心知识点

### Java + AI 的技术栈定位
2025-2026年，Java在AI领域的核心优势是**工程化能力**和**存量生态**。全球超80%的企业后端运行在Java上，AI能力直接嵌入现有系统是主流方向。

> **"Python 适合做实验，Java 适合做生产。"**

这意味着Java开发者不需要转算法工程师，而是成为 **"AI应用工程师"**——用模型开发应用，而非训练模型。

### Spring AI 框架简介
Spring AI 是 Spring 官方推出的 AI 抽象层，统一对接各大模型厂商的 API，与 Spring Boot 生态无缝集成。

核心组件：
- **ChatClient**：对话客户端，支持同步和流式调用
- **ChatModel**：模型抽象，支持 OpenAI、DeepSeek、通义千问等
- **Prompt**：提示词封装，支持模板化
- **Tool Calling**：函数调用能力，让AI调用Java方法

### 环境搭建示例

首先创建Spring Boot项目，添加依赖：

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    <version>1.0.0-M6</version>
</dependency>
```

配置文件 `application.yml`：

```yaml
spring:
  ai:
    openai:
      base-url: https://api.deepseek.com
      api-key: ${AI_API_KEY}
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7
```

第一个AI接口：

```java
@RestController
@RequestMapping("/ai")
public class AIController {
    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }
}
```

### 流式输出（SSE）
```java
@GetMapping("/chat/stream")
public Flux<String> chatStream(@RequestParam String message) {
    return chatClient.prompt()
        .user(message)
        .stream()
        .content();
}
```

## 小结
本章建立了Java AI开发的宏观视野。Spring AI 框架屏蔽了底层模型差异，让Java开发者可以用熟悉的 Spring Boot 方式开发AI应用。下一步将深入框架核心能力。

## 实践练习
1. 创建Spring Boot项目，集成Spring AI
2. 对接DeepSeek或通义千问API
3. 实现一个流式对话接口
4. 对比不同 temperature 参数对输出的影响
',
NOW());

SET @course_1 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_1, 'Spring AI 官方文档', 'LINK', 'https://docs.spring.io/spring-ai/reference/', 'Spring AI 框架官方参考文档，含快速入门和API参考', 1, NOW()),
(@course_1, 'DeepSeek API 文档', 'LINK', 'https://platform.deepseek.com/docs', 'DeepSeek 大模型 API 调用指南（国产免费方案）', 2, NOW()),
(@course_1, 'Spring Boot 3.x 官方文档', 'LINK', 'https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/', 'Spring Boot 3.x 官方参考文档', 3, NOW()),
(@course_1, 'Java开发者AI转型指南', 'LINK', 'https://developer.baidu.com/article/detail.html?id=7090263', '百度开发者社区：Java 转 AI 完整路径', 4, NOW()),
(@course_1, 'Spring Initializr 项目生成器', 'LINK', 'https://start.spring.io/', '在线生成 Spring Boot 项目脚手架', 5, NOW());


-- ========== 课程2：Spring AI 框架核心 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程2：Spring AI 框架核心深入', '深入学习ChatClient、ChatModel、Prompt模板、OutputParser等核心组件，掌握多模型切换和参数调优。', 2, 'CODING', 90,
'```mermaid
mindmap
  root((Spring AI 核心))
    ChatClient
      同步调用 call()
      流式调用 stream()
      System Prompt 设置
      历史消息管理
    ChatModel
      OpenAI / DeepSeek
      通义千问 / 文心一言
      多模型动态切换
    Prompt 模板
      SpEL 表达式
      参数注入
      模板文件管理
    OutputParser
      结构化输出 Bean
      JSON 解析
      错误重试策略
    高级配置
      Temperature 调参
      Top-P / Max Tokens
      超时与重试
      Token 用量统计
```

## 学习目标
深入掌握Spring AI框架的核心组件，能够灵活配置多模型、构建复杂Prompt、解析结构化输出。

## 核心知识点

### ChatClient 的三种用法

**1. 基本对话：**
```java
String response = chatClient.prompt()
    .user("解释什么是RAG")
    .call()
    .content();
```

**2. 带系统消息的对话：**
```java
String response = chatClient.prompt()
    .system("你是一个资深Java架构师，用中文回答")
    .user("如何设计微服务架构？")
    .call()
    .content();
```

**3. 流式对话：**
```java
Flux<String> stream = chatClient.prompt()
    .user("写一个冒泡排序")
    .stream()
    .content();
```

### 多模型切换

通过不同Bean名称配置多个模型客户端：

```java
@Configuration
public class AIConfig {
    @Bean("deepseek")
    public ChatClient deepseekClient(ChatClient.Builder builder) {
        return builder.build();
    }

    @Bean("tongyi")
    public ChatClient tongyiClient() {
        // 阿里云通义千问配置
        return ChatClient.builder()
            .build();
    }
}
```

### Prompt 模板

```java
@Value("classpath:prompts/code-review.st")
private Resource promptTemplate;

public String codeReview(String code) {
    return chatClient.prompt()
        .user(u -> u.text(promptTemplate)
            .param("code", code))
        .call()
        .content();
}
```

模板文件 `prompts/code-review.st`：
```
请审查以下 Java 代码：
{code}
从代码质量、潜在Bug、安全漏洞三个方面给出评价。
```

### 结构化输出（OutputParser）

```java
public record CodeReviewResult(int quality, List<String> bugs, List<String> suggestions) {}

CodeReviewResult result = chatClient.prompt()
    .user("审查这段代码：" + code)
    .call()
    .entity(CodeReviewResult.class);
```

## 小结
Core API 是构建所有AI应用的基础。掌握 ChatClient 的多种调用模式、Prompt 模板化、结构化输出解析，是后续开发RAG和Agent的前提。

## 实践练习
1. 创建两个不同的模型客户端（DeepSeek + 通义千问）
2. 实现一个带系统消息的代码审查接口
3. 使用OutputParser解析结构化响应
4. 实现一个动态Prompt模板
',
NOW());

SET @course_2 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_2, 'Spring AI ChatClient 参考文档', 'LINK', 'https://docs.spring.io/spring-ai/reference/api/chatclient.html', 'Spring AI ChatClient API 详细文档', 1, NOW()),
(@course_2, 'Spring AI Prompt Template', 'LINK', 'https://docs.spring.io/spring-ai/reference/api/prompt.html', 'Prompt 模板化开发指南', 2, NOW()),
(@course_2, 'Spring AI OutputParser', 'LINK', 'https://docs.spring.io/spring-ai/reference/api/output.html', '结构化输出解析（BeanOutputConverter）', 3, NOW()),
(@course_2, 'OpenAI API 参数详解', 'LINK', 'https://platform.openai.com/docs/api-reference/chat/create', 'Temperature/TopP/MaxTokens 等参数含义', 4, NOW()),
(@course_2, 'Spring AI 多模型配置示例', 'LINK', 'https://github.com/spring-projects/spring-ai/tree/main/models', 'GitHub 官方示例：多模型配置', 5, NOW());


-- ========== 课程3：大模型API集成与流式输出 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程3：大模型 API 集成与流式输出', '对接多个大模型厂商API，实现动态模型路由、流式SSE输出、错误重试与回退机制。', 3, 'CODING', 90,
'```mermaid
mindmap
  root((API 集成与流式))
    多厂商对接
      OpenAI 协议兼容
      DeepSeek 国内首选
      通义千问 阿里云
      文心一言 百度
    流式输出 SSE
      Spring WebFlux
      SseEmitter
      前端 EventSource
      逐字返回
    错误处理
      超时重试策略
      指数退避
      熔断降级
      模型回退链
    成本控制
      Token 计数
      按模型定价
      用量监控
      缓存重复请求
```

## 学习目标
掌握多模型接入和动态路由能力，实现生产级的流式输出和错误处理机制。

## 核心知识点

### 多模型动态路由

```java
@Service
public class ModelRouter {
    private final Map<String, ChatClient> clients = new ConcurrentHashMap<>();

    public ChatClient getClient(String model) {
        return clients.getOrDefault(model, clients.get("default"));
    }

    // 根据任务复杂度自动选择
    public ChatClient autoSelect(String task) {
        if (task.length() > 1000) return clients.get("deepseek");  // 复杂任务
        return clients.get("fast");  // 简单任务
    }
}
```

### SSE 流式输出（服务端）

```java
@GetMapping("/chat/stream")
public SseEmitter chatStream(@RequestParam String message) {
    SseEmitter emitter = new SseEmitter(180_000L); // 3分钟超时
    chatClient.prompt()
        .user(message)
        .stream()
        .content()
        .subscribe(
            content -> emitter.send(SseEmitter.event().data(content)),
            emitter::completeWithError,
            emitter::complete
        );
    return emitter;
}
```

### 前端接收
```javascript
const eventSource = new EventSource(`/api/ai/chat/stream?message=你好`);
eventSource.onmessage = (event) => {
    document.getElementById("output").textContent += event.data;
};
```

### 重试与回退机制
```java
@Component
public class AIRetryTemplate {
    @Retryable(
        retryFor = {SocketTimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String chatWithRetry(String message) {
        return chatClient.prompt().user(message).call().content();
    }

    @Recover
    public String fallback(Exception e, String message) {
        return "⚠️ 当前服务繁忙，已切换备用模型：" + fallbackClient.prompt()
            .user(message).call().content();
    }
}
```

## 小结
生产级的AI应用必须处理好模型切换、流式输出和错误恢复三个关键点。流式输出提升用户体验，多模型路由保证可用性，重试机制确保稳定性。

## 实践练习
1. 同时对接 DeepSeek 和 通义千问 两个模型
2. 实现基于 SSE 的流式对话接口
3. 实现指数退避重试机制
4. 配置一个备用模型作为降级方案
',
NOW());

SET @course_3 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_3, 'Spring WebFlux 官方文档', 'LINK', 'https://docs.spring.io/spring-framework/reference/web/webflux.html', 'Spring 响应式编程和 SSE 支持', 1, NOW()),
(@course_3, 'OpenAI SSE 流式规范', 'LINK', 'https://platform.openai.com/docs/api-reference/streaming', 'OpenAI API Server-Sent Events 标准', 2, NOW()),
(@course_3, 'Spring Retry 官方文档', 'LINK', 'https://github.com/spring-projects/spring-retry', 'Spring 重试和回退机制', 3, NOW()),
(@course_3, '通义千问 API 文档', 'LINK', 'https://help.aliyun.com/zh/model-studio/', '阿里云通义千问大模型 API 文档', 4, NOW()),
(@course_3, '腾讯云 API 限流最佳实践', 'LINK', 'https://cloud.tencent.com/document/product/436/41257', 'API 限流和熔断策略设计', 5, NOW());


-- ========== 课程4：Tool Calling 工具调用开发 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程4：Tool Calling 工具调用开发', '学习如何让AI模型调用Java方法，连接数据库、第三方API，构建有"行动能力"的智能应用。', 4, 'CODING', 100,
'```mermaid
mindmap
  root((Tool Calling))
    原理
      Function Calling 机制
      模型选择工具
      参数注入
      结果回传
    Spring AI @Tool
      注解定义工具
      参数绑定
      返回值格式化
      错误处理
    实战工具
      数据库查询工具
      天气查询工具
      邮件发送工具
      文件操作工具
    Tool 管理
      按需注册
      权限控制
      审计日志
      限流保护
    高级模式
      并行工具调用
      工具链路编排
      人工审批节点
```

## 学习目标
掌握 Tool Calling 的核心原理和实现方式，能够开发各类业务工具，构建有行动能力的AI应用。

## 核心知识点

### @Tool 注解开发

```java
@Component
public class DatabaseTools {

    @Tool(description = "根据用户ID查询用户信息")
    public String getUserInfo(@P(description = "用户ID") Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return "用户不存在";
        return String.format("用户名：%s，邮箱：%s，角色：%s",
            user.getUsername(), user.getEmail(), user.getRole());
    }

    @Tool(description = "查询订单列表")
    public String listOrders(@P(description = "用户ID") Long userId,
                             @P(description = "页码，默认1") int page) {
        List<Order> orders = orderMapper.selectByUserId(userId, page);
        if (orders.isEmpty()) return "暂无订单";
        return orders.stream()
            .map(o -> String.format("订单#%d：¥%.2f - %s", o.getId(), o.getAmount(), o.getStatus()))
            .collect(Collectors.joining("\n"));
    }
}
```

### 在对话中使用工具

```java
@GetMapping("/assistant")
public String assistant(@RequestParam String message) {
    return chatClient.prompt()
        .user(message)
        .tools(new DatabaseTools(), new WeatherTools())
        .call()
        .content();
}
```

### 自定义Tool回调

```java
public interface ToolCallback {
    String getName();
    String getDescription();
    String call(String jsonArgs);
}

// 手动注册工具
ToolCallback weatherTool = ToolCallbackBuilder.create()
    .name("getWeather")
    .description("查询城市天气")
    .inputType(String.class)
    .method(city -> {
        return weatherService.getWeather(city);
    })
    .build();
```

### 工具权限控制

```java
@Component
public class SecuredToolExecutor {
    // 只读工具全部可用，写操作需要确认
    private final Set<String> readOnlyTools = Set.of("getUserInfo", "listOrders");
    private final Set<String> writeTools = Set.of("createOrder", "sendEmail");

    public boolean canExecute(String toolName, String role) {
        if (readOnlyTools.contains(toolName)) return true;
        if (writeTools.contains(toolName) && "ADMIN".equals(role)) return true;
        return false;
    }
}
```

## 小结
Tool Calling 是AI从"聊天"到"行动"的关键能力。通过 @Tool 注解，Java开发者可以零门槛让AI操作数据库、调用API、执行业务逻辑。这是构建Agent智能体的基础。

## 实践练习
1. 开发一个数据库查询工具（用户查询+订单查询）
2. 开发一个天气查询工具（调用外部API）
3. 在对话中组合使用多个工具
4. 实现工具的限流和权限控制
',
NOW());

SET @course_4 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_4, 'Spring AI Tool Calling 官方文档', 'LINK', 'https://docs.spring.io/spring-ai/reference/api/tools.html', 'Spring AI @Tool 注解使用指南', 1, NOW()),
(@course_4, 'OpenAI Function Calling 指南', 'LINK', 'https://platform.openai.com/docs/guides/function-calling', 'Function Calling 原理与最佳实践', 2, NOW()),
(@course_4, 'LangChain4j Tool 开发', 'LINK', 'https://docs.langchain4j.dev/tutorials/tools', 'LangChain4j Java AI 框架的Tool开发', 3, NOW()),
(@course_4, 'Spring AI 工具调用示例项目', 'LINK', 'https://github.com/spring-projects/spring-ai/tree/main/models/spring-ai-openai', 'GitHub 官方 Tool Calling 示例', 4, NOW()),
(@course_4, '企业级AI工具设计原则', 'LINK', 'https://blog.csdn.net/EnjoyEDU/article/details/160850586', 'Java开发者AI转型：工具设计最佳实践', 5, NOW());


-- ========== 课程5：RAG 知识库应用开发 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程5：RAG 知识库应用开发', '基于 Spring AI 的 RAG 能力，构建企业知识库问答系统，处理文档加载、向量化、检索增强等核心环节。', 5, 'CODING', 120,
'```mermaid
mindmap
  root((RAG 知识库))
    原理
      LLM 知识截止问题
      私有数据无法访问
      检索+生成两阶段
    Spring AI RAG
      DocumentReader
      DocumentTransformer
      VectorStore
      RetrievalAugmentor
    文档处理
      PDF/Word/Excel 解析
      文本分块策略
      元数据提取
    向量数据库
      PostgreSQL + pgvector
      Milvus / Chroma
      Redis Stack
    RAG 优化
      多路召回
      Re-ranking 重排序
      Hybrid 搜索
      Query 重写
```

## 学习目标
理解RAG原理，掌握在Spring AI中搭建知识库问答系统的完整流程。

## 核心知识点

### RAG 工作流程
```
用户提问 → 向量化查询 → 向量检索 → 召回相关片段
→ 注入Prompt → LLM生成 → 引用溯源回答
```

### Spring AI RAG 配置

```java
@Configuration
public class RAGConfig {

    @Bean
    public VectorStore vectorStore(JdbcClient jdbcClient) {
        // 使用 PostgreSQL + pgvector
        return new PgVectorStore(jdbcClient, new CosineDistance());
    }

    @Bean
    public RetrievalAugmentor augmentor(VectorStore vectorStore) {
        return RetrievalAugmentor.builder()
            .vectorStore(vectorStore)
            .topK(5)          // 检索最相关的5个片段
            .scoreThreshold(0.7) // 相似度阈值
            .build();
    }
}
```

### 文档导入流程

```java
@Service
public class DocumentService {

    public void importDocument(MultipartFile file) {
        // 1. 读取文档
        DocumentReader reader = new PdfDocumentReader(file.getInputStream());
        List<Document> docs = reader.read();

        // 2. 文本分块
        DocumentTransformer splitter =
            new TokenTextSplitter(500, 100); // 500字符一块，重叠100字符
        List<Document> chunks = splitter.transform(docs);

        // 3. 写入向量库
        vectorStore.add(chunks);
    }
}
```

### 带RAG的对话接口

```java
@GetMapping("/rag/ask")
public String ask(@RequestParam String question) {
    return chatClient.prompt()
        .user(question)
        .advisors(new QuestionAnswerAdvisor(vectorStore))
        .call()
        .content();
}
```

### 多路召回优化

```java
// 同时使用向量检索 + 关键词检索
List<Document> vectorResults = vectorStore.similaritySearch(query);
List<Document> keywordResults = keywordSearch(query);

// 合并去重
Map<String, Document> merged = new LinkedHashMap<>();
for (Document doc : vectorResults) merged.put(doc.getId(), doc);
for (Document doc : keywordResults) merged.put(doc.getId(), doc);

// Re-ranking
List<Document> ranked = reRanker.rerank(query, new ArrayList<>(merged.values()));
return ranked.subList(0, 5);
```

## 小结
RAG 是企业AI落地最广泛的技术模式。Spring AI 对 RAG 有完整的支持链，从文档读取到向量检索一步到位。关键是分块策略和检索质量。

## 实践练习
1. 搭建 PostgreSQL + pgvector 向量数据库
2. 实现 PDF 文档导入和分块
3. 构建基于 RAG 的知识库问答接口
4. 测试不同分块大小对检索质量的影响
',
NOW());

SET @course_5 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_5, 'Spring AI RAG 官方文档', 'LINK', 'https://docs.spring.io/spring-ai/reference/api/retrieval-augmented-generation.html', 'Spring AI RAG 完整开发指南', 1, NOW()),
(@course_5, 'pgvector 向量扩展文档', 'LINK', 'https://github.com/pgvector/pgvector', 'PostgreSQL 向量检索扩展，Java + SQL 实现语义搜索', 2, NOW()),
(@course_5, 'LangChain4j RAG 教程', 'LINK', 'https://docs.langchain4j.dev/tutorials/rag', 'Java版 LangChain RAG 实现', 3, NOW()),
(@course_5, 'RAG 技术深度分析', 'LINK', 'https://zhuanlan.zhihu.com/p/16623938646', '知乎：RAG 落地的 10 个关键问题', 4, NOW()),
(@course_5, 'Milvus 向量数据库 Java SDK', 'LINK', 'https://milvus.io/docs/java-java.md', 'Milvus 分布式向量数据库 Java 接入指南', 5, NOW());


-- ========== 课程6：AI Agent 智能体开发 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程6：AI Agent 智能体开发', '实现自主规划-执行-观察循环的AI Agent，构建多Agent协作系统，掌握ReAct模式和MCP协议。', 6, 'CODING', 120,
'```mermaid
mindmap
  root((AI Agent 开发))
    Agent 架构
      感知-思考-行动循环
      LLM 作为大脑
      工具作为手脚
    核心模式
      ReAct 推理-行动
      Plan-and-Execute
      Reflection 反思
    多 Agent 协作
      Master-Slave 架构
      Pipeline 链式
      Debate 辩论
    MCP 协议
      Server-Client 模型
      资源暴露
      工具注册
    生产化
      有限状态机
      人工审批节点
      超时控制
      错误恢复
```

## 学习目标
掌握AI Agent的核心架构和实现模式，能够构建单Agent和多Agent协作系统。

## 核心知识点

### ReAct Agent 实现

```java
@Component
public class ReActAgent {

    public String execute(String task) {
        int maxSteps = 10;
        StringBuilder thought = new StringBuilder();

        for (int i = 0; i < maxSteps; i++) {
            // 思考：决定下一步做什么
            String response = chatClient.prompt()
                .user(formatReActPrompt(task, thought.toString()))
                .tools(allTools)
                .call()
                .content();

            // 解析响应，判断是否完成
            if (response.contains("FINAL_ANSWER:")) {
                return extractFinalAnswer(response);
            }

            thought.append(response).append("\n");
        }
        return "任务超时，已执行最大步骤数";
    }
}
```

### 多Agent协作

```java
// Master Agent 负责任务分解和结果汇总
// Worker Agents 负责具体执行
public class MultiAgentSystem {

    public String executeComplexTask(String task) {
        // 1. Master Agent 拆解任务
        List<String> subtasks = masterAgent.decompose(task);

        // 2. 并行执行子任务
        List<String> results = subtasks.parallelStream()
            .map(st -> workerAgent.execute(st))
            .toList();

        // 3. Master Agent 汇总结果
        return masterAgent.summarize(results);
    }
}
```

### MCP 协议集成

```java
// MCP Server：暴露Java服务为AI可调用的工具
@SpringBootApplication
public class McpServerApplication {

    @Bean
    public ToolProvider orderTools(OrderService orderService) {
        return ToolProvider.builder()
            .tool("queryOrder", orderService::queryOrder)
            .tool("createOrder", orderService::createOrder)
            .tool("cancelOrder", orderService::cancelOrder)
            .build();
    }
}
```

## 小结
Agent 是2026年AI开发最热门的方向。从单Agent的ReAct模式到多Agent协作系统，再到MCP协议标准化工具暴露，Java生态正在快速完善Agent开发能力。

## 实践练习
1. 实现一个 ReAct 模式的计算 Agent（加减乘除 + 搜索）
2. 实现一个信息查询 Agent（查天气+查新闻+查百科）
3. 构建 Master-Worker 多Agent系统
4. 用 Spring AI 搭建 MCP Server
',
NOW());

SET @course_6 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_6, 'Spring AI Agent 官方文档', 'LINK', 'https://docs.spring.io/spring-ai/reference/api/agent.html', 'Spring AI Agent 开发指南', 1, NOW()),
(@course_6, 'MCP 协议官方文档', 'LINK', 'https://modelcontextprotocol.io/', 'Model Context Protocol 规范与 SDK', 2, NOW()),
(@course_6, 'LangChain4j Agent 教程', 'LINK', 'https://docs.langchain4j.dev/tutorials/chat-with-tools', 'Java Agent 开发实战教程', 3, NOW()),
(@course_6, 'ReAct 模式论文', 'LINK', 'https://arxiv.org/abs/2210.03629', 'ReAct: Synergizing Reasoning and Acting in LLM', 4, NOW()),
(@course_6, 'Multi-Agent 架构模式', 'LINK', 'https://blog.csdn.net/EnjoyEDU/article/details/160850586', 'Java开发者AI转型：Agent 架构设计', 5, NOW());


-- ========== 课程7：企业级AI安全与监控 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程7：企业级 AI 应用安全与监控', '掌握AI应用的安全防护、提示词注入防护、Token追踪、日志审计和性能监控。', 7, 'ARTICLE', 80,
'```mermaid
mindmap
  root((AI 安全与监控))
    安全防护
      Prompt 注入检测
      输出内容过滤
      敏感信息脱敏
      用户权限校验
    审计日志
      每次对话记录
      Token 消耗追踪
      工具调用日志
      异常行为告警
    性能监控
      响应延迟 P99
      TPS 吞吐量
      错误率监控
      模型成本核算
    限流保护
      用户级别限流
      API 级别限流
      令牌桶算法
      降级与熔断
```

## 学习目标
掌握AI应用生产化必备的安全防护和监控运维能力。

## 核心知识点

### Prompt 注入防护

```java
@Component
public class PromptGuard {
    private final List<String> blockedPatterns = List.of(
        "忽略之前的指令", "system prompt", "你是",
        "扮演", "忘记你之前的"
    );

    public boolean isSafe(String input) {
        String lower = input.toLowerCase();
        return blockedPatterns.stream().noneMatch(lower::contains);
    }
}
```

### Token 用量追踪

```java
@Component
public class TokenTracker {

    @EventListener
    public void onTokenUsage(TokenUsageEvent event) {
        TokenUsage usage = event.getSource();
        log.info("对话消耗: prompt={}tokens, completion={}tokens, cost={}元",
            usage.getPromptTokens(),
            usage.getCompletionTokens(),
            calculateCost(usage));
    }
}
```

### 性能监控（Micrometer）

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,metrics
  metrics:
    tags:
      application: ai-app
```

自定义指标：

```java
@RestController
public class MetricsController {
    private final Counter aiRequestCounter =
        Metrics.counter("ai.requests.total");
    private final Timer aiResponseTimer =
        Metrics.timer("ai.response.duration");

    public String chat(String message) {
        aiRequestCounter.increment();
        return aiResponseTimer.record(() ->
            chatClient.prompt().user(message).call().content());
    }
}
```

## 小结
AI应用的安全和监控是生产级部署的必备能力。Prompt注入防护、Token追踪和性能监控这三个方面缺一不可。

## 实践练习
1. 实现 Prompt 注入检测过滤器
2. 集成 Micrometer + Prometheus 监控
3. 实现基于令牌桶的限流
4. 搭建 Grafana 监控看板
',
NOW());

SET @course_7 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_7, 'OWASP LLM Top 10', 'LINK', 'https://owasp.org/www-project-top-10-for-llm-applications/', 'LLM 应用安全风险 Top 10 权威指南', 1, NOW()),
(@course_7, 'Spring Boot Actuator 监控', 'LINK', 'https://docs.spring.io/spring-boot/docs/current/actuator/htmlsingle/', 'Spring Boot 生产级监控端点配置', 2, NOW()),
(@course_7, 'Micrometer 文档', 'LINK', 'https://micrometer.io/docs', '应用指标收集框架，支持 Prometheus/Grafana', 3, NOW()),
(@course_7, 'Prompt 注入攻击与防御', 'LINK', 'https://learnprompting.org/zh-Hans/docs/category/-prompt-hacking', 'Prompt Hacking 攻防技术详解', 4, NOW()),
(@course_7, 'Spring Cloud Sentinel 限流', 'LINK', 'https://sentinelguard.io/zh-cn/docs/quick-start.html', '阿里巴巴开源限流熔断框架', 5, NOW());


-- ========== 课程8：实战项目：智能客服系统 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_java_ai, '课程8：实战项目：企业智能客服系统', '综合运用 Spring AI + RAG + Agent 能力，构建一个完整的企业级智能客服系统，含工单流转、知识库检索、人工接管。', 8, 'CODING', 150,
'```mermaid
mindmap
  root((智能客服系统))
    系统架构
      Spring Boot 后端
      WebSocket 实时通信
      消息队列解耦
      RAG 知识库查询
    核心功能
      多轮对话管理
      知识库检索回复
      工单自动创建
      转人工服务
    技术实现
      Chat Memory 会话记忆
      情感识别与优先级
      Agent 工具调用
      WebSocket 推送
    部署运维
      Docker 容器化
      Nginx 反向代理
      Prometheus 监控
      ELK 日志分析
```

## 学习目标
综合运用课程1-7的知识，独立完成一个企业级智能客服系统的设计与开发。

## 项目架构

```
用户 → Nginx → Spring Boot API Gateway
                       ↓
              ┌─────────────────┐
              │   Chat Service   │ ← Session Manager
              │   RAG Service    │ ← Vector Store
              │   Agent Service  │ ← Tool Registry
              │   Ticket Service │ ← 工单系统
              └─────────────────┘
                       ↓
              ┌─────────────────┐
              │   Message Queue  │ → 异步日志/分析
              │    (RabbitMQ)    │
              └─────────────────┘
```

### 核心接口设计

```java
@RestController
@RequestMapping("/api/customer-service")
public class CustomerServiceController {

    // 多轮对话
    @PostMapping("/chat")
    public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
        return chatService.processMessage(request);
    }

    // 创建工单
    @PostMapping("/ticket")
    public ApiResponse<Ticket> createTicket(@RequestBody TicketRequest request) {
        return ApiResponse.success(ticketService.create(request));
    }

    // 转人工
    @PostMapping("/transfer")
    public ApiResponse<Void> transferToHuman(@RequestParam String sessionId) {
        chatService.transferToHuman(sessionId);
        return ApiResponse.success();
    }
}
```

### Docker Compose 部署

```yaml
version: "3.8"
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
    environment:
      SPRING_AI_OPENAI_API_KEY: ${AI_API_KEY}

  postgres:
    image: pgvector/pgvector:pg16
    environment:
      POSTGRES_DB: knowledge_base

  redis:
    image: redis:7-alpine

  prometheus:
    image: prom/prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
```

## 项目要求
1. 支持多轮对话（记忆最近20轮）
2. RAG知识库支持产品文档/FAQ检索
3. Agent自动创建工单/查询订单
4. 情感分析识别客户情绪
5. 支持人工接管
6. 完整的日志和监控

## 小结
智能客服是RAG+Agent最典型的落地场景。通过本项目，你将完整经历AI应用的需求分析、架构设计、编码实现和部署运维全流程。
',
NOW());

SET @course_8 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@course_8, 'Spring AI 智能客服示例项目', 'LINK', 'https://github.com/spring-projects/spring-ai/tree/main/spring-ai-docs/src/main/antora/modules/ROOT/examples', 'Spring AI 官方示例：对话应用', 1, NOW()),
(@course_8, 'WebSocket Spring 官方指南', 'LINK', 'https://docs.spring.io/spring-framework/reference/web/websocket.html', 'Spring WebSocket 实现实时通信', 2, NOW()),
(@course_8, 'RabbitMQ Java 客户端文档', 'LINK', 'https://www.rabbitmq.com/tutorials/tutorial-one-java.html', '消息队列解耦异步任务', 3, NOW()),
(@course_8, 'Docker Compose 官方文档', 'LINK', 'https://docs.docker.com/compose/', '多容器应用编排部署', 4, NOW()),
(@course_8, 'ELK Stack 日志分析', 'LINK', 'https://www.elastic.co/guide/en/elastic-stack/current/index.html', 'Elasticsearch + Logstash + Kibana 日志系统', 5, NOW());


-- ============================================
-- 更新课程数
-- ============================================
UPDATE learning_path SET course_count = 8 WHERE id = @path_java_ai;


-- ============================================
-- 新增学习路径：Python+AI 全栈开发从入门到项目
-- 难度：BEGINNER 预计天数：120
-- ============================================

INSERT INTO learning_path (title, description, difficulty, estimated_days, cover_image, course_count, status, create_time, update_time)
VALUES ('Python+AI 全栈开发从入门到项目', '从零开始掌握Python AI开发全栈技能。覆盖Python基础、数据处理、深度学习、Transformer、RAG、Agent、模型微调、实战项目八大模块。适合有编程基础或零基础想进入AI领域的开发者。', 'BEGINNER', 120, NULL, 0, 'DRAFT', NOW(), NOW());

SET @path_python_ai = LAST_INSERT_ID();


-- ========== 课程1：Python AI 开发环境与基础 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程1：Python AI 开发环境与基础', '搭建Python AI开发环境，掌握Conda虚拟环境、Jupyter Notebook、科学计算库安装，完成第一个AI调用程序。', 1, 'ARTICLE', 60,
'```mermaid
mindmap
  root((Python AI 环境))
    开发工具
      Conda 环境管理
      Jupyter Notebook
      VS Code + Python
      Git 版本控制
    科学计算栈
      NumPy 数组计算
      Pandas 数据处理
      Matplotlib 可视化
      Scikit-learn 入门
    AI 依赖
      PyTorch / TensorFlow
      Transformers 库
      LangChain 框架
      HuggingFace Hub
    硬件要求
      CPU 方案（入门）
      GPU 方案（训练）
      Colab 免费 GPU
      Ollama 本地部署
    第一个 AI 程序
      调用 OpenAI API
      HuggingFace Pipeline
      本地模型推理
```

## 学习目标
搭建完整的Python AI开发环境，掌握科学计算栈基础，成功运行第一个AI程序。

## 核心知识点

### Conda 环境搭建
```bash
# 安装 Miniconda
wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh
bash Miniconda3-latest-Linux-x86_64.sh

# 创建 AI 环境
conda create -n ai python=3.11
conda activate ai

# 安装核心库
pip install numpy pandas matplotlib scikit-learn
pip install jupyter notebook
pip install torch torchvision --index-url https://download.pytorch.org/whl/cpu
pip install transformers datasets accelerate
```

### Jupyter Notebook 快速上手
```python
# 在 notebook 中交互式运行
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

# 创建数据
data = pd.DataFrame({
    "x": np.linspace(0, 10, 100),
    "y": np.sin(np.linspace(0, 10, 100))
})

# 可视化
plt.plot(data["x"], data["y"])
plt.title("正弦波")
plt.show()
```

### 第一个 AI 程序（HuggingFace Pipeline）
```python
from transformers import pipeline

# 加载情感分析模型
classifier = pipeline("sentiment-analysis", model="distilbert-base-uncased-finetuned-sst-2-english")

# 推理
result = classifier("I love learning AI!")
print(result)
# [{"label": "POSITIVE", "score": 0.9998}]
```

## 小结
Python AI开发环境搭建是入门的第一步。Conda管理环境、Jupyter交互式开发、HuggingFace快速调用预训练模型，这三板斧能让你快速进入AI开发状态。

## 实践练习
1. 安装 Miniconda 并创建 AI 开发环境
2. 运行 Jupyter Notebook 完成简单数据可视化
3. 使用 HuggingFace Pipeline 完成文本分类/情感分析
4. 对比 CPU 和 GPU（Colab）的推理速度差异
',
NOW());

SET @pc1 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc1, 'Miniconda 官方安装指南', 'LINK', 'https://docs.conda.io/en/latest/miniconda.html', 'Python 环境管理工具，AI 开发标配', 1, NOW()),
(@pc1, 'PyTorch 官方入门教程', 'LINK', 'https://pytorch.org/tutorials/', 'PyTorch 深度学习框架快速入门', 2, NOW()),
(@pc1, 'Google Colab 免费 GPU', 'LINK', 'https://colab.research.google.com/', '免费 Jupyter Notebook + GPU，零成本入门', 3, NOW()),
(@pc1, 'HuggingFace 快速入门', 'LINK', 'https://huggingface.co/docs/transformers/quicktour', 'Transformers 库 5 分钟快速上手', 4, NOW()),
(@pc1, '蓝山工作室 Python AI 课件 2025', 'LINK', 'https://github.com/LanshanTeam/Courseware-AI-Python-2025', 'Github 开源课程：编程通识 + Python 语法模块', 5, NOW());


-- ========== 课程2：Python 数据处理与可视化 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程2：Python 数据处理与可视化', '掌握NumPy和Pandas进行数据清洗、转换和分析，使用Matplotlib/Seaborn制作专业图表。', 2, 'CODING', 90,
'```mermaid
mindmap
  root((数据处理与可视化))
    NumPy
      多维数组 ndarray
      广播机制 Broadcast
      矩阵运算
      随机数生成
    Pandas
      DataFrame 操作
      数据清洗与过滤
      分组聚合 groupby
      多表连接 merge
    可视化
      Matplotlib 基础
      Seaborn 统计图
      Plotly 交互图
      Echarts 可视化
    实战案例
      销售数据分析
      用户行为分析
      时序趋势分析
      数据报表自动生成
```

## 学习目标
掌握Python数据科学生态的核心工具，能够独立完成数据清洗、分析和可视化。

## 核心知识点

### NumPy 基础
```python
import numpy as np

# 创建数组
arr = np.array([[1, 2, 3], [4, 5, 6]])
print(arr.shape)  # (2, 3)

# 广播运算
a = np.array([1, 2, 3])
b = np.array([10, 20, 30])
print(a * b)  # [10, 40, 90]

# 矩阵运算
matrix = np.random.randn(3, 3)
print(np.linalg.inv(matrix))  # 矩阵求逆
```

### Pandas 数据处理
```python
import pandas as pd

# 读取数据
df = pd.read_csv("sales_data.csv")

# 数据清洗
df = df.dropna()  # 删除空值
df["date"] = pd.to_datetime(df["date"])
df = df[df["amount"] > 0]  # 过滤异常值

# 分组统计
summary = df.groupby("category").agg({
    "amount": ["sum", "mean", "count"],
    "profit": "sum"
}).round(2)
```

## 小结
数据处理是AI应用的基石。NumPy提供高效数组运算，Pandas处理结构化数据。掌握这两者，就具备了AI数据准备能力。

## 实践练习
1. 用 NumPy 实现一个简单的神经网络前向传播
2. 加载 CSV 数据集进行清洗和统计分析
3. 使用 Matplotlib 绘制多子图对比报告
4. 用 Pandas 合并多个表格，生成透视表
',
NOW());

SET @pc2 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc2, 'NumPy 官方教程', 'LINK', 'https://numpy.org/doc/stable/user/quickstart.html', 'NumPy 数组计算快速入门', 1, NOW()),
(@pc2, 'Pandas 10分钟入门', 'LINK', 'https://pandas.pydata.org/docs/user_guide/10min.html', 'Pandas 官方 10 分钟快速教程', 2, NOW()),
(@pc2, 'Matplotlib 教程', 'LINK', 'https://matplotlib.org/stable/tutorials/index.html', 'Python 数据可视化库官方教程', 3, NOW()),
(@pc2, 'Kaggle 数据集平台', 'LINK', 'https://www.kaggle.com/datasets', '免费获取各类真实数据集用于练习', 4, NOW()),
(@pc2, 'Seaborn 统计可视化', 'LINK', 'https://seaborn.pydata.org/tutorial.html', '基于 Matplotlib 的高级统计图表库', 5, NOW());


-- ========== 课程3：深度学习基础与 PyTorch ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程3：深度学习基础与 PyTorch 入门', '从感知机到神经网络，使用PyTorch实现手写数字识别，理解前向传播、反向传播、损失函数等核心概念。', 3, 'CODING', 120,
'```mermaid
mindmap
  root((深度学习与PyTorch))
    神经网络基础
      感知机 Perceptron
      激活函数 ReLU/Sigmoid
      多层感知机 MLP
      损失函数 CrossEntropy
    PyTorch 核心
      张量 Tensor
      自动求导 Autograd
      模型构建 nn.Module
      优化器 Optimizer
    训练流程
      数据加载 DataLoader
      前向传播
      反向传播 backward
      参数更新 step
    实战项目
      MNIST 手写识别
      CIFAR-10 图像分类
      自定义数据集
      GPU 训练加速
```

## 学习目标
理解深度学习核心概念，掌握PyTorch框架的基本用法，独立训练一个图像分类模型。

## 核心知识点

### PyTorch 张量
```python
import torch

# 创建张量
x = torch.tensor([[1, 2], [3, 4]], dtype=torch.float32)
print(x.device)  # CPU

# 转移到 GPU
if torch.cuda.is_available():
    x = x.cuda()
    print(x.device)  # CUDA

# 自动求导
x = torch.tensor([2.0], requires_grad=True)
y = x ** 2 + 3 * x + 1
y.backward()
print(x.grad)  # 2*x + 3 = 7
```

### 一个完整的神经网络
```python
import torch.nn as nn
import torch.optim as optim

class SimpleNet(nn.Module):
    def __init__(self):
        super().__init__()
        self.fc1 = nn.Linear(784, 256)
        self.fc2 = nn.Linear(256, 128)
        self.fc3 = nn.Linear(128, 10)
        self.relu = nn.ReLU()

    def forward(self, x):
        x = x.view(-1, 784)  # 展平
        x = self.relu(self.fc1(x))
        x = self.relu(self.fc2(x))
        return self.fc3(x)

# 训练循环
model = SimpleNet()
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=0.001)

for epoch in range(10):
    for images, labels in train_loader:
        optimizer.zero_grad()
        outputs = model(images)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()
    print(f"Epoch {epoch+1}, Loss: {loss.item():.4f}")
```

## 小结
深度学习入门的关键是理解"张量计算 + 自动求导 + 梯度下降"这个三元组。PyTorch用直观的API封装了这些概念。手写数字识别是深度学习的"Hello World"。

## 实践练习
1. 用 PyTorch 实现一个 2 层神经网络
2. 在 MNIST 数据集上训练图像分类模型
3. 对比不同激活函数（ReLU/Sigmoid/Tanh）的效果
4. 使用 TensorBoard 可视化训练曲线
',
NOW());

SET @pc3 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc3, 'PyTorch 60分钟入门', 'LINK', 'https://pytorch.org/tutorials/beginner/deep_learning_60min_blitz.html', 'PyTorch 官方 60 分钟深度学习入门', 1, NOW()),
(@pc3, '蓝山工作室 深度学习基础课件', 'LINK', 'https://github.com/LanshanTeam/Courseware-AI-Python-2025', 'GitHub 开源课件：从感知机到多层网络', 2, NOW()),
(@pc3, 'MNIST 数据集介绍', 'LINK', 'http://yann.lecun.com/exdb/mnist/', '手写数字识别经典数据集', 3, NOW()),
(@pc3, 'PyTorch 官方教程中文版', 'LINK', 'https://pytorch.apachecn.org/', 'PyTorch 中文文档与教程', 4, NOW()),
(@pc3, 'TensorBoard 可视化指南', 'LINK', 'https://www.tensorflow.org/tensorboard/get_started', '训练过程可视化工具', 5, NOW());


-- ========== 课程4：Transformer 与 HuggingFace ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程4：Transformer 与 HuggingFace 实战', '理解Transformer架构原理，掌握HuggingFace生态，实现文本分类、命名实体识别、文本生成等NLP任务。', 4, 'CODING', 120,
'```mermaid
mindmap
  root((Transformer 与 HF))
    Transformer 架构
      自注意力 Self-Attention
      多头注意力 Multi-Head
      Encoder-Decoder
      Position Encoding
    BERT 家族
      BERT 双向编码
      RoBERTa 优化
      ALBERT 轻量
      DistilBERT 蒸馏
    GPT 家族
      GPT-2/3 生成
      ChatGLM 中文
      Bloom 多语言
      Llama 开源
    HuggingFace 生态
      Pipeline 快速推理
      Trainer 训练
      Datasets 数据集
      Hub 模型共享
    实战案例
      文本分类 BERT
      命名实体识别 NER
      文本摘要
      语义相似度
```

## 学习目标
理解Transformer架构原理，掌握HuggingFace生态的完整使用流程。

## 核心知识点

### HuggingFace Pipeline
```python
from transformers import pipeline

# 文本分类
classifier = pipeline("text-classification")
result = classifier("This course is amazing!")
print(result)

# 命名实体识别
ner = pipeline("ner", aggregation_strategy="simple")
result = ner("Apple Inc. was founded by Steve Jobs in Cupertino.")
for entity in result:
    print(f"{entity["word"]}: {entity["entity_group"]}")

# 文本生成
generator = pipeline("text-generation", model="gpt2")
result = generator("AI will change", max_length=50, num_return_sequences=1)
print(result[0]["generated_text"])
```

### 使用 Trainer API 微调
```python
from transformers import AutoModelForSequenceClassification, TrainingArguments, Trainer

model = AutoModelForSequenceClassification.from_pretrained("bert-base-chinese", num_labels=2)

training_args = TrainingArguments(
    output_dir="./results",
    num_train_epochs=3,
    per_device_train_batch_size=16,
    per_device_eval_batch_size=64,
    warmup_steps=500,
    logging_dir="./logs",
    evaluation_strategy="epoch",
    save_strategy="epoch",
)

trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=train_dataset,
    eval_dataset=eval_dataset,
)

trainer.train()
```

## 小结
Transformer是当代AI的基石架构。HuggingFace将其封装为易用的Pipeline和Trainer API。理解Self-Attention机制，掌握HuggingFace生态，就能完成绝大多数NLP任务。

## 实践练习
1. 使用 Pipeline 完成文本分类、NER、摘要任务
2. 加载 BERT 模型进行中文情感分类
3. 使用 Trainer API 微调一个文本分类模型
4. 探索 HuggingFace Hub 上的开源模型
',
NOW());

SET @pc4 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc4, 'HuggingFace 官方课程', 'LINK', 'https://huggingface.co/learn/nlp-course', 'HuggingFace NLP 课程，从入门到精通', 1, NOW()),
(@pc4, 'The Annotated Transformer', 'LINK', 'https://nlp.seas.harvard.edu/annotated-transformer/', 'Harvard NLP：Transformer 源码级解析', 2, NOW()),
(@pc4, 'BERT 论文中文解读', 'LINK', 'https://arxiv.org/abs/1810.04805', 'BERT: Pre-training of Deep Bidirectional Transformers', 3, NOW()),
(@pc4, '蓝山工作室 HF 课件', 'LINK', 'https://github.com/LanshanTeam/Courseware-AI-Python-2025', 'GitHub 课件：神经网络结构与HuggingFace模块', 4, NOW()),
(@pc4, 'HuggingFace Model Hub', 'LINK', 'https://huggingface.co/models', '40万+开源模型库，一键加载使用', 5, NOW());


-- ========== 课程5：RAG 应用开发实战 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程5：RAG 应用开发实战', '使用 LangChain + Chroma 构建基于 RAG 的知识库问答系统，掌握文档加载、分块、向量检索、增强生成全流程。', 5, 'CODING', 120,
'```mermaid
mindmap
  root((RAG 应用开发))
    LangChain 基础
      LCEL 表达式
      Chain 链式调用
      模型封装
      Prompt 模板
    文档处理
      Document Loaders
      文本分割 Splitter
      元数据提取
      Embedding 向量化
    向量存储
      Chroma 本地库
      FAISS 高效检索
      Pinecone 云端
      Qdrant 可扩展
    检索增强
      相似度检索
      多路召回 Fusion
      Re-ranking 排序
      Context 压缩
    实战项目
      PDF 问答系统
      网页知识库
      代码库检索
      多文档问答
```

## 学习目标
掌握 LangChain 框架，独立构建基于 RAG 的知识库问答系统。

## 核心知识点

### LangChain RAG 完整流程
```python
from langchain_community.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import Chroma
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain.chains import RetrievalQA

# 1. 加载文档
loader = PyPDFLoader("knowledge_base.pdf")
documents = loader.load()

# 2. 文本分块
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=500,
    chunk_overlap=100,
    separators=["\n\n", "\n", "。", " ", ""]
)
chunks = text_splitter.split_documents(documents)

# 3. 向量化并存储
embeddings = OpenAIEmbeddings()
vectorstore = Chroma.from_documents(
    documents=chunks,
    embedding=embeddings,
    persist_directory="./chroma_db"
)

# 4. 构建 RAG Chain
llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
qa_chain = RetrievalQA.from_chain_type(
    llm=llm,
    chain_type="stuff",
    retriever=vectorstore.as_retriever(search_kwargs={"k": 5})
)

# 5. 提问
answer = qa_chain.invoke("公司的核心产品有哪些特色功能？")
print(answer["result"])
```

### Streamlit 可交互界面
```python
import streamlit as st

st.title("📚 知识库问答系统")

if "messages" not in st.session_state:
    st.session_state.messages = []

for msg in st.session_state.messages:
    with st.chat_message(msg["role"]):
        st.markdown(msg["content"])

if prompt := st.chat_input("输入问题..."):
    st.session_state.messages.append({"role": "user", "content": prompt})
    with st.chat_message("user"):
        st.markdown(prompt)

    response = qa_chain.invoke(prompt)
    st.session_state.messages.append({"role": "assistant", "content": response["result"]})
    with st.chat_message("assistant"):
        st.markdown(response["result"])
```

## 小结
RAG是2025-2026年落地最多的AI应用模式。LangChain提供了完整的RAG开发工具链，从文档加载到检索增强一站式完成。Streamlit可以快速搭建可交互的Demo界面。

## 实践练习
1. 用 LangChain 加载 PDF 文档并构建问答系统
2. 测试不同分块策略对检索质量的影响
3. 添加 Streamlit 前端界面
4. 实现多路召回（向量检索 + BM25 关键词检索）
',
NOW());

SET @pc5 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc5, 'LangChain 官方 RAG 教程', 'LINK', 'https://python.langchain.com/docs/tutorials/rag/', 'LangChain RAG 构建官方教程', 1, NOW()),
(@pc5, 'Chroma 向量数据库文档', 'LINK', 'https://docs.trychroma.com/', '轻量级本地向量数据库，适合学习', 2, NOW()),
(@pc5, 'Streamlit 官方文档', 'LINK', 'https://docs.streamlit.io/', 'Python 快速构建 AI 应用界面的框架', 3, NOW()),
(@pc5, 'LangChain 中文入门教程', 'LINK', 'https://www.langchain.com.cn/', 'LangChain 中文社区翻译文档', 4, NOW()),
(@pc5, 'OpenAI Embeddings 指南', 'LINK', 'https://platform.openai.com/docs/guides/embeddings', '文本向量化最佳实践', 5, NOW());


-- ========== 课程6：AI Agent 与工具链 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程6：AI Agent 与工具链开发', '使用 LangChain/LangGraph 构建 AI Agent，实现工具调用、多步推理、多Agent协作，了解MCP协议。', 6, 'CODING', 120,
'```mermaid
mindmap
  root((AI Agent 开发))
    Agent 架构
      ReAct 模式
      工具调用 Tool
      记忆系统 Memory
      规划能力 Planner
    LangChain Agent
      AgentExecutor
      Tool 定义
      Agent 类型
      Chat History
    LangGraph
      状态图 StateGraph
      节点与边
      条件路由
      循环控制
    工具开发
      API 工具
      数据库工具
      搜索工具
      代码执行工具
    多 Agent 系统
      Supervisor Agent
      专用 Worker
      结果汇总
      人工审批
```

## 学习目标
掌握AI Agent的核心开发模式，使用LangChain和LangGraph构建功能完整的智能体系统。

## 核心知识点

### LangChain Agent 实现
```python
from langchain.agents import tool, AgentExecutor, create_react_agent
from langchain_openai import ChatOpenAI
from langchain_community.tools import DuckDuckGoSearchRun

@tool
def calculate(expression: str) -> str:
    """计算数学表达式"""
    return str(eval(expression))

@tool
def get_weather(city: str) -> str:
    """查询城市天气"""
    # 实际项目中调用天气 API
    return f"{city} 今天气温 22-28°C，晴"

tools = [calculate, get_weather, DuckDuckGoSearchRun()]
model = ChatOpenAI(model="gpt-4o-mini", temperature=0)

agent = create_react_agent(model, tools, prompt)
agent_executor = AgentExecutor(agent=agent, tools=tools, verbose=True)

result = agent_executor.invoke({"input": "北京和上海哪个更热？顺便算一下温差"})
print(result["output"])
```

### LangGraph 构建复杂流程
```python
from langgraph.graph import StateGraph, END
from typing import TypedDict, List

class AgentState(TypedDict):
    messages: List
    next_step: str

# 定义节点
def analyze(state: AgentState):
    # 分析用户意图
    return {"next_step": "search"}

def search(state: AgentState):
    # 检索信息
    return {"messages": search_result, "next_step": "generate"}

def generate(state: AgentState):
    # 生成最终回答
    return {"messages": final_answer, "next_step": END}

# 构建图
graph = StateGraph(AgentState)
graph.add_node("analyze", analyze)
graph.add_node("search", search)
graph.add_node("generate", generate)

graph.set_entry_point("analyze")
graph.add_conditional_edges("analyze", decide_next)
graph.add_edge("search", "generate")
graph.add_edge("generate", END)
```

## 小结
Agent是AI开发的前沿方向。LangChain提供了开箱即用的Agent实现，LangGraph支持更灵活的工作流编排。

## 实践练习
1. 使用 LangChain 创建一个带搜索和计算能力的 Agent
2. 自定义工具（查询数据库、调用外部 API）
3. 使用 LangGraph 构建多步骤分析流程
4. 实现一个带人工审核环节的 Agent 系统
',
NOW());

SET @pc6 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc6, 'LangChain Agent 官方教程', 'LINK', 'https://python.langchain.com/docs/tutorials/agents/', 'LangChain Agent 构建完整教程', 1, NOW()),
(@pc6, 'LangGraph 官方文档', 'LINK', 'https://langchain-ai.github.io/langgraph/', 'LangGraph 有状态 Agent 编排框架', 2, NOW()),
(@pc6, 'OpenAI Function Calling 指南', 'LINK', 'https://platform.openai.com/docs/guides/function-calling', 'Function Calling 原理与最佳实践', 3, NOW()),
(@pc6, 'AutoGen 多 Agent 框架', 'LINK', 'https://microsoft.github.io/autogen/', '微软多 Agent 协作框架', 4, NOW()),
(@pc6, 'MCP 协议文档', 'LINK', 'https://modelcontextprotocol.io/', 'Model Context Protocol 与工具标准化', 5, NOW());


-- ========== 课程7：模型微调与部署 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程7：模型微调与部署', '使用 LlamaFactory 进行 LoRA 微调，用 Ollama 本地部署，用 FastAPI 封装推理接口，完成模型从训练到上线全流程。', 7, 'CODING', 150,
'```mermaid
mindmap
  root((模型微调与部署))
    微调基础
      Full Fine-tuning
      LoRA 低秩适配
      QLoRA 量化版
      PEFT 参数高效
    LlamaFactory
      数据集准备
      配置文件
      分布式训练
      模型评估
    本地部署
      Ollama 一键部署
      vLLM 高性能推理
      LMDeploy 国产
      ModelScope 模型
    API 封装
      FastAPI 接口
      OpenAI 兼容协议
      Docker 容器化
      负载均衡
    成本优化
      模型量化 INT4/8
      KV Cache 优化
      批量推理
      缓存策略
```

## 学习目标
掌握使用 LlamaFactory 微调开源大模型，并部署为可调用的 API 服务。

## 核心知识点

### LlamaFactory 微调
```bash
# 安装
git clone https://github.com/hiyouga/LLaMA-Factory.git
cd LLaMA-Factory
pip install -r requirements.txt

# 准备数据集（JSON 格式）
# train.json
[
    {"instruction": "什么是RAG？", "output": "RAG是检索增强生成..."},
    {"instruction": "解释注意力机制", "output": "注意力机制是..."}
]

# 启动训练
llamafactory-cli train \
    --model_name_or_path Qwen/Qwen2.5-7B \
    --dataset train.json \
    --finetuning_type lora \
    --lora_target q_proj,v_proj \
    --output_dir ./qwen-lora \
    --num_train_epochs 3 \
    --per_device_train_batch_size 4
```

### Ollama 本地部署
```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 拉取模型
ollama pull qwen2.5:7b
ollama pull deepseek-r1:7b

# 导入微调后的模型
ollama create my-ai-model -f Modelfile

# Modelfile
FROM ./qwen-lora
PARAMETER temperature 0.7
PARAMETER top_p 0.9
```

### FastAPI 推理服务
```python
from fastapi import FastAPI
from pydantic import BaseModel
import ollama

app = FastAPI()

class ChatRequest(BaseModel):
    message: str
    model: str = "qwen2.5:7b"

@app.post("/v1/chat/completions")
async def chat(request: ChatRequest):
    response = ollama.chat(
        model=request.model,
        messages=[{"role": "user", "content": request.message}]
    )
    return {"response": response["message"]["content"]}

# 启动：uvicorn app:app --host 0.0.0.0 --port 8000
```

## 小结
模型微调和部署是AI工程化的核心技能。LlamaFactory提供了极简的微调体验，Ollama让本地部署一键完成，FastAPI封装为标准的OpenAI兼容API。

## 实践练习
1. 准备一个领域数据集，用 LlamaFactory 微调 Qwen2.5
2. 用 Ollama 部署微调后的模型
3. 用 FastAPI 封装 OpenAI 兼容的 API 接口
4. Docker 容器化部署整个服务
',
NOW());

SET @pc7 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc7, 'LlamaFactory GitHub 仓库', 'LINK', 'https://github.com/hiyouga/LLaMA-Factory', '开源大模型微调框架，支持 LoRA/QLoRA', 1, NOW()),
(@pc7, 'Ollama 官方文档', 'LINK', 'https://ollama.com/docs', '本地大模型部署工具，一行命令跑模型', 2, NOW()),
(@pc7, 'FastAPI 官方文档', 'LINK', 'https://fastapi.tiangolo.com/', '高性能 Python API 框架，AI 推理首选', 3, NOW()),
(@pc7, 'vLLM 高性能推理', 'LINK', 'https://docs.vllm.ai/en/latest/', '高吞吐大模型推理引擎，支持 PagedAttention', 4, NOW()),
(@pc7, '蓝山工作室 LLM 微调课件', 'LINK', 'https://github.com/LanshanTeam/Courseware-AI-Python-2025', 'GitHub 课件：Ollama + LlamaFactory 微调实战', 5, NOW());


-- ========== 课程8：实战项目：AI 知识助手平台 ==========
INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, content_markdown, create_time) VALUES
(@path_python_ai, '课程8：实战项目：AI 知识助手平台', '综合运用 Python 全栈技能，构建一个完整的 AI 知识助手平台，整合 RAG、Agent、模型微调能力，支持多文档问答。', 8, 'CODING', 180,
'```mermaid
mindmap
  root((AI 知识助手平台))
    项目架构
      FastAPI 后端
      Streamlit 前端
      PostgreSQL 数据库
      Chroma 向量库
    核心功能
      多文档上传与解析
      智能问答 RAG
      Agent 自动分析
      报告自动生成
    技术栈
      LangChain 框架
      OpenAI/DeepSeek 模型
      Celery 异步任务
      Docker 部署
    高级特性
      多轮对话记忆
      文档溯源引用
      批量文档处理
      定时知识更新
    部署运维
      Docker Compose
      Nginx 反向代理
      Prometheus 监控
      日志收集 ELK
```

## 学习目标
综合运用课程1-7的全部知识，独立完成一个全功能的AI知识助手平台。

## 项目技术栈

| 层级 | 技术 | 用途 |
|------|------|------|
| 前端 | Streamlit / Gradio | 用户交互界面 |
| 后端 | FastAPI | API 服务和业务逻辑 |
| AI 框架 | LangChain | RAG + Agent 编排 |
| 向量库 | Chroma / FAISS | 文档向量存储 |
| 数据库 | PostgreSQL | 用户和对话记录 |
| 任务队列 | Celery + Redis | 异步文档处理 |
| 部署 | Docker + Nginx | 容器化部署 |

### 核心功能实现

**文档处理流程：**
```python
# 用户上传 → 文档解析 → 分块 → 向量化 → 存储
# 用户提问 → 向量检索 → Prompt注入 → LLM生成 → 返回+溯源
```

**Agent 分析流程：**
```python
# 用户请求分析 → Agent拆解任务 → 查询知识库
# → 调用计算工具 → 生成分析报告 → WebSocket推送
```

### Docker Compose 部署
```yaml
version: "3.8"
services:
  api:
    build: ./backend
    ports:
      - "8000:8000"
    depends_on:
      - chroma
      - redis

  chroma:
    image: chromadb/chroma
    volumes:
      - chroma-data:/chroma/chroma

  redis:
    image: redis:7-alpine

  celery:
    build: ./backend
    command: celery -A tasks worker -l info
```

## 项目交付要求
1. 支持上传 PDF / Word / Markdown / 纯文本
2. 基于 RAG 的文档问答，准确率 ≥ 85%
3. Agent 能自动生成数据分析报告
4. 多轮对话保持上下文
5. Docker 一键部署
6. API 响应时间 P99 < 5s

## 小结
本项目融合了Python全栈开发的各项技能：数据处理、深度学习、RAG、Agent、微调、部署。完成后将具备独立开发生产级AI应用的能力。
',
NOW());

SET @pc8 = LAST_INSERT_ID();

INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES
(@pc8, 'FastAPI 大型项目结构', 'LINK', 'https://fastapi.tiangolo.com/tutorial/bigger-applications/', 'FastAPI 项目组织最佳实践', 1, NOW()),
(@pc8, 'Celery 异步任务队列', 'LINK', 'https://docs.celeryq.dev/en/stable/', 'Python 分布式任务队列，适合 AI 异步处理', 2, NOW()),
(@pc8, 'LangServe 部署指南', 'LINK', 'https://python.langchain.com/docs/langserve/', 'LangChain Chain 部署为 API 服务', 3, NOW()),
(@pc8, 'AI Builder Python100 项目', 'LINK', 'https://github.com/guanyuchery-rgb/AI-Builder-Python100', 'GitHub 开源：100天 AI 学习路径项目实战', 4, NOW()),
(@pc8, 'Dify 开源 AI 平台', 'LINK', 'https://github.com/langgenius/dify', '开源 LLM 应用开发平台，集成 RAG + Agent', 5, NOW());


-- ============================================
-- 更新课程数
-- ============================================
UPDATE learning_path SET course_count = 8 WHERE id = @path_python_ai;


-- ============================================
-- 发布两条路径
-- ============================================
UPDATE learning_path SET status = 'PUBLISHED', update_time = NOW() WHERE id IN (@path_java_ai, @path_python_ai);
