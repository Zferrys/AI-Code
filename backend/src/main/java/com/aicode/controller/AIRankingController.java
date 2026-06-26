package com.aicode.controller;

import com.aicode.dto.AIRankingEntry;
import com.aicode.dto.ApiResponse;
import com.aicode.service.AIRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 模型排名控制器
 */
@RestController
@RequestMapping("/api/rankings")
public class AIRankingController {

    @Autowired
    private AIRankingService aiRankingService;

    /**
     * 获取 AI 模型排名
     */
    @GetMapping
    public ApiResponse<List<AIRankingEntry>> getRankings() {
        return ApiResponse.success(aiRankingService.getRankings());
    }

    /**
     * 手动刷新排名（管理员用）
     */
    @PostMapping("/refresh")
    public ApiResponse<List<AIRankingEntry>> refreshRankings() {
        return ApiResponse.success(aiRankingService.refreshRankings());
    }
}
