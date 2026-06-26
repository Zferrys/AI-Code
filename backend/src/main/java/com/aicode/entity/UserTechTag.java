package com.aicode.entity;

/**
 * 用户技术标签关联实体
 */
public class UserTechTag {

    private Long id;
    private Long userId;
    private Long tagId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }
}
