package com.aicode.service;

import com.aicode.dto.CaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数学验证码服务
 * 生成算式图片，存储答案（内存，3分钟过期），校验用户输入
 */
@Service
public class CaptchaService {

    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 50;
    private static final long CAPTCHA_TTL = 3 * 60 * 1000L; // 3分钟
    private static final int CLEAN_THRESHOLD = 1000; // 条目超1000触发清理

    private final ConcurrentHashMap<String, CaptchaEntry> store = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private static class CaptchaEntry {
        final int answer;
        final long createdAt;
        CaptchaEntry(int answer) { this.answer = answer; this.createdAt = System.currentTimeMillis(); }
        boolean isExpired() { return System.currentTimeMillis() - createdAt > CAPTCHA_TTL; }
    }

    /**
     * 生成验证码并创建AI视觉抽象图像
     * @return 验证码响应（ID + base64图片）
     */
    public CaptchaResponse generate() {
        // 随机生成算式
        int a = random.nextInt(50) + 1;
        int b = random.nextInt(50) + 1;
        boolean isAdd = random.nextBoolean();
        String text;
        int answer;
        if (isAdd) {
            answer = a + b;
            text = a + " + " + b + " = ?";
        } else {
            // 减法保证非负
            if (a < b) { int t = a; a = b; b = t; }
            answer = a - b;
            text = a + " - " + b + " = ?";
        }

        // 绘制图片
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("Arial", Font.BOLD, 22));

            // 干扰线
            g.setColor(new Color(200, 200, 200));
            for (int i = 0; i < 4; i++) {
                int x1 = random.nextInt(WIDTH);
                int y1 = random.nextInt(HEIGHT);
                int x2 = random.nextInt(WIDTH);
                int y2 = random.nextInt(HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }

            // 噪点
            g.setColor(new Color(180, 180, 180));
            for (int i = 0; i < 30; i++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                g.fillRect(x, y, 1, 1);
            }

            // 算式文字
            g.setColor(new Color(30, 30, 30));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (WIDTH - textWidth) / 2;
            int y = (HEIGHT + fm.getAscent()) / 2 - 4;
            g.drawString(text, x, y);
        } finally {
            g.dispose();
        }

        // 编码为 PNG base64
        String base64;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("验证码图片生成失败", e);
            throw new RuntimeException("验证码生成失败");
        }

        String captchaId = UUID.randomUUID().toString();
        store.put(captchaId, new CaptchaEntry(answer));

        // 惰性清理过期条目
        if (store.size() > CLEAN_THRESHOLD) {
            store.entrySet().removeIf(e -> e.getValue().isExpired());
        }

        return new CaptchaResponse(captchaId, "data:image/png;base64," + base64);
    }

    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param answer 用户输入的答案
     * @return true 通过，false 失败
     */
    public boolean validate(String captchaId, Integer answer) {
        if (captchaId == null || answer == null) return false;
        CaptchaEntry entry = store.remove(captchaId);
        if (entry == null || entry.isExpired()) return false;
        return entry.answer == answer;
    }
}
