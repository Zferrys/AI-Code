package com.aicode.controller;

import com.aicode.dto.ApiResponse;
import com.aicode.dto.TagVO;
import com.aicode.dto.TechTagRequest;
import com.aicode.service.UserTechTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户技术标签控制器
 */
@RestController
@RequestMapping("/api/user/tags")
public class UserTechTagController {

    @Autowired
    private UserTechTagService userTechTagService;

    /**
     * 获取用户的技术标签
     */
    @GetMapping
    public ApiResponse<List<TagVO>> getUserTechTags(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ApiResponse.success(userTechTagService.getUserTechTags(userId));
    }

    /**
     * 添加技术标签
     */
    @PostMapping
    public ApiResponse<?> addTechTag(HttpServletRequest request,
                                      @RequestBody TechTagRequest techTagRequest) {
        Long userId = (Long) request.getAttribute("userId");
        userTechTagService.addTechTag(userId, techTagRequest.getTagId());
        return ApiResponse.success("添加成功", null);
    }

    /**
     * 删除技术标签
     */
    @DeleteMapping("/{tagId}")
    public ApiResponse<?> removeTechTag(HttpServletRequest request,
                                         @PathVariable Long tagId) {
        Long userId = (Long) request.getAttribute("userId");
        userTechTagService.removeTechTag(userId, tagId);
        return ApiResponse.success("删除成功", null);
    }
}
