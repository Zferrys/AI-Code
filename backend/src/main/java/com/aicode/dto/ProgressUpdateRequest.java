package com.aicode.dto;

/**
 * 学习进度更新请求 DTO
 */
public class ProgressUpdateRequest {

    private Long courseId;
    private Long pathId;

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }
}
