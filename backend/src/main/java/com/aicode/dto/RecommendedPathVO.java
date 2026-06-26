package com.aicode.dto;

import java.util.List;

/**
 * 推荐学习路径视图对象
 */
public class RecommendedPathVO {

    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private Integer estimatedDays;
    private String coverImage;
    private Integer courseCount;
    private double matchScore;       // 匹配分数（0-100）
    private List<String> matchTags;  // 匹配的标签

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

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public List<String> getMatchTags() { return matchTags; }
    public void setMatchTags(List<String> matchTags) { this.matchTags = matchTags; }
}
