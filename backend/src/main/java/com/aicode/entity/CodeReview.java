package com.aicode.entity;

import java.util.Date;

/**
 * 代码审查记录实体
 */
public class CodeReview {

    private Long id;
    private Long userId;
    private String title;
    private String language;
    private String codeContent;
    private String reviewResult;
    private Integer qualityScore;
    private Integer bugCount;
    private Integer suggestionCount;
    private String status;       // PENDING / COMPLETED / FAILED
    private Integer aiResponseTime;
    private Integer isHelpful;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCodeContent() { return codeContent; }
    public void setCodeContent(String codeContent) { this.codeContent = codeContent; }

    public String getReviewResult() { return reviewResult; }
    public void setReviewResult(String reviewResult) { this.reviewResult = reviewResult; }

    public Integer getQualityScore() { return qualityScore; }
    public void setQualityScore(Integer qualityScore) { this.qualityScore = qualityScore; }

    public Integer getBugCount() { return bugCount; }
    public void setBugCount(Integer bugCount) { this.bugCount = bugCount; }

    public Integer getSuggestionCount() { return suggestionCount; }
    public void setSuggestionCount(Integer suggestionCount) { this.suggestionCount = suggestionCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAiResponseTime() { return aiResponseTime; }
    public void setAiResponseTime(Integer aiResponseTime) { this.aiResponseTime = aiResponseTime; }

    public Integer getIsHelpful() { return isHelpful; }
    public void setIsHelpful(Integer isHelpful) { this.isHelpful = isHelpful; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
