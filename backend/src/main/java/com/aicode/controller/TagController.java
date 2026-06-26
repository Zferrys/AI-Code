package com.aicode.controller;

import com.aicode.dto.ApiResponse;
import com.aicode.dto.TagVO;
import com.aicode.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器（公开接口）
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     */
    @GetMapping
    public ApiResponse<List<TagVO>> getAllTags() {
        return ApiResponse.success(tagService.getAllTags());
    }

    /**
     * 按分类获取标签
     */
    @GetMapping("/category/{category}")
    public ApiResponse<List<TagVO>> getTagsByCategory(@PathVariable String category) {
        return ApiResponse.success(tagService.getTagsByCategory(category));
    }
}
