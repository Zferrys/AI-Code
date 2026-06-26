package com.aicode.controller;

import com.aicode.dto.ApiResponse;
import com.aicode.dto.FavoriteRequest;
import com.aicode.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired private FavoriteService favoriteService;

    @PostMapping
    public ApiResponse<?> add(HttpServletRequest request, @RequestBody FavoriteRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.addFavorite(userId, req.getContentType(), req.getContentId());
        return ApiResponse.success("收藏成功", null);
    }

    @DeleteMapping
    public ApiResponse<?> remove(HttpServletRequest request, @RequestBody FavoriteRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.removeFavorite(userId, req.getContentType(), req.getContentId());
        return ApiResponse.success("取消收藏", null);
    }

    @GetMapping("/list")
    public ApiResponse<?> list(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        java.util.List<com.aicode.entity.Favorite> list = favoriteService.getUserFavorites(userId, page, pageSize);
        long total = favoriteService.countUserFavorites(userId);
        return ApiResponse.success(ApiResponse.page(list, total, page, pageSize));
    }

    @GetMapping("/check")
    public ApiResponse<Map<String, Object>> check(HttpServletRequest request,
                                                   @RequestParam String contentType,
                                                   @RequestParam Long contentId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean favorited = favoriteService.checkFavorite(userId, contentType, contentId);
        Map<String, Object> map = new HashMap<>();
        map.put("favorited", favorited);
        return ApiResponse.success(map);
    }
}
