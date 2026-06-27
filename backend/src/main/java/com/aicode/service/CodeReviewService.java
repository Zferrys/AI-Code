package com.aicode.service;

import com.aicode.ai.AIPromptTemplate;
import com.aicode.ai.AIRateLimiter;
import com.aicode.ai.AIService;
import com.aicode.ai.InputSanitizer;
import com.aicode.dto.CodeReviewRequest;
import com.aicode.dto.CodeReviewStatusVO;
import com.aicode.dto.CodeReviewVO;
import com.aicode.entity.CodeReview;
import com.aicode.entity.CodeReviewHistory;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.CodeReviewHistoryMapper;
import com.aicode.mapper.CodeReviewMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.Jsoup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码审查服务
 */
@Service
public class CodeReviewService {

    private static final Logger log = LoggerFactory.getLogger(CodeReviewService.class);

    @Autowired
    private CodeReviewMapper codeReviewMapper;

    @Autowired
    private CodeReviewHistoryMapper codeReviewHistoryMapper;

    @Autowired
    private AIService aiService;

    @Autowired
    private AIRateLimiter rateLimiter;

    /**
     * 提交代码审查（同步创建记录，异步调用 AI 处理）
     */
    @Transactional
    public CodeReviewVO submitCodeReview(Long userId, CodeReviewRequest request) {
        // 参数校验
        if (request.getCodeContent() == null || request.getCodeContent().trim().isEmpty()) {
            throw new BusinessException(400, "代码内容不能为空");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException(400, "审查标题不能为空");
        }
        if (request.getLanguage() == null || request.getLanguage().trim().isEmpty()) {
            request.setLanguage("java");
        }

        // XSS 过滤 + 安全校验
        String safeTitle = Jsoup.clean(request.getTitle(), org.jsoup.safety.Safelist.none());
        String safeCode = Jsoup.clean(request.getCodeContent(), org.jsoup.safety.Safelist.none());
        InputSanitizer.validate(safeTitle, "审查标题");
        InputSanitizer.validate(safeCode, "代码内容");

        // 创建审查记录（状态：PENDING）
        Date now = new Date();
        CodeReview review = new CodeReview();
        review.setUserId(userId);
        review.setTitle(safeTitle);
        review.setLanguage(request.getLanguage());
        review.setCodeContent(safeCode);
        review.setReviewResult("");
        review.setStatus("PENDING");
        review.setCreateTime(now);

        codeReviewMapper.insert(review);

        // 异步调用 AI 进行审查
        asyncProcessReview(review.getId(), userId, request);

        return toCodeReviewVO(review);
    }

