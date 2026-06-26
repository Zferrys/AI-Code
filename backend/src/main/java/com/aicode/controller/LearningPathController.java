package com.aicode.controller;

import com.aicode.dto.*;
import com.aicode.service.LearningPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 学习路径控制器
 */
@RestController
@RequestMapping("/api/learning-paths")
public class LearningPathController {

    @Autowired
    private LearningPathService learningPathService;

    /**
     * 获取所有已发布的路径（带分页）
     */
    @GetMapping
    public ApiResponse<?> getAllPaths(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        List<LearningPathVO> list = learningPathService.getPublishedPathsPaged(page, pageSize);
        long total = learningPathService.countPublishedPaths();
        return ApiResponse.success(ApiResponse.page(list, total, page, pageSize));
    }

    /**
     * 获取推荐路径（由 OptionalAuthInterceptor 设置 userId）
     */
    @GetMapping("/recommended")
    public ApiResponse<List<RecommendedPathVO>> getRecommended(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResponse.success(learningPathService.getRecommendedPaths(userId));
    }

    /**
     * 获取路径详情（由 OptionalAuthInterceptor 设置 userId）
     */
    @GetMapping("/{id}")
    public ApiResponse<LearningPathVO> getDetail(@PathVariable Long id,
                                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResponse.success(learningPathService.getPathDetail(id, userId));
    }

    /**
     * 获取课程详情（含 Markdown 内容和用户进度）
     */
    @GetMapping("/course/{courseId}")
    public ApiResponse<LearningPathCourseVO> getCourse(@PathVariable Long courseId,
                                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResponse.success(learningPathService.getCourseDetailVO(courseId, userId));
    }

    /**
     * 获取课程资源列表
     */
    @GetMapping("/course/{courseId}/resources")
    public ApiResponse<List<CourseResourceVO>> getCourseResources(@PathVariable Long courseId) {
        return ApiResponse.success(learningPathService.getCourseResources(courseId));
    }
}
