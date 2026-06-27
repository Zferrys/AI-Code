package com.aicode.controller;

import com.aicode.dto.ApiResponse;
import com.aicode.service.EmailVerificationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮箱验证控制器
 */
@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    public EmailController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/send-code")
    public ApiResponse<?> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            return ApiResponse.error(400, "邮箱格式不正确");
        }
        try {
            boolean sent = emailVerificationService.sendCode(email);
            if (sent) {
                return ApiResponse.success("验证码已发送");
            }
            return ApiResponse.error(429, "发送过于频繁或邮箱已被锁定，请稍后再试");
        } catch (Exception e) {
            return ApiResponse.error(500, "验证码发送失败，请稍后重试");
        }
    }

    /**
     * 校验邮箱验证码
     */
    @PostMapping("/verify-code")
    public ApiResponse<?> verifyCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        if (email == null || code == null) {
            return ApiResponse.error(400, "参数不完整");
        }
        boolean ok = emailVerificationService.verifyCode(email, code);
        if (ok) {
            return ApiResponse.success("邮箱验证通过");
        }
        return ApiResponse.error(400, "验证码错误或已过期");
    }
}
