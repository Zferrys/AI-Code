package com.aicode.entity;

import java.util.Date;

/**
 * 收藏实体（多态）
 */
public class Favorite {

    private Long id;
    private Long userId;
    private String contentType;  // QUESTION / COURSE / PATH
    private Long contentId;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
