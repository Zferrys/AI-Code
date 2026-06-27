package com.aicode.controller;

import com.aicode.dto.ApiResponse;
import com.aicode.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开统计数据（首页用，无需登录）
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ApiResponse<?> stats() {
        return ApiResponse.success(adminService.getDashboardStats());
    }
}
