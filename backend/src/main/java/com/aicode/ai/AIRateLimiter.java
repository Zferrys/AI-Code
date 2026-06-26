package com.aicode.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI API 限流器（令牌桶算法）
 * 限制每分钟的 API 调用次数，防止超限导致被封
 */
@Component
public class AIRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(AIRateLimiter.class);

    /** 默认每分钟调用上限 */
    private static final int DEFAULT_MAX_PER_MINUTE = 30;

    private final int maxRequestsPerMinute;
    private final AtomicInteger counter;
    private long windowStart;

    public AIRateLimiter() {
        this.maxRequestsPerMinute = DEFAULT_MAX_PER_MINUTE;
        this.counter = new AtomicInteger(0);
        this.windowStart = System.currentTimeMillis();
    }

    /**
     * 尝试获取调用许可
     * @return true 允许调用，false 超出限流
     */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        // 如果超过 1 分钟窗口，重置计数器
        if (now - windowStart > TimeUnit.MINUTES.toMillis(1)) {
            counter.set(0);
            windowStart = now;
        }

        int count = counter.incrementAndGet();
        if (count > maxRequestsPerMinute) {
            log.warn("AI API 调用频率超限: {}/分钟", maxRequestsPerMinute);
            return false;
        }
        return true;
    }

    /**
     * 获取当前分钟内的调用次数
     */
    public int getCurrentCount() {
        return counter.get();
    }

    /**
     * 获取每分钟调用上限
     */
    public int getMaxPerMinute() {
        return maxRequestsPerMinute;
    }
}
