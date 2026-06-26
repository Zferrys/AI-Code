package com.aicode.interceptor;

import com.aicode.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 可选认证拦截器 — 用于公开但可个性化的路径（如学习路径、排行榜）
 * 如果有有效 token 则提取用户信息，无 token 也不阻塞
 */
@Component
public class OptionalAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(OptionalAuthInterceptor.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return true; // 无 token，继续但不设 userId
        }
        try {
            String token = authHeader.substring(7);
            if (jwtTokenUtil.validateToken(token)) {
                Integer userIdInt = jwtTokenUtil.getUserIdFromToken(token);
                if (userIdInt != null) {
                    request.setAttribute("userId", userIdInt.longValue());
                }
                request.setAttribute("username", jwtTokenUtil.getUsernameFromToken(token));
                request.setAttribute("role", jwtTokenUtil.getRoleFromToken(token));
            }
        } catch (Exception e) {
            log.debug("可选认证解析失败: {}", e.getMessage());
        }
        return true;
    }
}
