package com.aicode.controller;

import com.aicode.dto.*;
import com.aicode.service.CodeReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 代码审查控制器
 */
@RestController
@RequestMapping("/api/code-review")
public class CodeReviewController {

    @Autowired
    private CodeReviewService codeReviewService;

    /**
     * 提交代码审查
     */
    @PostMapping("/submit")
    public ApiResponse<CodeReviewVO> submit(HttpServletRequest request,
                                             @RequestBody CodeReviewRequest reviewRequest) {
        Long userId = (Long) request.getAttribute("userId");
        CodeReviewVO vo = codeReviewService.submitCodeReview(userId, reviewRequest);
        return ApiResponse.success(vo);
    }

    /**
     * 获取审查状态（轮询用）
     */
    @GetMapping("/{id}/status")
    public ApiResponse<CodeReviewStatusVO> getStatus(HttpServletRequest request,
                                                      @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        CodeReviewStatusVO status = codeReviewService.getReviewStatus(id, userId);
        return ApiResponse.success(status);
    }

    /**
     * 获取审查详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CodeReviewVO> getDetail(HttpServletRequest request,
                                                @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        CodeReviewVO vo = codeReviewService.getReviewDetail(id, userId);
        return ApiResponse.success(vo);
    }

    /**
     * 获取用户审查列表
     */
    @GetMapping("/list")
    public ApiResponse<?> list(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        List<CodeReviewVO> list = codeReviewService.getUserReviews(userId, page, pageSize);
        long total = codeReviewService.countUserReviews(userId);
        return ApiResponse.success(ApiResponse.page(list, total, page, pageSize));
    }
}
