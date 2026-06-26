package com.aicode.service;

import com.aicode.entity.SysLog;
import com.aicode.mapper.SysLogMapper;
import com.aicode.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 系统操作日志服务
 * 记录用户关键操作到 sys_log 表
 */
@Service
public class SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    /**
     * 从 request 中提取用户 ID
     */
    private Long getUserId(HttpServletRequest request) {
        if (request == null) return null;
        Object attr = request.getAttribute("userId");
        return attr instanceof Long ? (Long) attr : null;
    }

    /**
     * 记录操作日志
     * @param request     HTTP 请求（可为 null，此时 userId 和 IP 为 null）
     * @param actionType  操作类型（CREATE / UPDATE / DELETE / LOGIN 等）
     * @param targetType  目标类型（USER / PATH / CONFIG / COURSE 等）
     * @param targetId    目标 ID（可为 null）
     * @param description 描述
     */
    @Async("aiExecutor")
    public void log(HttpServletRequest request, String actionType, String targetType, Long targetId, String description) {
        SysLog log = new SysLog();
        log.setUserId(getUserId(request));
        log.setActionType(actionType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDescription(description);
        log.setIpAddress(WebUtil.getClientIp(request));
        log.setCreateTime(new Date());
        sysLogMapper.insert(log);
    }

    /**
     * 简化：从 Controller 方法直接调用，自动从请求上下文获取用户/IP
     */
    public void log(String actionType, String targetType, Long targetId, String description) {
        HttpServletRequest request = null;
        try {
            request = ((org.springframework.web.context.request.ServletRequestAttributes)
                    org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception ignored) {
            // 非 Web 上下文（如测试）时静默跳过
        }
        log(request, actionType, targetType, targetId, description);
    }
}
