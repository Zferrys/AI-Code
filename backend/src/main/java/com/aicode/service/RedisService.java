package com.aicode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Supplier;

/**
 * Redis 缓存服务
 * 提供缓存读写和 getOrSet 模板方法
 */
@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private JedisPool jedisPool;

    /** 默认缓存过期时间（30 分钟） */
    private static final int DEFAULT_TTL = 1800;

    /**
     * 获取缓存值
     */
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            log.warn("Redis get 失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 设置缓存
     */
    public void set(String key, String value, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlSeconds, value);
        } catch (Exception e) {
            log.warn("Redis set 失败: key={}", key, e);
        }
    }

    /**
     * 设置缓存（使用默认 TTL）
     */
    public void set(String key, String value) {
        set(key, value, DEFAULT_TTL);
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            log.warn("Redis delete 失败: key={}", key, e);
        }
    }

    /**
     * 获取或设置缓存（缓存穿透保护）
     * @param key 缓存键
     * @param loader 加载数据的回调
     * @param ttlSeconds 过期时间
     * @return 缓存值或回调加载的值
     */
    public String getOrSet(String key, Supplier<String> loader, int ttlSeconds) {
        String cached = get(key);
        if (cached != null) {
            return cached;
        }
        // 双检锁防止缓存击穿
        synchronized (this) {
            cached = get(key);
            if (cached != null) {
                return cached;
            }
            String value = loader.get();
            if (value != null) {
                set(key, value, ttlSeconds);
            }
            return value;
        }
    }

    public String getOrSet(String key, Supplier<String> loader) {
        return getOrSet(key, loader, DEFAULT_TTL);
    }
}
