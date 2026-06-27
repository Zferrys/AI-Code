package com.aicode.ai;

import com.aicode.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * AI 输入过滤工具
 * 检测 prompt injection、敏感词、异常字符
 */
public class InputSanitizer {

    private static final Logger log = LoggerFactory.getLogger(InputSanitizer.class);

    /** 最大输入长度 */
    private static final int MAX_INPUT_LENGTH = 10000;

    /** Prompt injection 模式（中文） */
    private static final Pattern[] INJECTION_PATTERNS = {
        Pattern.compile("忽略(之前|以上|所有)(的)?(指令|命令|要求|规则|指示|提示|prompt|system)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(现在|接下来|下面)(你|请)(必须|要|给我).*?(扮演|假装|当成|作为)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("你是(一个|名)?(AI|机器人|助手|模型|人工智能)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("告诉我你的(系统)?(提示词|指令|prompt|system|设置|配置)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("重复(一遍|以上|刚刚|之前)(的)?(话|内容|回答|指令)", Pattern.CASE_INSENSITIVE),
    };

    /** Prompt injection 模式（英文） */
    private static final Pattern[] INJECTION_PATTERNS_EN = {
        Pattern.compile("ignore\\s+(all\\s+)?(previous|above|prior)\\s+(instructions|commands|prompts|messages|rules)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("reveal\\s+(your\\s+)?(system\\s+)?(prompt|instructions|configuration)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("forget\\s+(everything|all|your\\s+instructions)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("you\\s+are\\s+(an?\\s+)?(AI|bot|model|assistant)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("print\\s+(your\\s+)?(system\\s+)?(prompt|instructions)", Pattern.CASE_INSENSITIVE),
    };

    /** 敏感内容关键字 */
    private static final List<String> SENSITIVE_KEYWORDS = Arrays.asList(
            "api.key", "api_key", "api-secret", "password", "jwt.secret",
            "AGNES_API_KEY", "AI_API_KEY", "DEEPSEEK_API_KEY", "MYSQL_ROOT_PASSWORD"
    );

    /**
     * 校验输入，通过则静默返回，不通过抛出 BusinessException
     */
    public static void validate(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            return; // 空值由业务层校验
        }

        String text = input.trim();

        // 长度限制
        if (text.length() > MAX_INPUT_LENGTH) {
            log.warn("输入过长: field={}, length={}", fieldName, text.length());
            throw new BusinessException(400, fieldName + " 内容过长，请精简后重试（最多 " + MAX_INPUT_LENGTH + " 字）");
        }

        // 检测 prompt injection（中文）
        for (Pattern p : INJECTION_PATTERNS) {
            if (p.matcher(text).find()) {
                log.warn("检测到 prompt injection（中文）: field={}, matched={}", fieldName, text.substring(0, Math.min(50, text.length())));
                throw new BusinessException(400, "输入包含不允许的内容，请修改后重试");
            }
        }

        // 检测 prompt injection（英文）
        for (Pattern p : INJECTION_PATTERNS_EN) {
            if (p.matcher(text).find()) {
                log.warn("检测到 prompt injection（英文）: field={}, matched={}", fieldName, text.substring(0, Math.min(50, text.length())));
                throw new BusinessException(400, "输入包含不允许的内容，请修改后重试");
            }
        }

        // 检测敏感关键字
        String lower = text.toLowerCase();
        for (String kw : SENSITIVE_KEYWORDS) {
            if (lower.contains(kw.toLowerCase())) {
                log.warn("检测到敏感关键字: field={}, keyword={}", fieldName, kw);
                throw new BusinessException(400, "输入包含不允许的内容，请修改后重试");
            }
        }
    }

    /**
     * 清理输入（去掉危险模式，保留正常文本）
     * 当前简单返回原文本 + 只做 trim
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.trim();
    }
}
