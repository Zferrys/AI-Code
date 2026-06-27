package com.aicode.controller;

import com.aicode.dto.*;
import com.aicode.entity.SysConfig;
import com.aicode.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminController.class);

    @Autowired private AdminService adminService;
    @Autowired private UserService userService;
    @Autowired private CodeReviewService codeReviewService;
    @Autowired private QaService qaService;
    @Autowired private LearningPathService learningPathService;
    @Autowired private EmailService emailService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsVO> stats() {
        return ApiResponse.success(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ApiResponse<?> users(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        List<com.aicode.dto.UserVO> list = userService.listUsers(page, pageSize);
        long total = userService.countUsers();
        return ApiResponse.success(ApiResponse.page(list, total, page, pageSize));
    }

    @PutMapping("/user/{id}/status")
    public ApiResponse<?> updateUserStatus(@PathVariable Long id,
                                            @RequestBody StatusUpdateRequest request) {
        Integer status = request.getStatus();
        if (status == null || (status != 0 && status != 1)) {
            return ApiResponse.error(400, "状态值无效，仅支持 0（禁用）或 1（启用）");
        }
        adminService.updateUserStatus(id, status);
        return ApiResponse.success("更新成功", null);
    }

    @GetMapping("/reviews")
    public ApiResponse<?> reviews(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(ApiResponse.page(
                codeReviewService.listAllReviews(page, pageSize),
                codeReviewService.countAllReviews(), page, pageSize));
    }

    @GetMapping("/questions")
    public ApiResponse<?> questions(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        List<QaQuestionVO> list = qaService.listAllQuestions(page, pageSize);
        long total = qaService.countAllQuestions();
        return ApiResponse.success(ApiResponse.page(list, total, page, pageSize));
    }

    @GetMapping("/paths")
    public ApiResponse<?> paths(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(ApiResponse.page(
                learningPathService.listAllPaths(page, pageSize),
                learningPathService.countAllPaths(), page, pageSize));
    }

    @PostMapping("/paths")
    public ApiResponse<LearningPathVO> createPath(@RequestBody LearningPathVO vo) {
        return ApiResponse.success(learningPathService.createPath(vo));
    }

    @PutMapping("/paths")
    public ApiResponse<?> updatePath(@RequestBody LearningPathVO vo) {
        learningPathService.updatePath(vo);
        return ApiResponse.success("更新成功", null);
    }

    @DeleteMapping("/paths/{id}")
    public ApiResponse<?> deletePath(@PathVariable Long id) {
        learningPathService.deletePath(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * AI 智能生成学习路径
     */
    @PostMapping("/paths/generate")
    public ApiResponse<LearningPathVO> generatePath(@RequestBody java.util.Map<String, String> body) {
        String requirement = body.getOrDefault("requirement", "Java 后端开发学习路线");
        LearningPathVO vo = learningPathService.generatePathByAI(requirement);
        return ApiResponse.success(vo);
    }

    // ========== 路径课程管理 ==========

    @GetMapping("/paths/{pathId}/courses")
    public ApiResponse<List<LearningPathCourseVO>> pathCourses(@PathVariable Long pathId) {
        return ApiResponse.success(learningPathService.getCoursesByPathId(pathId));
    }

    @PostMapping("/paths/{pathId}/courses")
    public ApiResponse<LearningPathCourseVO> createCourse(
            @PathVariable Long pathId, @RequestBody LearningPathCourseVO vo) {
        vo.setPathId(pathId);
        return ApiResponse.success(learningPathService.addCourse(vo));
    }

    @PutMapping("/paths/courses")
    public ApiResponse<LearningPathCourseVO> updateCourse(
            @RequestBody LearningPathCourseVO vo) {
        return ApiResponse.success(learningPathService.updateCourse(vo));
    }

    @DeleteMapping("/paths/courses/{id}")
    public ApiResponse<?> deleteCourse(@PathVariable Long id) {
        learningPathService.deleteCourse(id);
        return ApiResponse.success("删除成功", null);
    }

    // ========== 课程资源管理 ==========

    @GetMapping("/paths/courses/{courseId}/resources")
    public ApiResponse<List<CourseResourceVO>> courseResources(@PathVariable Long courseId) {
        return ApiResponse.success(learningPathService.getCourseResources(courseId));
    }

    @PostMapping("/paths/courses/{courseId}/resources")
    public ApiResponse<CourseResourceVO> addCourseResource(
            @PathVariable Long courseId, @RequestBody CourseResourceVO vo) {
        vo.setCourseId(courseId);
        return ApiResponse.success(learningPathService.addCourseResource(vo));
    }

    @DeleteMapping("/paths/courses/resources/{id}")
    public ApiResponse<?> deleteCourseResource(@PathVariable Long id) {
        learningPathService.deleteCourseResource(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/config")
    public ApiResponse<List<SysConfig>> config() {
        return ApiResponse.success(adminService.getAllConfig());
    }

    @PutMapping("/config")
    public ApiResponse<?> updateConfig(@RequestBody SysConfig config) {
        if (config.getConfigKey() == null || config.getConfigKey().trim().isEmpty()) {
            return ApiResponse.error(400, "配置键不能为空");
        }
        if (config.getConfigValue() == null || config.getConfigValue().trim().isEmpty()) {
            return ApiResponse.error(400, "配置值不能为空");
        }
        adminService.updateConfig(config);
        return ApiResponse.success("更新成功", null);
    }

    @GetMapping("/logs")
    public ApiResponse<?> logs(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(ApiResponse.page(
                adminService.listLogs(page, pageSize),
                adminService.countLogs(), page, pageSize));
    }

    @PostMapping("/notify")
    public ApiResponse<?> sendNotification(HttpServletRequest request,
                                            @RequestBody NotificationRequest req) {
        // 权限校验
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error(403, "需要管理员权限");
        }
        if (req.getSubject() == null || req.getSubject().trim().isEmpty()) {
            return ApiResponse.error(400, "请输入通知标题");
        }
        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            return ApiResponse.error(400, "请输入通知内容");
        }
        try {
            if ("all".equals(req.getType())) {
                List<String> emails = userService.getAllUserEmails();
                if (emails.isEmpty()) {
                    return ApiResponse.error(400, "没有可发送的用户邮箱");
                }
                // 异步批量发送，避免阻塞 HTTP 线程
                List<String> copy = new java.util.ArrayList<>(emails);
                CompletableFuture.runAsync(() -> {
                    int sent = 0;
                    for (String email : copy) {
                        try {
                            emailService.send(email, req.getSubject(), req.getContent());
                            sent++;
                        } catch (Exception e) {
                            log.warn("通知发送失败: email={}, {}", email, e.getMessage());
                        }
                    }
                    log.info("批量通知完成: 成功{}/{}封", sent, copy.size());
                });
                return ApiResponse.success("通知发送任务已提交，共 " + emails.size() + " 封");
            } else {
                if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
                    return ApiResponse.error(400, "请输入接收邮箱");
                }
                emailService.send(req.getEmail().trim(), req.getSubject(), req.getContent());
                return ApiResponse.success("通知已发送");
            }
        } catch (Exception e) {
            log.error("邮件通知发送异常: type={}, email={}", req.getType(), req.getEmail(), e);
            return ApiResponse.error(500, "发送失败：" + e.getMessage());
        }
    }

    @GetMapping("/users/emails")
    public ApiResponse<?> getUserEmails(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error(403, "需要管理员权限");
        }
        return ApiResponse.success(userService.getAllUserEmails());
    }
}
