package com.aicode.ai;

import com.aicode.exception.BusinessException;
import com.aicode.mapper.SysConfigMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * AI 服务实现（兼容 OpenAI 协议）
 * 配置缓存 10 秒，避免每次请求都查数据库
 */
@Service
public class DeepSeekV4ServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekV4ServiceImpl.class);

    private static final String DEFAULT_ENDPOINT = "https://apihub.agnes-ai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "agnes-2.0-flash";
    private static final int DEFAULT_TIMEOUT = 60000;
    /** 配置缓存时间（毫秒），后台改配置后最多等这么久才生效 */
    private static final long CONFIG_CACHE_TTL = 10000;
    private static final int MAX_RESPONSE_CHARS = 500_000;
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    // 身份泄露清理模式
    private static final Pattern[] IDENTITY_PATTERNS = {
        Pattern.compile("^(你好[，!！]*(?:我(?:是|叫))\\s*\\S+[。！!]?\\s*)", Pattern.MULTILINE),
        Pattern.compile("^(Hi[，,]*(?:I'?m|I am)\\s*\\S+[.!]?\\s*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE),
        Pattern.compile("^(你好[，!！]*我是\\s*\\S+[，!！]?\\s*)", Pattern.MULTILINE),
    };
    // injection 成功检测（如果 AI 回复包含这些说明被注入了）
    private static final Pattern INJECTION_CONFIRM = Pattern.compile(
            "忽略(之前|以上|所有)(的)?(指令|命令|要求|规则|提示|prompt)",
            Pattern.CASE_INSENSITIVE);

    @Autowired
    private SysConfigMapper sysConfigMapper;

    private String apiEndpoint;
    private int readTimeoutMs;
    private String apiKey;
    private String modelName;
    private long lastConfigRefresh;

    @PostConstruct
    public void init() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        refreshConfig(true);
        log.info("AI 服务初始化完成, endpoint={}, model={}, timeout={}ms",
                this.apiEndpoint, this.modelName, this.readTimeoutMs);
    }

    /**
     * 刷新配置（带缓存，10 秒内不重复查库）
     */
    private void refreshConfig() {
        refreshConfig(false);
    }

    private synchronized void refreshConfig(boolean force) {
        long now = System.currentTimeMillis();
        if (!force && now - lastConfigRefresh < CONFIG_CACHE_TTL) {
            return; // 缓存有效，跳过
        }
        lastConfigRefresh = now;

        String endpoint = sysConfigMapper.selectValueByKey("ai.api.endpoint");
        this.apiEndpoint = (endpoint != null && !endpoint.isEmpty()) ? endpoint : DEFAULT_ENDPOINT;

        String timeout = sysConfigMapper.selectValueByKey("ai.api.timeout");
        this.readTimeoutMs = (timeout != null) ? Integer.parseInt(timeout) : DEFAULT_TIMEOUT;

        String dbKey = sysConfigMapper.selectValueByKey("ai.api.key");
        this.apiKey = (dbKey != null && !dbKey.isEmpty()) ? dbKey : getEnvApiKey();

        String model = sysConfigMapper.selectValueByKey("ai.api.model");
        this.modelName = (model != null && !model.isEmpty()) ? model : DEFAULT_MODEL;
    }

    @Override
    public String chat(String systemPrompt, String userMessage, int timeoutMs) {
        refreshConfig(); // 10 秒内不重复查库
        if (apiKey == null || apiKey.isEmpty()) {
            throw new BusinessException(500, "AI API Key 未配置，请登录后台 → 系统配置 → 设置 AI API Key");
        }

        int actualTimeout = Math.max(timeoutMs, this.readTimeoutMs);
        long start = System.currentTimeMillis();

        try {
            String requestBody = buildRequestBody(systemPrompt, userMessage);
            log.info("AI 请求: endpoint={}, model={}, timeout={}ms",
                    this.apiEndpoint, this.modelName, actualTimeout);

            URL url = new URL(this.apiEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setConnectTimeout(30000);       // 连接超时 30 秒（Agnes 较慢）
            conn.setReadTimeout(actualTimeout);   // 读取超时由配置决定
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            long elapsed = System.currentTimeMillis() - start;

            if (responseCode != 200) {
                String errorBody = readStream(conn.getErrorStream());
                log.error("AI API 返回错误: code={}, body={}, 耗时={}ms",
                        responseCode, errorBody, elapsed);
                throw new BusinessException(500, "AI 服务暂时不可用，请检查系统配置后重试");
            }

            String responseBody = readStream(conn.getInputStream());
            String result = parseResponse(responseBody);
            log.info("AI 请求成功: 耗时={}ms, 响应长度={}", elapsed, result.length());
            return result;

        } catch (java.net.SocketTimeoutException e) {
            long elapsed = System.currentTimeMillis() - start;
            String msg = e.getMessage() != null ? e.getMessage() : "";
            boolean isConnect = msg.contains("connect") || elapsed < 35000;
            log.error("AI 请求{}: 已等待{}ms, endpoint={}",
                    isConnect ? "连接超时" : "响应超时", elapsed, this.apiEndpoint);
            if (isConnect) {
                throw new BusinessException(500, "AI 服务连接超时，已等待 " + elapsed + "ms。" +
                        "请检查网络连接或 AI 服务是否正常");
            }
            throw new BusinessException(500, "AI 服务响应超时，已等待 " + elapsed + "ms。" +
                    "可在系统配置中调大 ai.api.timeout 值（当前 " + actualTimeout + "ms）");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 服务调用异常", e);
            throw new BusinessException(500, "AI 服务调用异常，请稍后重试");
        }
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        return chat(systemPrompt, userMessage, this.readTimeoutMs);
    }

    // ========== 内部方法 ==========

    private String buildRequestBody(String systemPrompt, String userMessage) {
        try {
            com.fasterxml.jackson.databind.node.ObjectNode body = JSON_MAPPER.createObjectNode();
            body.put("model", this.modelName);
            com.fasterxml.jackson.databind.node.ArrayNode messages = body.putArray("messages");
            messages.addObject().put("role", "system").put("content", systemPrompt);
            messages.addObject().put("role", "user").put("content", userMessage);
            body.put("temperature", 0.3);
            body.put("max_tokens", 8192);
            body.put("stream", false);
            return JSON_MAPPER.writeValueAsString(body);
        } catch (Exception e) {
            log.error("构建 AI 请求体失败", e);
            throw new BusinessException(500, "AI 请求构建失败");
        }
    }

    private String parseResponse(String responseBody) {
        try {
            com.fasterxml.jackson.databind.JsonNode root = JSON_MAPPER.readTree(responseBody);
            com.fasterxml.jackson.databind.JsonNode content = root.path("choices").get(0).path("message").path("content");
            if (content.isMissingNode()) {
                content = root.path("content");
            }
            if (content.isMissingNode() || !content.isTextual()) {
                log.warn("AI 响应格式异常，无法解析 content: {}", responseBody);
                return responseBody;
            }
            // 通用清洁：去掉 markdown 代码块包裹、非法转义
            String text = content.asText();
            if (text.length() > MAX_RESPONSE_CHARS) {
                log.warn("AI 响应过大 ({} chars)，截断至 {} chars", text.length(), MAX_RESPONSE_CHARS);
                text = text.substring(0, MAX_RESPONSE_CHARS);
            }
            text = text.replaceAll("(?s)```(?:json)?\\s*", "").replaceAll("(?s)```\\s*", "").trim();
            text = text.replaceAll("\\\\(\r?\n)", "$1");
            text = text.replaceAll("\\\\$", "");
            // 输出安全清理：移除身份泄露、检测 injection 成功
            text = cleanupOutput(text);
            return text;
        } catch (Exception e) {
            log.warn("AI 响应解析失败: {}", e.getMessage());
            return responseBody;
        }
    }

    /**
     * 清理 AI 输出：移除身份泄露文本，检测 injection 是否成功
     */
    private String cleanupOutput(String text) {
        if (text == null || text.isEmpty()) return text;
        // 移除开头的自我介绍
        for (Pattern p : IDENTITY_PATTERNS) {
            text = p.matcher(text).replaceFirst("");
        }
        // 检测 injection 成功特征 — 如果 AI 响应中包含"忽略指令"类文本，说明被注入了
        if (INJECTION_CONFIRM.matcher(text).find()) {
            log.warn("AI 响应检测到 injection 特征，已被拦截: {}", text.substring(0, Math.min(100, text.length())));
            return "抱歉，我无法处理这个请求。";
        }
        return text.trim();
    }

    private String readStream(java.io.InputStream stream) throws Exception {
        if (stream == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private String getEnvApiKey() {
        String env = System.getenv("AGNES_API_KEY");
        if (env != null && !env.isEmpty()) return env;
        env = System.getenv("AI_API_KEY");
        if (env != null && !env.isEmpty()) return env;
        env = System.getenv("DEEPSEEK_API_KEY");
        if (env != null && !env.isEmpty()) return env;
        String prop = System.getProperty("ai.api.key");
        if (prop != null && !prop.isEmpty()) return prop;
        return null;
    }
}