    /**
     * 异步处理 AI 代码审查
     */
    @Async("aiExecutor")
    protected void asyncProcessReview(Long reviewId, Long userId, CodeReviewRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            // 更新状态为处理中
            codeReviewMapper.updateStatus(reviewId, "PROCESSING");

            // 限流检查
            if (!rateLimiter.tryAcquire(userId, null)) {
                CodeReview review = codeReviewMapper.selectById(reviewId);
                review.setStatus("FAILED");
                review.setReviewResult("{\"summary\":\"系统繁忙，请稍后重试（API 调用频率超限）\"}");
                codeReviewMapper.updateResult(review);
                log.warn("AI 限流，审查任务跳过: reviewId={}", reviewId);
                return;
            }

            // 构建 prompt 并调用 AI
            String systemPrompt = AIPromptTemplate.CODE_REVIEW_SYSTEM;
            String userMessage = AIPromptTemplate.buildCodeReviewPrompt(
                    request.getCodeContent(), request.getLanguage());

            String aiResponse = aiService.chat(systemPrompt, userMessage);
            int duration = (int) (System.currentTimeMillis() - startTime);

            // 使用 Jackson 解析 AI 响应的 JSON
            AiReviewResult parsed = cleanAndValidateJson(aiResponse);

            // 更新审查结果
            CodeReview review = codeReviewMapper.selectById(reviewId);
            review.setReviewResult(parsed.rawJson);
            review.setQualityScore(parsed.quality);
            review.setBugCount(parsed.bugCount);
            review.setSuggestionCount(parsed.suggestionCount);
            review.setStatus("COMPLETED");
            review.setAiResponseTime(duration);
            codeReviewMapper.updateResult(review);

            // 记录历史版本
            CodeReviewHistory history = new CodeReviewHistory();
            history.setReviewId(reviewId);
            history.setVersion(1);
            history.setCodeContent(request.getCodeContent());
            history.setReviewResult(aiResponse);
            history.setAiResponseTime(duration);
            history.setCreateTime(new Date());
            codeReviewHistoryMapper.insert(history);

            log.info("代码审查完成: reviewId={}, 耗时={}ms, 评分={}",
                    reviewId, duration, parsed.quality);

        } catch (Exception e) {
            log.error("AI 审查异常: reviewId={}", reviewId, e);
            try {
                CodeReview review = codeReviewMapper.selectById(reviewId);
                review.setStatus("FAILED");
                review.setReviewResult("{\"summary\":\"审查异常: " +
                        e.getMessage().replace("\"", "'") + "\"}");
                review.setAiResponseTime((int) (System.currentTimeMillis() - startTime));
                codeReviewMapper.updateResult(review);
            } catch (Exception ex) {
                log.error("更新审查失败状态异常", ex);
            }
        }
    }

    /**
     * 获取审查状态（供前端轮询）
     */
    public CodeReviewStatusVO getReviewStatus(Long reviewId, Long userId) {
        CodeReview review = codeReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "审查记录不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问此审查记录");
        }

        String message;
        String stage = "analyzing";
        switch (review.getStatus()) {
            case "PENDING":
                message = "排队等待中...";
                stage = "queuing";
                break;
            case "PROCESSING":
                message = "AI 审查中...";
                stage = "analyzing";
                break;
            case "COMPLETED":
                message = "审查完成";
                stage = "done";
                break;
            case "FAILED":
                message = "审查失败";
                stage = "failed";
                break;
            default:
                message = "";
        }

        int elapsedSeconds = 0;
        if (review.getCreateTime() != null) {
            elapsedSeconds = (int) ((System.currentTimeMillis() - review.getCreateTime().getTime()) / 1000);
        }

        return new CodeReviewStatusVO(reviewId, review.getStatus(),
                review.getQualityScore(), message, elapsedSeconds, stage);
    }

    /**
     * 获取审查详情
     */
    public CodeReviewVO getReviewDetail(Long reviewId, Long userId) {
        CodeReview review = codeReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "审查记录不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问此审查记录");
        }
        return toCodeReviewVO(review);
    }

    /**
     * 获取用户的审查历史
     */
    public List<CodeReviewVO> getUserReviews(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<CodeReview> reviews = codeReviewMapper.selectByUserId(userId, offset, pageSize);
        return reviews.stream().map(this::toCodeReviewVO).collect(Collectors.toList());
    }

    public long countUserReviews(Long userId) {
        return codeReviewMapper.countByUserId(userId);
    }

    /**
     * 管理员统计所有审查记录总数
     */
    public long countAllReviews() {
        return codeReviewMapper.countAll();
    }

    /**
     * 管理员获取所有审查记录
     */
    public List<CodeReviewVO> listAllReviews(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return codeReviewMapper.selectAllPaged(offset, pageSize).stream()
                .map(this::toCodeReviewVO).collect(Collectors.toList());
    }

    // ========== 内部方法 ==========

    /**
     * 使用 Jackson 解析和验证 AI 返回的 JSON，提取字段值
     * 解析成功后重新序列化，确保前端 JSON.parse 不会因宽松格式而失败
     * 当 AI 输出掺杂了多余文本时，尝试提取 JSON 部分；兜底返回安全错误 JSON
     */
    private AiReviewResult cleanAndValidateJson(String raw) {
        String clean = raw;
        // 清理可能的 ```json 和 ``` 标记
        clean = clean.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```\\s*", "").trim();

        ObjectMapper mapper = new ObjectMapper();
        try {
            // 先尝试直接解析
            JsonNode root = mapper.readTree(clean);
            return buildResult(mapper, root, clean);
        } catch (Exception e) {
            log.warn("AI 响应 JSON 解析失败，尝试提取 JSON 部分: {}", e.getMessage());
        }

        // 降级1：尝试从文本中提取 { ... } 包裹的 JSON 部分
        try {
            int start = clean.indexOf("{");
            int end = clean.lastIndexOf("}");
            if (start != -1 && end > start) {
                String extracted = clean.substring(start, end + 1);
                JsonNode root = mapper.readTree(extracted);
                log.info("AI 响应 JSON 提取成功，丢弃 {} 字符非 JSON 前缀/后缀",
                        clean.length() - extracted.length());
                return buildResult(mapper, root, extracted);
            }
        } catch (Exception e) {
            log.warn("AI 响应 JSON 提取解析也失败: {}", e.getMessage());
        }

        // 降级2：兜底 — 返回安全 JSON 错误信息，绝不存原始脏文本
        log.warn("AI 响应完全无法解析为 JSON，返回错误响应");
        String safeJson = "{\"summary\":\"AI 审查结果格式异常，无法解析。请重试。\"}";
        try {
            JsonNode root = mapper.readTree(safeJson);
            return new AiReviewResult(safeJson, 0, 0, 0);
        } catch (Exception ex) {
            // 极端兜底，不应该发生
            return new AiReviewResult("{\"summary\":\"审查失败\"}", 0, 0, 0);
        }
    }

    private AiReviewResult buildResult(ObjectMapper mapper, JsonNode root, String source) throws Exception {
        Integer quality = root.has("quality") ? root.get("quality").asInt(0) : null;
        int bugCount = 0;
        int suggestionCount = 0;

        if (root.has("bugs") && root.get("bugs").isArray()) {
            bugCount = root.get("bugs").size();
        }
        if (root.has("optimizations") && root.get("optimizations").isArray()) {
            suggestionCount = root.get("optimizations").size();
        }

        // 重新序列化 JSON，保证格式严格合法
        String reSerialized = mapper.writeValueAsString(root);
        return new AiReviewResult(reSerialized, quality, bugCount, suggestionCount);
    }

    /**
     * 降级提取：在非 JSON 文本中查找 key 后的第一个数字
     */
    private Integer fallbackExtractInt(String text, String key, int defaultVal) {
        try {
            int idx = text.indexOf(key);
            if (idx == -1) return defaultVal;
            String after = text.substring(idx + key.length());
            int colon = after.indexOf(":");
            if (colon == -1) return defaultVal;
            StringBuilder num = new StringBuilder();
            char[] chars = after.substring(colon + 1).toCharArray();
            for (char c : chars) {
                if (Character.isDigit(c)) {
                    num.append(c);
                } else if (num.length() > 0) {
                    break;
                }
            }
            return num.length() > 0 ? Integer.parseInt(num.toString()) : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    /**
     * 降级提取：计算 JSON 数组中的对象数量
     */
    private int fallbackExtractArrayCount(String text, String key) {
        try {
            int idx = text.indexOf(key);
            if (idx == -1) return 0;
            String after = text.substring(idx + key.length());
            int colon = after.indexOf(":");
            int bracketStart = after.indexOf("[", colon);
            int bracketEnd = after.indexOf("]", bracketStart);
            if (bracketStart == -1 || bracketEnd == -1) return 0;
            String content = after.substring(bracketStart + 1, bracketEnd).trim();
            if (content.isEmpty()) return 0;
            int count = 0, pos = 0;
            while ((pos = content.indexOf("{", pos)) != -1) { count++; pos++; }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * AI 审查结果解析后的内部值对象
     */
    private static class AiReviewResult {
        final String rawJson;
        final Integer quality;
        final int bugCount;
        final int suggestionCount;

        AiReviewResult(String rawJson, Integer quality, int bugCount, int suggestionCount) {
            this.rawJson = rawJson;
            this.quality = quality;
            this.bugCount = bugCount;
            this.suggestionCount = suggestionCount;
        }
    }

    private CodeReviewVO toCodeReviewVO(CodeReview review) {
        if (review == null) return null;
        CodeReviewVO vo = new CodeReviewVO();
        vo.setId(review.getId());
        vo.setTitle(review.getTitle());
        vo.setLanguage(review.getLanguage());
        vo.setCodeContent(review.getCodeContent());
        vo.setReviewResult(review.getReviewResult());
        vo.setQualityScore(review.getQualityScore());
        vo.setBugCount(review.getBugCount());
        vo.setSuggestionCount(review.getSuggestionCount());
        vo.setStatus(review.getStatus());
        vo.setAiResponseTime(review.getAiResponseTime());
        vo.setCreateTime(review.getCreateTime());
        return vo;
    }
}
