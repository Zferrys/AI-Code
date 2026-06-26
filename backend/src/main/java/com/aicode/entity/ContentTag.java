package com.aicode.entity;

/**
 * 内容标签关联实体（多态）
 */
public class ContentTag {

    private Long id;
    private Long tagId;
    private String contentType;  // COURSE / PATH / QUESTION / REVIEW
    private Long contentId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
}
