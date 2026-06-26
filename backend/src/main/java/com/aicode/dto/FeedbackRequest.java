package com.aicode.dto;

/**
 * 反馈请求 DTO
 */
public class FeedbackRequest {

    private String feedbackType;  // REVIEW / ANSWER / PATH
    private Long targetId;
    private Integer rating;
    private String comment;

    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
