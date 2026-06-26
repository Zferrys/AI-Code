package com.aicode.aspect;

import com.aicode.service.SysLogService;
import com.aicode.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller 操作日志切面
 * 1. 记录请求耗时和异常（日志文件）
 * 2. 关键操作写入 sys_log 表（供后台「操作日志」查看）
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    private static final long SLOW_THRESHOLD = 500;

    private static final Set<String> MUTATING_METHODS = new HashSet<>();
    static {
        MUTATING_METHODS.add("POST");
        MUTATING_METHODS.add("PUT");
        MUTATING_METHODS.add("DELETE");
    }

    /** URI 路径前缀 → 业务类型映射 */
    private static final Map<String, String> URI_TARGET_MAP = new HashMap<>();
    static {
        URI_TARGET_MAP.put("/admin/paths",   "PATH");
        URI_TARGET_MAP.put("/admin/users",   "USER");
        URI_TARGET_MAP.put("/admin/config",  "CONFIG");
        URI_TARGET_MAP.put("/admin/reviews", "REVIEW");
        URI_TARGET_MAP.put("/admin/questions", "QUESTION");
        URI_TARGET_MAP.put("/admin/tags",    "TAG");
        URI_TARGET_MAP.put("/user/avatar",   "AVATAR");
        URI_TARGET_MAP.put("/user/info",     "USER_PROFILE");
        URI_TARGET_MAP.put("/feedback",      "FEEDBACK");
        URI_TARGET_MAP.put("/favorite",      "FAVORITE");
    }

    /** 中文描述映射 */
    private static final Map<String, String> TARGET_LABEL_MAP = new HashMap<>();
    static {
        TARGET_LABEL_MAP.put("PATH", "学习路径");
        TARGET_LABEL_MAP.put("COURSE", "课程");
        TARGET_LABEL_MAP.put("USER", "用户");
        TARGET_LABEL_MAP.put("CONFIG", "系统配置");
        TARGET_LABEL_MAP.put("REVIEW", "代码审查");
        TARGET_LABEL_MAP.put("QUESTION", "问答");
        TARGET_LABEL_MAP.put("TAG", "标签");
        TARGET_LABEL_MAP.put("AVATAR", "头像");
        TARGET_LABEL_MAP.put("USER_PROFILE", "个人信息");
        TARGET_LABEL_MAP.put("FEEDBACK", "反馈");
        TARGET_LABEL_MAP.put("FAVORITE", "收藏");
    }

    /** 取 URI 末尾的数字 ID */
    private static final Pattern ID_PATTERN = Pattern.compile("/(\\d+)(?:/courses/(\\d+))?$");

    @Autowired
    private SysLogService sysLogService;

    @Around("execution(* com.aicode.controller..*.*(..))")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // 获取请求信息（失败时不阻塞主流程）
        RequestInfo req = extractRequestInfo();

        try {
            Object result = joinPoint.proceed();
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            logExecution(className, methodName, req, elapsedMs, null);

            if (req != null && isMutating(req.method)) {
                writeLogSafely(req, null);
            }

            return result;
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            String safeMsg = StringUtil.maskSensitive(e.getMessage());
            logExecution(className, methodName, req, elapsedMs, safeMsg);

            if (req != null && isMutating(req.method)) {
                writeLogSafely(req, safeMsg);
            }
            throw e;
        }
    }

    // ========== 请求信息提取 ==========

    private RequestInfo extractRequestInfo() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            return new RequestInfo(request, request.getMethod(), request.getRequestURI());
        } catch (Exception e) {
            log.debug("无法获取请求上下文: {}", e.getMessage());
            return null;
        }
    }

    private static class RequestInfo {
        final HttpServletRequest request;
        final String method;
        final String uri;
        RequestInfo(HttpServletRequest request, String method, String uri) {
            this.request = request;
            this.method = method;
            this.uri = uri;
        }
    }

    // ========== 日志记录 ==========

    private void logExecution(String className, String methodName, RequestInfo req, long elapsedMs, String errorMsg) {
        String httpInfo = req != null ? " [" + req.method + " " + req.uri + "]" : " [no request]";
        if (errorMsg != null) {
            log.error("{}.{} {} 异常: {}, 耗时 {}ms", className, methodName, httpInfo, errorMsg, elapsedMs);
        } else if (elapsedMs > SLOW_THRESHOLD) {
            log.warn("{}.{} {} 耗时 {}ms", className, methodName, httpInfo, elapsedMs);
        } else {
            log.debug("{}.{} {} 耗时 {}ms", className, methodName, httpInfo, elapsedMs);
        }
    }

    // ========== 操作日志写入（安全，不抛异常） ==========

    private void writeLogSafely(RequestInfo req, String errorMsg) {
        try {
            String actionType = mapActionType(req.method);
            String targetType = mapTargetType(req.uri);
            if (targetType == null) return;
            Long targetId = extractTargetId(req.uri);
            String description = buildDescription(actionType, targetType, req.uri, errorMsg);
            sysLogService.log(req.request, actionType, targetType, targetId, description);
        } catch (Exception e) {
            log.warn("操作日志写入失败: {}", e.getMessage());
        }
    }

    // ========== 映射方法 ==========

    private static boolean isMutating(String method) {
        return method != null && MUTATING_METHODS.contains(method);
    }

    private static String mapActionType(String httpMethod) {
        if (httpMethod == null) return "UNKNOWN";
        switch (httpMethod) {
            case "POST":   return "CREATE";
            case "PUT":    return "UPDATE";
            case "DELETE": return "DELETE";
            default:       return "UNKNOWN";
        }
    }

    private static String mapTargetType(String uri) {
        if (uri == null) return null;
        for (Map.Entry<String, String> entry : URI_TARGET_MAP.entrySet()) {
            String key = entry.getKey();
            // 匹配 /xxx/paths 后紧跟 / 或结尾，避免 /paths-copy 误匹配
            int idx = uri.indexOf(key);
            if (idx >= 0) {
                int end = idx + key.length();
                if (end == uri.length() || uri.charAt(end) == '/') {
                    return entry.getValue();
                }
            }
        }
        if (uri.contains("/courses")) return "COURSE";
        return null;
    }

    /**
     * 从 URI 末尾提取数字 ID
     * /admin/paths/123          → 123
     * /admin/paths/123/courses/456 → 456（取课程 ID）
     */
    static Long extractTargetId(String uri) {
        if (uri == null) return null;
        Matcher m = ID_PATTERN.matcher(uri);
        if (m.find()) {
            // group(2) 是 /courses/{id} 中的数字
            if (m.group(2) != null) return Long.parseLong(m.group(2));
            return Long.parseLong(m.group(1));
        }
        return null;
    }

    // ========== 描述构建 ==========

    private static String buildDescription(String action, String targetType, String uri, String errorMsg) {
        StringBuilder sb = new StringBuilder();
        switch (action) {
            case "CREATE": sb.append("新增"); break;
            case "UPDATE": sb.append("修改"); break;
            case "DELETE": sb.append("删除"); break;
            default:       sb.append("操作"); break;
        }
        String label = TARGET_LABEL_MAP.getOrDefault(targetType, targetType);
        sb.append(label).append(" — ").append(uri);
        if (errorMsg != null) {
            // 错误信息只取前 100 字符，避免敏感数据泄露
            String safe = errorMsg.length() > 100 ? errorMsg.substring(0, 100) + "..." : errorMsg;
            sb.append(" [失败: ").append(safe).append("]");
        }
        return sb.toString();
    }

}
