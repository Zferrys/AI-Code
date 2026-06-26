package com.aicode.dto;

/**
 * 代码审查状态视图对象（供前端轮询）
 */
public class CodeReviewStatusVO {

    private Long id;
    private String status;     // PENDING / COMPLETED / FAILED
    private Integer qualityScore;
    private String message;
    private Integer elapsedSeconds;
    private String stage;      // analyzing / detecting / summarizing

    public CodeReviewStatusVO() {}

    public CodeReviewStatusVO(Long id, String status, Integer qualityScore, String message,
                              Integer elapsedSeconds, String stage) {
        this.id = id;
        this.status = status;
        this.qualityScore = qualityScore;
        this.message = message;
        this.elapsedSeconds = elapsedSeconds;
        this.stage = stage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getQualityScore() { return qualityScore; }
    public void setQualityScore(Integer qualityScore) { this.qualityScore = qualityScore; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getElapsedSeconds() { return elapsedSeconds; }
    public void setElapsedSeconds(Integer elapsedSeconds) { this.elapsedSeconds = elapsedSeconds; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
}
