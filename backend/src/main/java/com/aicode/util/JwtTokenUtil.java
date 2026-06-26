package com.aicode.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT 工具类。密钥从环境变量 JWT_SECRET 或系统属性 jwt.secret 读取。
 * 未配置时直接启动失败，避免随机密钥导致重启后所有 Token 静默失效。
 */
@Component
public class JwtTokenUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    private static final long EXPIRATION = 2 * 60 * 60 * 1000L; // 2小时（会话级别）

    private String secret;
    private Key signingKey;

    @PostConstruct
    public void init() {
        String prop = System.getProperty("jwt.secret");
        String env = System.getenv("JWT_SECRET");
        secret = (prop != null && !prop.isEmpty()) ? prop
               : (env != null && !env.isEmpty()) ? env
               : null;

        if (secret == null) {
            throw new IllegalStateException(
                "JWT_SECRET 未配置！请设置环境变量 JWT_SECRET 或系统属性 jwt.secret。" +
                "例如: set JWT_SECRET=your-256-bit-secret");
        }

        if (secret.length() < 32) {
            throw new IllegalStateException(
                "JWT_SECRET 长度不足！当前 " + secret.length() + " 位，至少需要 32 位。" +
                "请使用更长的密钥，例如: set JWT_SECRET=" + UUID.randomUUID().toString().replace("-", ""));
        }

        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT 密钥已加载");
    }

    public String generateToken(Integer userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) return false;
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) return null;
        Object id = claims.get("userId");
        return id instanceof Integer ? (Integer) id : (id != null ? Integer.valueOf(String.valueOf(id)) : null);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims == null ? null : claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        if (claims == null) return null;
        Object r = claims.get("role");
        return r instanceof String ? (String) r : (r != null ? String.valueOf(r) : null);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
