package com.aicode.util;

/**
 * 字符串工具类
 */
public class StringUtil {

    private StringUtil() {}

    /** 对异常消息脱敏，避免密码/key 等敏感信息写入日志 */
    public static String maskSensitive(String msg) {
        if (msg == null) return null;
        String masked = msg.replaceAll("Bearer\\s+\\S+", "Bearer ***");
        masked = masked.replaceAll("(?i)\\b(key|api[_-]?key|secret|password|token)\\s*[:=]\\s*\\S+", "$1=***");
        return masked;
    }
}
