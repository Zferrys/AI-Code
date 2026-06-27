package com.aicode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮箱验证码服务
 * 生成6位数字验证码，支持频率限制、失败锁定、自动过期清理
 */
@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    @Autowired
    private EmailService emailService;

    private final ConcurrentHashMap<String, CodeEntry> store = new ConcurrentHashMap<>();

    /** 发送频率限制：同一邮箱 60 秒内不可重复发送 */
    private static final long SEND_INTERVAL = 60 * 1000L;
    /** 验证码有效期 */
    private static final long CODE_TTL = 5 * 60 * 1000L;
    /** 最大失败次数，超限立即作废并锁定 */
    private static final int MAX_FAILS = 5;
    /** 邮箱锁定时间 */
    private static final long LOCK_TTL = 30 * 60 * 1000L;
    /** 惰性清理阈值，超过此数量触发过期条目清理 */
    private static final int CLEAN_THRESHOLD = 50;

    private final SecureRandom random = new SecureRandom();

    private static class CodeEntry {
        final String code;
        final long createdAt;
        int failCount;
        boolean locked;
        long lockedUntil;

        CodeEntry(String code) {
            this.code = code;
            this.createdAt = System.currentTimeMillis();
            this.failCount = 0;
            this.locked = false;
        }

        boolean isExpired() { return System.currentTimeMillis() - createdAt > CODE_TTL; }

        boolean isLocked() { return locked && System.currentTimeMillis() < lockedUntil; }

        /** 是否可重新发送（已验证、过期、或锁定都算不可发送） */
        boolean canResend() {
            if (locked) return false;
            if (isExpired()) return false;
            return System.currentTimeMillis() - createdAt > SEND_INTERVAL;
        }

        void recordFail() {
            failCount++;
            if (failCount >= MAX_FAILS) {
                locked = true;
                lockedUntil = System.currentTimeMillis() + LOCK_TTL;
                log.warn("验证码连续{}次失败，邮箱已锁定30分钟", failCount);
            }
        }
    }

    /**
     * 发送验证码到邮箱（含频率限制）
     * @return true=发送成功, false=发送过于频繁
     */
    public boolean sendCode(String email) {
        // 检查是否在频率限制内
        CodeEntry existing = store.get(email);
        if (existing != null && !existing.isExpired()) {
            if (existing.isLocked()) {
                log.warn("邮箱已被锁定，拒绝发送验证码: email={}", email);
                return false;
            }
            long elapsed = System.currentTimeMillis() - existing.createdAt;
            if (elapsed < SEND_INTERVAL) {
                log.warn("发送过于频繁: email={}, 已过{}s", email, elapsed / 1000);
                return false;
            }
        }

        // 生成6位数字验证码（SecureRandom）
        String code = String.format("%06d", random.nextInt(1000000));

        // 存储验证码
        store.put(email, new CodeEntry(code));

        // 发送邮件
        String subject = "AI-Code 邮箱验证";
        String text = "您好！\n\n您的 AI-Code 邮箱验证码为：\n\n" +
                code + "\n\n验证码 5 分钟内有效，请尽快完成验证。\n\n如非本人操作，请忽略此邮件。";

        emailService.send(email, subject, text);
        log.info("验证码已发送: email={}", email);

        // 惰性清理过期条目
        if (store.size() > CLEAN_THRESHOLD) {
            store.entrySet().removeIf(e -> e.getValue().isExpired());
        }
        return true;
    }

    /**
     * 验证邮箱验证码
     * @return true 通过
     */
    public boolean verifyCode(String email, String code) {
        CodeEntry entry = store.get(email);
        if (entry == null || entry.isExpired()) {
            store.remove(email);
            return false;
        }
        if (entry.isLocked()) {
            log.warn("邮箱已被锁定，拒绝验证: email={}", email);
            return false;
        }
        if (!entry.code.equals(code)) {
            entry.recordFail();
            return false;
        }
        // 验证通过，移除记录
        store.remove(email);
        return true;
    }
}
