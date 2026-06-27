package com.aicode.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI API 限流器（多维度令牌桶）
 * 支持 per-User、per-IP、全局三级限流，防止单用户耗尽额度
 */
@Component
public class AIRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(AIRateLimiter.class);

    /** 每用户每分钟上限 */
    private static final int DEFAULT_USER_MAX = 10;
    /** 每 IP 每分钟上限 */
    private static final int DEFAULT_IP_MAX = 20;
    /** 全局每分钟上限 */
    private static final int DEFAULT_GLOBAL_MAX = 60;

    private final int globalMax;
    private final AtomicInteger globalCounter;
    private long globalWindowStart;

    private final ConcurrentHashMap<String, UserBucket> userBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserBucket> ipBuckets = new ConcurrentHashMap<>();

    private static class UserBucket {
        final AtomicInteger counter = new AtomicInteger(0);
        long windowStart = System.currentTimeMillis();

        synchronized boolean tryAcquire(int max) {
            long now = System.currentTimeMillis();
            if (now - windowStart > TimeUnit.MINUTES.toMillis(1)) {
                counter.set(0);
                windowStart = now;
            }
            return counter.incrementAndGet() <= max;
        }
    }

    public AIRateLimiter() {
        this.globalMax = DEFAULT_GLOBAL_MAX;
        this.globalCounter = new AtomicInteger(0);
        this.globalWindowStart = System.currentTimeMillis();
    }

    /**
     * 尝试获取调用许可（多维度限流）
     * @param userId 用户ID，可为 null
     * @param ip    请求 IP，可为 null
     * @return true 允许调用
     */
    public boolean tryAcquire(Long userId, String ip) {
        // 1. 全局限流
        boolean globalOk;
        synchronized (this) {
            long now = System.currentTimeMillis();
            if (now - globalWindowStart > TimeUnit.MINUTES.toMillis(1)) {
                globalCounter.set(0);
                globalWindowStart = now;
            }
            globalOk = globalCounter.incrementAndGet() <= globalMax;
        }
        if (!globalOk) {
            log.warn("AI API 全局限流触发: 已达 {}次/分钟", globalMax);
            return false;
        }

        // 2. 每用户限流
        if (userId != null) {
            UserBucket ub = userBuckets.computeIfAbsent(String.valueOf(userId), k -> new UserBucket());
            if (!ub.tryAcquire(DEFAULT_USER_MAX)) {
                log.warn("AI API 用户限流触发: userId={}, 上限 {}次/分钟", userId, DEFAULT_USER_MAX);
                return false;
            }
        }

        // 3. 每 IP 限流
        if (ip != null) {
            UserBucket ib = ipBuckets.computeIfAbsent(ip, k -> new UserBucket());
            if (!ib.tryAcquire(DEFAULT_IP_MAX)) {
                log.warn("AI API IP 限流触发: ip={}, 上限 {}次/分钟", ip, DEFAULT_IP_MAX);
                return false;
            }
        }

        return true;
    }

    /**
     * 简版 tryAcquire（兼容旧调用）
     */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        if (now - globalWindowStart > TimeUnit.MINUTES.toMillis(1)) {
            globalCounter.set(0);
            globalWindowStart = now;
        }
        return globalCounter.incrementAndGet() <= globalMax;
    }

    public int getGlobalMax() { return globalMax; }
}
