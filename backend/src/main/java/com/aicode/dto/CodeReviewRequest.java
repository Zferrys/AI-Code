package com.aicode.dto;

/**
 * 提交代码审查请求 DTO
 */
public class CodeReviewRequest {

    private String title;
    private String codeContent;
    private String language;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCodeContent() { return codeContent; }
    public void setCodeContent(String codeContent) { this.codeContent = codeContent; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
