package com.aicode.dto;

import java.util.Date;

/**
 * 回答视图对象
 */
public class QaAnswerVO {

    private Long id;
    private String type;       // AI / USER
    private String content;
    private Integer rating;
    private Integer isAccepted;
    private Integer aiResponseTime;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Integer getIsAccepted() { return isAccepted; }
    public void setIsAccepted(Integer isAccepted) { this.isAccepted = isAccepted; }

    public Integer getAiResponseTime() { return aiResponseTime; }
    public void setAiResponseTime(Integer aiResponseTime) { this.aiResponseTime = aiResponseTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
