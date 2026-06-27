package com.aicode.service;

import com.aicode.ai.AIPromptTemplate;
import com.aicode.ai.AIRateLimiter;
import com.aicode.ai.AIService;
import com.aicode.ai.InputSanitizer;
import com.aicode.dto.QaAnswerVO;
import org.jsoup.Jsoup;
import com.aicode.dto.QaFollowUpRequest;
import com.aicode.dto.QaQuestionRequest;
import com.aicode.dto.QaQuestionVO;
import com.aicode.entity.QaAnswer;
import com.aicode.entity.QaQuestion;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.QaAnswerMapper;
import com.aicode.mapper.QaQuestionMapper;
import com.aicode.mapper.UserMapper;
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
 * 问答服务
 */
@Service
public class QaService {

    private static final Logger log = LoggerFactory.getLogger(QaService.class);

    @Autowired
    private QaQuestionMapper qaQuestionMapper;

    @Autowired
    private QaAnswerMapper qaAnswerMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AIService aiService;

    @Autowired
    private AIRateLimiter rateLimiter;

    /**
     * 提问（同步创建问题，异步调用 AI 回答）
     */
    @Transactional
    public QaQuestionVO askQuestion(Long userId, QaQuestionRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "问题内容不能为空");
        }
        // 标题为空时自动从内容截取
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            String autoTitle = request.getContent().trim();
            if (autoTitle.length() > 80) {
                autoTitle = autoTitle.substring(0, 80) + "...";
            }
            request.setTitle(autoTitle);
        }

        // XSS 过滤 + 安全校验
        String safeTitle = Jsoup.clean(request.getTitle(), org.jsoup.safety.Safelist.none());
        String safeContent = Jsoup.clean(request.getContent(), org.jsoup.safety.Safelist.none());
        InputSanitizer.validate(safeTitle, "问题标题");
        InputSanitizer.validate(safeContent, "问题内容");

        Date now = new Date();
        QaQuestion question = new QaQuestion();
        question.setUserId(userId);
        question.setTitle(safeTitle);
        question.setContent(safeContent);
        question.setCategory(request.getCategory() != null ? request.getCategory() : "general");
        question.setTags(request.getTags());
        question.setStatus("ACTIVE");
        question.setCreateTime(now);
        question.setUpdateTime(now);

        qaQuestionMapper.insert(question);

        // 异步调用 AI 回答
        asyncAnswerQuestion(question.getId(), userId, request);

        return toQaQuestionVO(question, null);
    }

    /**
     * 异步 AI 回答
     */
    @Async("aiExecutor")
    protected void asyncAnswerQuestion(Long questionId, Long userId, QaQuestionRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            // 限流检查
            if (!rateLimiter.tryAcquire(userId, null)) {
                log.warn("AI 限流，问答跳过: questionId={}", questionId);
                saveAnswer(questionId, "系统繁忙，请稍后重试（API 调用频率超限）", startTime);
                return;
            }

            String systemPrompt = AIPromptTemplate.QA_SYSTEM;
            String userMessage = AIPromptTemplate.buildQaPrompt(
                    request.getContent(), request.getCategory());

            String aiResponse = aiService.chat(systemPrompt, userMessage);

            saveAnswer(questionId, aiResponse, startTime);

            // 更新问题状态
            QaQuestion question = qaQuestionMapper.selectById(questionId);
            if (question != null) {
                int count = qaAnswerMapper.countByQuestionId(questionId);
                question.setAnswerCount(count);
                question.setIsResolved(1);
                question.setUpdateTime(new Date());
                qaQuestionMapper.updateById(question);
            }

            log.info("AI 回答完成: questionId={}, 耗时={}ms", questionId,
                    (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            log.error("AI 回答异常: questionId={}", questionId, e);
            saveAnswer(questionId, "AI 回答异常: " + e.getMessage(), startTime);
        }
    }

    private void saveAnswer(Long questionId, String content, long startTime) {
        int duration = (int) (System.currentTimeMillis() - startTime);
        QaAnswer answer = new QaAnswer();
        answer.setQuestionId(questionId);
        answer.setType("AI");
        answer.setContent(content);
        answer.setAiResponseTime(duration);
        answer.setCreateTime(new Date());
        qaAnswerMapper.insert(answer);
    }

    /**
     * 获取问题详情（含回答列表）
     */
    public QaQuestionVO getQuestionDetail(Long questionId) {
        QaQuestion question = qaQuestionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(404, "问题不存在");
        }

        // 增加浏览次数
        qaQuestionMapper.incrementViewCount(questionId);

        List<QaAnswer> answers = qaAnswerMapper.selectByQuestionId(questionId);
        List<QaAnswerVO> answerVOs = answers.stream().map(this::toQaAnswerVO).collect(Collectors.toList());

        return toQaQuestionVO(question, answerVOs);
    }

    /**
     * 获取用户的问题列表
     */
    public List<QaQuestionVO> getUserQuestions(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return qaQuestionMapper.selectByUserId(userId, offset, pageSize).stream()
                .map(q -> toQaQuestionVO(q, null))
                .collect(Collectors.toList());
    }

    public long countUserQuestions(Long userId) {
        return qaQuestionMapper.countByUserId(userId);
    }

    public List<QaQuestionVO> listUserQuestions(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return qaQuestionMapper.selectByUserId(userId, offset, pageSize).stream()
                .map(q -> toQaQuestionVO(q, null))
                .collect(Collectors.toList());
    }

    /**
     * 公开问题列表（管理用）
     */
    public List<QaQuestionVO> listQuestions(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return qaQuestionMapper.selectAllPaged(offset, pageSize).stream()
                .map(q -> toQaQuestionVO(q, null))
                .collect(Collectors.toList());
    }

    /**
     * 搜索问题
     */
    public List<QaQuestionVO> searchQuestions(String keyword, String category, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return qaQuestionMapper.search(keyword, category, offset, pageSize).stream()
                .map(q -> toQaQuestionVO(q, null))
                .collect(Collectors.toList());
    }

    public long countSearch(String keyword, String category) {
        return qaQuestionMapper.countSearch(keyword, category);
    }

    /**
     * 获取所有问题总数
     */
    public long countAllQuestions() {
        return qaQuestionMapper.countAll();
    }

    /**
     * 追问（AI 调用在事务外，只保存结果在事务内）
     */
    public QaAnswerVO askFollowUp(Long userId, QaFollowUpRequest request) {
        QaQuestion question = qaQuestionMapper.selectById(request.getQuestionId());
        if (question == null) {
            throw new BusinessException(404, "问题不存在");
        }

        // 安全校验
        InputSanitizer.validate(request.getContent(), "追问内容");

        // 限流检查（在 AI 调用前）
        if (!rateLimiter.tryAcquire(userId, null)) {
            throw new BusinessException(429, "AI 服务繁忙，请稍后再试");
        }

        // 限制上下文长度：只取最近 3 轮对话，每段不超过 500 字
        List<QaAnswer> previousAnswers = qaAnswerMapper.selectByQuestionId(request.getQuestionId());
        String context = previousAnswers.stream()
                .skip(Math.max(0, previousAnswers.size() - 6)) // 最多 6 条（3 轮问答）
                .map(a -> {
                    String content = a.getContent();
                    if (content != null && content.length() > 500) {
                        content = content.substring(0, 500) + "...(截断)";
                    }
                    return (a.getType().equals("AI") ? "AI: " : "用户: ") + content;
                })
                .collect(Collectors.joining("\n\n"));

        // AI 调用（事务外，避免长事务占连接）
        String systemPrompt = AIPromptTemplate.QA_SYSTEM;
        String userMessage = AIPromptTemplate.buildFollowUpPrompt(
                question.getTitle(), context, request.getContent());

        long startTime = System.currentTimeMillis();
        String aiResponse = aiService.chat(systemPrompt, userMessage);
        int duration = (int) (System.currentTimeMillis() - startTime);

        // 保存结果（单独事务）
        return saveFollowUpAnswer(question, aiResponse, duration);
    }

    @Transactional
    protected QaAnswerVO saveFollowUpAnswer(QaQuestion question, String aiResponse, int duration) {
        QaAnswer answer = new QaAnswer();
        answer.setQuestionId(question.getId());
        answer.setType("AI");
        answer.setContent(aiResponse);
        answer.setAiResponseTime(duration);
        answer.setCreateTime(new Date());
        qaAnswerMapper.insert(answer);

        // 更新回答数
        int count = qaAnswerMapper.countByQuestionId(question.getId());
        question.setAnswerCount(count);
        question.setUpdateTime(new Date());
        qaQuestionMapper.updateById(question);

        return toQaAnswerVO(answer);
    }

    /**
     * 管理员获取所有问题
     */
    public List<QaQuestionVO> listAllQuestions(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return qaQuestionMapper.selectAllPaged(offset, pageSize).stream()
                .map(q -> toQaQuestionVO(q, null))
                .collect(Collectors.toList());
    }

    // ========== 内部方法 ==========

    private QaQuestionVO toQaQuestionVO(QaQuestion q, List<QaAnswerVO> answers) {
        if (q == null) return null;
        QaQuestionVO vo = new QaQuestionVO();
        vo.setId(q.getId());
        vo.setTitle(q.getTitle());
        vo.setContent(q.getContent());
        vo.setCategory(q.getCategory());
        vo.setTags(q.getTags());
        vo.setViewCount(q.getViewCount());
        vo.setFavoriteCount(q.getFavoriteCount());
        vo.setAnswerCount(q.getAnswerCount());
        vo.setIsResolved(q.getIsResolved());
        vo.setStatus(q.getStatus());
        vo.setCreateTime(q.getCreateTime());
        vo.setUpdateTime(q.getUpdateTime());
        vo.setAnswers(answers);

        // 获取用户名
        if (q.getUserId() != null) {
            try {
                com.aicode.entity.User user = userMapper.selectById(q.getUserId());
                if (user != null) vo.setUsername(user.getUsername());
            } catch (Exception ignored) {}
        }
        return vo;
    }

    private QaAnswerVO toQaAnswerVO(QaAnswer a) {
        if (a == null) return null;
        QaAnswerVO vo = new QaAnswerVO();
        vo.setId(a.getId());
        vo.setType(a.getType());
        vo.setContent(a.getContent());
        vo.setRating(a.getRating());
        vo.setIsAccepted(a.getIsAccepted());
        vo.setAiResponseTime(a.getAiResponseTime());
        vo.setCreateTime(a.getCreateTime());
        return vo;
    }
}
