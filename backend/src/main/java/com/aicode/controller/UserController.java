package com.aicode.controller;

import com.aicode.dto.*;
import com.aicode.service.UserService;

import java.util.Map;
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

    /**
     * 修改邮箱（需原邮箱验证码）
     */
    @PutMapping("/email")
    public ApiResponse<?> changeEmail(HttpServletRequest request,
                                       @RequestBody Map<String, String> body) {
        Long userId = (Long) request.getAttribute("userId");
        String code = body.get("code");
        String newEmail = body.get("newEmail");
        if (code == null || code.trim().isEmpty()) {
            return ApiResponse.error(400, "请输入验证码");
        }
        userService.changeEmail(userId, code.trim(), newEmail);
        return ApiResponse.success("邮箱修改成功");
    }

    /**
     * 重置密码（忘记密码，邮箱验证码验证身份，无需登录）
     */
    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");
        if (email == null || code == null || code.trim().isEmpty()) {
            return ApiResponse.error(400, "参数不完整");
        }
        if (newPassword == null || newPassword.length() < 6) {
            return ApiResponse.error(400, "新密码长度不能少于6位");
        }
        userService.resetPassword(email.trim(), code.trim(), newPassword);
        return ApiResponse.success("密码重置成功");
    }

    /**
     * 修改密码（需邮箱验证码）
     */
    @PutMapping("/password")
    public ApiResponse<?> changePassword(HttpServletRequest request,
                                          @RequestBody Map<String, String> body) {
        Long userId = (Long) request.getAttribute("userId");
        String code = body.get("code");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (code == null || code.trim().isEmpty()) {
            return ApiResponse.error(400, "请输入验证码");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return ApiResponse.error(400, "请输入旧密码");
        }
        if (newPassword == null || newPassword.length() < 6) {
            return ApiResponse.error(400, "新密码长度不能少于6位");
        }
        userService.changePassword(userId, code.trim(), oldPassword, newPassword);
        return ApiResponse.success("密码修改成功");
    }
}
