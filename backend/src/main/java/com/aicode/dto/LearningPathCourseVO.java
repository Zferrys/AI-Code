package com.aicode.dto;

import java.util.List;

/**
 * 学习路径课程视图对象
 */
public class LearningPathCourseVO {

    private Long id;
    private Long pathId;
    private String title;
    private String description;
    private Integer orderIndex;
    private String contentType;
    private String contentUrl;
    private String contentMarkdown;
    private Integer estimatedMinutes;
    private String userStatus;    // NOT_STARTED / IN_PROGRESS / COMPLETED
    private List<CourseResourceVO> resources;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }

    public String getContentMarkdown() { return contentMarkdown; }
    public void setContentMarkdown(String contentMarkdown) { this.contentMarkdown = contentMarkdown; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getUserStatus() { return userStatus; }
    public void setUserStatus(String userStatus) { this.userStatus = userStatus; }

    public List<CourseResourceVO> getResources() { return resources; }
    public void setResources(List<CourseResourceVO> resources) { this.resources = resources; }
}
