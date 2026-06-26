package com.aicode.dto;

/**
 * 收藏请求 DTO
 */
public class FavoriteRequest {

    private String contentType;  // QUESTION / COURSE / PATH
    private Long contentId;

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
}
