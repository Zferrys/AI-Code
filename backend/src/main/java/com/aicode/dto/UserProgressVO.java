package com.aicode.dto;

/**
 * 用户学习进度视图对象
 */
public class UserProgressVO {

    private Long pathId;
    private int totalCourses;
    private int completedCourses;
    private double percentage;

    public UserProgressVO() {}

    public UserProgressVO(Long pathId, int totalCourses, int completedCourses) {
        this.pathId = pathId;
        this.totalCourses = totalCourses;
        this.completedCourses = completedCourses;
        this.percentage = totalCourses > 0 ? Math.round((double) completedCourses / totalCourses * 100) : 0;
    }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public int getTotalCourses() { return totalCourses; }
    public void setTotalCourses(int totalCourses) { this.totalCourses = totalCourses; }

    public int getCompletedCourses() { return completedCourses; }
    public void setCompletedCourses(int completedCourses) { this.completedCourses = completedCourses; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}
