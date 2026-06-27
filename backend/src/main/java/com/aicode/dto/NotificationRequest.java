package com.aicode.dto;

/**
 * 管理员通知请求
 */
public class NotificationRequest {

    /** 接收方式：all=全部用户，single=单个用户 */
    private String type;

    /** 单个用户时指定邮箱 */
    private String email;

    /** 通知标题 */
    private String subject;

    /** 通知内容 */
    private String content;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
