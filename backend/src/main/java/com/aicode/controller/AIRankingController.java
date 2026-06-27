package com.aicode.controller;

import com.aicode.dto.AIRankingEntry;
import com.aicode.dto.ApiResponse;
import com.aicode.service.AIRankingService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * AI 模型排名控制器
 */
@RestController
@RequestMapping("/api/rankings")
public class AIRankingController {

    private final AIRankingService aiRankingService;

    public AIRankingController(AIRankingService aiRankingService) {
        this.aiRankingService = aiRankingService;
    }

    /**
     * 获取 AI 模型排名
     */
    @GetMapping
    public ApiResponse<List<AIRankingEntry>> getRankings() {
        List<AIRankingEntry> rankings = aiRankingService.getRankings();
        return ApiResponse.success(rankings != null ? rankings : Collections.emptyList());
    }

    /**
     * 手动刷新排名（管理员用）
     */
    @PostMapping("/refresh")
    public ApiResponse<List<AIRankingEntry>> refreshRankings(HttpServletRequest request) {
        // 权限校验：仅 ADMIN 角色可刷新
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error(403, "需要管理员权限");
        }
        List<AIRankingEntry> rankings = aiRankingService.refreshRankings();
        return ApiResponse.success(rankings != null ? rankings : Collections.emptyList());
    }
}
