package com.aicode.entity;

import java.util.Date;

/**
 * 课程资源实体（外部链接、视频、练习、参考资料等）
 */
public class CourseResource {

    private Long id;
    private Long courseId;
    private String title;
    private String type;       // LINK / VIDEO / EXERCISE / REFERENCE / DOWNLOAD
    private String url;
    private String description;
    private Integer sortOrder;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
