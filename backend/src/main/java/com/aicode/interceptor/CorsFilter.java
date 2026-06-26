package com.aicode.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CORS 跨域过滤器
 * 允许的来源通过环境变量 CORS_ALLOWED_ORIGINS 或系统属性 cors.allowed.origins 配置（逗号分隔）。
 * 未配置时默认允许 localhost 开发环境。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorsFilter.class);

    private Set<String> allowedOrigins = Collections.emptySet();

    @PostConstruct
    public void init() {
        String prop = System.getProperty("cors.allowed.origins");
        String env = System.getenv("CORS_ALLOWED_ORIGINS");
        String raw = (prop != null && !prop.isEmpty()) ? prop : (env != null ? env : null);
        if (raw != null && !raw.trim().isEmpty()) {
            allowedOrigins = Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(HashSet::new));
            log.info("CORS allowed origins: {}", allowedOrigins);
        } else {
            // 检测是否为生产环境
            boolean isProduction = "true".equalsIgnoreCase(System.getenv("PRODUCTION"))
                    || "true".equalsIgnoreCase(System.getProperty("production"));
            if (isProduction) {
                throw new IllegalStateException(
                    "生产环境必须配置 CORS_ALLOWED_ORIGINS 环境变量！");
            }
            // 开发环境默认允许 localhost 来源
            allowedOrigins = new HashSet<>(Arrays.asList(
                    "http://localhost:3000", "http://127.0.0.1:3000",
                    "http://localhost:3001", "http://127.0.0.1:3001",
                    "http://localhost:8080", "http://127.0.0.1:8080",
                    "http://localhost:8081", "http://127.0.0.1:8081"));
            log.warn("No CORS origins configured — defaulting to localhost. Set CORS_ALLOWED_ORIGINS for production!");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String origin = req.getHeader("Origin");

        boolean originAllowed = origin != null && allowedOrigins.contains(origin);

        if (originAllowed) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Vary", "Origin");
            res.setHeader("Access-Control-Allow-Credentials", "true");
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            res.setHeader("Access-Control-Max-Age", "3600");
        } else {
            res.setHeader("Access-Control-Allow-Credentials", "false");
            if (origin != null) {
                log.warn("CORS request from disallowed origin: {}", origin);
            }
        }

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            if (originAllowed) {
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {}
    @Override
    public void destroy() {}
}
