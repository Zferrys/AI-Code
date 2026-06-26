package com.aicode.controller;

import com.aicode.dto.*;
import com.aicode.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<?> register(HttpServletRequest httpRequest,
                                    @RequestBody RegisterRequest request) {
        userService.register(request, httpRequest);
        return ApiResponse.success("注册成功", null);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<UserService.LoginResult> login(HttpServletRequest httpRequest,
                                                       @RequestBody LoginRequest request) {
        UserService.LoginResult result = userService.login(request, httpRequest);
        return ApiResponse.success(result);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(HttpServletRequest request,
                                             @RequestParam("file") MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        String avatarPath = userService.uploadAvatar(userId, file);
        return ApiResponse.success(avatarPath);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ApiResponse<UserProfileVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserProfileVO profile = userService.getUserProfile(userId);
        return ApiResponse.success(profile);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public ApiResponse<?> updateUserInfo(HttpServletRequest request,
                                          @RequestBody UserUpdateRequest updateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateUserInfo(userId, updateRequest);
        return ApiResponse.success("保存成功", null);
    }
}
