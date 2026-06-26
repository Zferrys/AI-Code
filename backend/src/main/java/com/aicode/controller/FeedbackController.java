package com.aicode.controller;

import com.aicode.dto.*;
import com.aicode.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired private FeedbackService feedbackService;

    @PostMapping
    public ApiResponse<?> submit(HttpServletRequest request, @RequestBody FeedbackRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        feedbackService.submitFeedback(userId, req.getFeedbackType(), req.getTargetId(), req.getRating(), req.getComment());
        return ApiResponse.success("提交成功", null);
    }

    @GetMapping("/target/{type}/{id}")
    public ApiResponse<?> getByTarget(@PathVariable String type, @PathVariable Long id,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        String role = (String) request.getAttribute("role");
        // 管理员可查所有反馈，普通用户只能查自己的
        if ("ADMIN".equals(role)) {
            return ApiResponse.success(feedbackService.getFeedbackByTarget(type, id));
        }
        return ApiResponse.success(feedbackService.getFeedbackByTargetAndUser(type, id, userId));
    }
}
