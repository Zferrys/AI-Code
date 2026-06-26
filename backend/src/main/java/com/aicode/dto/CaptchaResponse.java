package com.aicode.dto;

/**
 * 验证码响应
 */
public class CaptchaResponse {

    private String captchaId;
    private String captchaImage; // data:image/png;base64,...

    public CaptchaResponse() {}

    public CaptchaResponse(String captchaId, String captchaImage) {
        this.captchaId = captchaId;
        this.captchaImage = captchaImage;
    }

    public String getCaptchaId() { return captchaId; }
    public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
    public String getCaptchaImage() { return captchaImage; }
    public void setCaptchaImage(String captchaImage) { this.captchaImage = captchaImage; }
}
