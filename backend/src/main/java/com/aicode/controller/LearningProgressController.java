package com.aicode.controller;

import com.aicode.dto.ApiResponse;
import com.aicode.dto.ProgressUpdateRequest;
import com.aicode.dto.UserProgressVO;
import com.aicode.entity.UserLearningProgress;
import com.aicode.service.UserLearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 学习进度控制器
 */
@RestController
@RequestMapping("/api/progress")
public class LearningProgressController {

    @Autowired
    private UserLearningProgressService progressService;

    /**
     * 开始学习课程
     */
    @PostMapping("/start")
    public ApiResponse<?> startCourse(HttpServletRequest request,
                                       @RequestBody ProgressUpdateRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        progressService.startCourse(userId, req.getCourseId(), req.getPathId());
        return ApiResponse.success("开始学习", null);
    }

    /**
     * 完成课程
     */
    @PostMapping("/complete")
    public ApiResponse<?> completeCourse(HttpServletRequest request,
                                          @RequestBody ProgressUpdateRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        progressService.completeCourse(userId, req.getCourseId(), req.getPathId());
        return ApiResponse.success("已完成", null);
    }

    /**
     * 取消完成（重置为未开始）
     */
    @PostMapping("/reset")
    public ApiResponse<?> resetCourse(HttpServletRequest request,
                                      @RequestBody ProgressUpdateRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        progressService.resetCourse(userId, req.getCourseId());
        return ApiResponse.success("已重置", null);
    }

    /**
     * 获取用户在某个路径下的进度
     */
    @GetMapping("/path/{pathId}")
    public ApiResponse<UserProgressVO> getPathProgress(HttpServletRequest request,
                                                        @PathVariable Long pathId) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResponse.success(progressService.getPathProgress(userId, pathId));
    }

    /**
     * 获取用户所有进度
     */
    @GetMapping("/my")
    public ApiResponse<List<UserLearningProgress>> getMyProgress(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResponse.success(progressService.getAllProgress(userId));
    }
}
