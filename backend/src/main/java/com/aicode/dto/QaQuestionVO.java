package com.aicode.dto;

import java.util.Date;
import java.util.List;

/**
 * 问题视图对象（含回答列表）
 */
public class QaQuestionVO {

    private Long id;
    private String title;
    private String content;
    private String category;
    private String tags;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer answerCount;
    private Integer isResolved;
    private String status;
    private String username;
    private Date createTime;
    private Date updateTime;
    private List<QaAnswerVO> answers;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }

    public Integer getAnswerCount() { return answerCount; }
    public void setAnswerCount(Integer answerCount) { this.answerCount = answerCount; }

    public Integer getIsResolved() { return isResolved; }
    public void setIsResolved(Integer isResolved) { this.isResolved = isResolved; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public List<QaAnswerVO> getAnswers() { return answers; }
    public void setAnswers(List<QaAnswerVO> answers) { this.answers = answers; }
}
