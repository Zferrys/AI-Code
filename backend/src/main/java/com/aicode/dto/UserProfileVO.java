package com.aicode.dto;

import java.util.List;

/**
 * 用户资料视图对象（含扩展信息和技术标签）
 * 扁平结构：常用用户字段直接挂在顶层，避免前端层层访问
 */
public class UserProfileVO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private Integer status;

    private UserVO user;
    private String realName;
    private String school;
    private String major;
    private String grade;
    private String bio;
    private String githubUrl;
    private String experienceLevel;
    private List<TagVO> tags;
    private long reviewCount;
    private long qaCount;

    public UserVO getUser() { return user; }
    public void setUser(UserVO user) { this.user = user; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public List<TagVO> getTags() { return tags; }
    public void setTags(List<TagVO> tags) { this.tags = tags; }

    public long getReviewCount() { return reviewCount; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }

    public long getQaCount() { return qaCount; }
    public void setQaCount(long qaCount) { this.qaCount = qaCount; }
}
