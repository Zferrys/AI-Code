package com.aicode.controller;

import com.aicode.dto.ApiResponse;
import com.aicode.dto.CaptchaResponse;
import com.aicode.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器 — 生成数学算式图片验证码
 */
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping
    public ApiResponse<CaptchaResponse> getCaptcha() {
        return ApiResponse.success(captchaService.generate());
    }
}
