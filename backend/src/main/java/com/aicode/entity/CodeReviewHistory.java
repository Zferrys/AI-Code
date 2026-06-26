package com.aicode.entity;

import java.util.Date;

/**
 * 代码审查历史版本实体
 */
public class CodeReviewHistory {

    private Long id;
    private Long reviewId;
    private Integer version;
    private String codeContent;
    private String reviewResult;
    private Integer aiResponseTime;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getCodeContent() { return codeContent; }
    public void setCodeContent(String codeContent) { this.codeContent = codeContent; }

    public String getReviewResult() { return reviewResult; }
    public void setReviewResult(String reviewResult) { this.reviewResult = reviewResult; }

    public Integer getAiResponseTime() { return aiResponseTime; }
    public void setAiResponseTime(Integer aiResponseTime) { this.aiResponseTime = aiResponseTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
