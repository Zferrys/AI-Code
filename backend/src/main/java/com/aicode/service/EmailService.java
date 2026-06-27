package com.aicode.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private org.springframework.mail.javamail.JavaMailSenderImpl mailSenderImpl;

    private String fromEmail;

    @javax.annotation.PostConstruct
    public void init() {
        if (mailSenderImpl != null) {
            this.fromEmail = mailSenderImpl.getUsername();
        } else {
            this.fromEmail = "1978738217@qq.com";
        }
        log.info("邮件服务初始化完成，发件人: {}", this.fromEmail);
    }

    /**
     * 发送简单文本邮件
     */
    public void send(String to, String subject, String text) {
        try {
            // XSS 防护：过滤邮件标题和内容的 HTML/JS
            String safeSubject = Jsoup.clean(subject != null ? subject : "", Safelist.none());
            String safeText = Jsoup.clean(text != null ? text : "", Safelist.none());

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(safeSubject);
            msg.setText(safeText);
            mailSender.send(msg);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("邮件发送失败: to={}, subject={}", to, subject, e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送 HTML 邮件
     */
    public void sendHtml(String to, String subject, String html) {
        try {
            javax.mail.internet.MimeMessage msg = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper =
                    new org.springframework.mail.javamail.MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("HTML邮件发送成功: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("HTML邮件发送失败: to={}, subject={}", to, subject, e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }
}
