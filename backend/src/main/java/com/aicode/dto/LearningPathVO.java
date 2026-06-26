package com.aicode.dto;

import java.util.Date;
import java.util.List;

/**
 * 学习路径视图对象（含课程列表和用户进度）
 */
public class LearningPathVO {

    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private Integer estimatedDays;
    private String coverImage;
    private Integer courseCount;
    private String status;
    private Date createTime;
    private List<LearningPathCourseVO> courses;
    private UserProgressVO userProgress;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(Integer estimatedDays) { this.estimatedDays = estimatedDays; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public Integer getCourseCount() { return courseCount; }
    public void setCourseCount(Integer courseCount) { this.courseCount = courseCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public List<LearningPathCourseVO> getCourses() { return courses; }
    public void setCourses(List<LearningPathCourseVO> courses) { this.courses = courses; }

    public UserProgressVO getUserProgress() { return userProgress; }
    public void setUserProgress(UserProgressVO userProgress) { this.userProgress = userProgress; }
}
