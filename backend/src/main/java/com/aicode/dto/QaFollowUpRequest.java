package com.aicode.dto;

/**
 * 追问请求 DTO
 */
public class QaFollowUpRequest {

    private Long questionId;
    private String content;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
