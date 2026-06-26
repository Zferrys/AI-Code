package com.aicode.dto;

/**
 * 仪表盘统计数据 VO
 */
public class DashboardStatsVO {

    private long userCount;
    private long reviewCount;
    private long questionCount;
    private long pathCount;
    private double avgRating;

    public long getUserCount() { return userCount; }
    public void setUserCount(long userCount) { this.userCount = userCount; }

    public long getReviewCount() { return reviewCount; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }

    public long getQuestionCount() { return questionCount; }
    public void setQuestionCount(long questionCount) { this.questionCount = questionCount; }

    public long getPathCount() { return pathCount; }
    public void setPathCount(long pathCount) { this.pathCount = pathCount; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
}
