package com.aicode.service;

import com.aicode.dto.*;
import com.aicode.entity.User;
import com.aicode.entity.UserProfile;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.UserMapper;
import com.aicode.mapper.UserProfileMapper;
import com.aicode.util.JwtTokenUtil;
import com.aicode.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用户服务
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /** 登录限流：同一 IP 5 次失败后封锁 15 分钟 */
    private static final int LOGIN_MAX_ATTEMPTS = 5;
    private static final long LOGIN_BLOCK_DURATION = 15 * 60 * 1000; // 15 分钟
    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    /** 注册限流：同一 IP 3 次/小时 */
    private static final int REGISTER_MAX = 3;
    private static final long REGISTER_WINDOW = 60 * 60 * 1000L; // 1小时
    private final ConcurrentHashMap<String, LoginAttempt> registerAttempts = new ConcurrentHashMap<>();

    private static class LoginAttempt {
        int count;
        long firstFailTime;
        boolean isBlocked() {
            return count >= LOGIN_MAX_ATTEMPTS
                    && System.currentTimeMillis() - firstFailTime < LOGIN_BLOCK_DURATION;
        }
        /** 是否已过期（超时窗口的两倍时间后清理） */
        boolean isExpired(long window) {
            return System.currentTimeMillis() - firstFailTime > window * 2;
        }
        void fail() {
            if (count == 0) firstFailTime = System.currentTimeMillis();
            count++;
        }
        void reset() { count = 0; }
    }

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request, HttpServletRequest httpRequest) {
        // 注册限流
        String ip = com.aicode.util.WebUtil.getClientIp(httpRequest);
        if (ip != null) {
            LoginAttempt att = registerAttempts.get(ip);
            if (att != null && att.count >= REGISTER_MAX
                    && System.currentTimeMillis() - att.firstFailTime < REGISTER_WINDOW) {
                throw new BusinessException(429, "注册过于频繁，请 1 小时后再试");
            }
        }

        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();

        // 先校验参数（不消耗验证码），参数不通过直接抛错
        if (username == null || username.length() < 4 || username.length() > 20) {
            throw new BusinessException(400, "用户名长度需在4-20位之间");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new BusinessException(400, "用户名仅支持字母、数字和下划线");
        }
        if (password == null || password.length() < 12) {
            throw new BusinessException(400, "密码长度至少12位，需包含大小写字母、数字和特殊字符");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException(400, "密码必须包含小写字母");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException(400, "密码必须包含大写字母");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException(400, "密码必须包含数字");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-={}\\[\\]:;\",.<>/?~`].*")) {
            throw new BusinessException(400, "密码必须包含至少一个特殊字符");
        }
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            throw new BusinessException(400, "邮箱格式不正确");
        }

        // 检查用户名唯一性
        if (userMapper.selectByUsername(username) != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 检查邮箱唯一性
        if (userMapper.selectByEmail(email) != null) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        // 校验邮箱验证码
        String emailCode = request.getEmailCode();
        if (emailCode == null || emailCode.trim().isEmpty()) {
            throw new BusinessException(400, "请先获取邮箱验证码");
        }
        if (!emailVerificationService.verifyCode(email, emailCode.trim())) {
            throw new BusinessException(400, "邮箱验证码错误或已过期，请重新获取");
        }

        // 最后校验图形验证码（消耗验证码，前面的校验不通过则验证码不会被消耗）
        String regCid = request.getCaptchaId();
        Integer regAns = request.getCaptchaAnswer();
        if (regCid == null || regCid.isEmpty() || regAns == null) {
            throw new BusinessException(400, "请完成验证码");
        }
        if (!captchaService.validate(regCid, regAns)) {
            throw new BusinessException(400, "验证码错误或已过期，请重新获取");
        }

        // 创建用户
        Date now = new Date();
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.encode(password));
        user.setEmail(email);
        user.setRole("USER");
        user.setStatus(1);
        user.setAvatar("/default.png");
        user.setCreateTime(now);
        user.setUpdateTime(now);

        userMapper.insert(user);
        // 注册成功，更新限流计数
        if (ip != null) {
            registerAttempts.computeIfAbsent(ip, k -> new LoginAttempt()).fail();
        }

        // 创建用户扩展信息
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setExperienceLevel("BEGINNER");
        userProfileMapper.insert(profile);

        log.info("用户注册成功: username={}, userId={}", username, user.getId());
    }

    /**
     * 用户登录
     */
    @Transactional
    public LoginResult login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || password == null) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }

        // 登录限流检查（基于 IP）
        String ip = com.aicode.util.WebUtil.getClientIp(httpRequest);
        if (ip != null) {
            LoginAttempt attempt = loginAttempts.get(ip);
            if (attempt != null && attempt.isBlocked()) {
                long remainMin = (LOGIN_BLOCK_DURATION - (System.currentTimeMillis() - attempt.firstFailTime)) / 60000;
                throw new BusinessException(429, "登录尝试过于频繁，请 " + (remainMin + 1) + " 分钟后再试");
            }
        }
        // 清理过期限流记录（每 10 次登录触发一次）
        if (loginAttempts.size() > 100) {
            loginAttempts.entrySet().removeIf(e -> e.getValue().isExpired(LOGIN_BLOCK_DURATION));
        }
        if (registerAttempts.size() > 100) {
            registerAttempts.entrySet().removeIf(e -> e.getValue().isExpired(REGISTER_WINDOW));
        }

        // 验证码校验（仅在提供了验证码ID和答案时校验）
        String cid = request.getCaptchaId();
        Integer ans = request.getCaptchaAnswer();
        if (cid != null && !cid.isEmpty() && ans != null) {
            if (!captchaService.validate(cid, ans)) {
                throw new BusinessException(400, "验证码错误或已过期，请重新获取");
            }
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 校验密码
        if (!passwordUtil.matches(password, user.getPassword())) {
            // 记录失败次数
            if (ip != null) {
                loginAttempts.computeIfAbsent(ip, k -> new LoginAttempt()).fail();
            }
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 登录成功，清除失败记录
        if (ip != null) loginAttempts.remove(ip);

        // 更新最后登录时间
        userMapper.updateLastLogin(user.getId());

        // 生成 Token
        String token = jwtTokenUtil.generateToken(
                user.getId().intValue(), user.getUsername(), user.getRole());

        // 构建返回
        UserVO userVO = toUserVO(user);
        LoginResult result = new LoginResult();
        result.setToken(token);
        result.setUser(userVO);

        log.info("用户登录成功: username={}, userId={}", username, user.getId());
        return result;
    }

    /**
     * 获取用户信息（含扩展信息和技术标签）
     */
    public UserProfileVO getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        UserProfile profile = userProfileMapper.selectByUserId(userId);

        UserVO userVO = toUserVO(user);
        UserProfileVO vo = new UserProfileVO();
        vo.setUser(userVO);
        // 扁平化：把常用用户字段复制到顶层，前端直接访问
        vo.setId(userVO.getId());
        vo.setUsername(userVO.getUsername());
        vo.setEmail(userVO.getEmail());
        vo.setPhone(userVO.getPhone());
        vo.setAvatar(userVO.getAvatar());
        vo.setRole(userVO.getRole());
        vo.setStatus(userVO.getStatus());
        if (profile != null) {
            vo.setRealName(profile.getRealName());
            vo.setSchool(profile.getSchool());
            vo.setMajor(profile.getMajor());
            vo.setGrade(profile.getGrade());
            vo.setBio(profile.getBio());
            vo.setGithubUrl(profile.getGithubUrl());
            vo.setExperienceLevel(profile.getExperienceLevel());
        }
        return vo;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateRequest request) {
        // 更新 user 表
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (request.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            User exist = userMapper.selectByEmail(request.getEmail());
            if (exist != null && !exist.getId().equals(userId)) {
                throw new BusinessException(400, "邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }
        user.setPhone(request.getPhone());
        user.setUpdateTime(new Date());
        userMapper.updateById(user);

        // 更新 user_profile 表
        UserProfile profile = userProfileMapper.selectByUserId(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setRealName(request.getRealName());
            profile.setSchool(request.getSchool());
            profile.setMajor(request.getMajor());
            profile.setGrade(request.getGrade());
            profile.setBio(request.getBio());
            profile.setGithubUrl(request.getGithubUrl());
            userProfileMapper.insert(profile);
        } else {
            profile.setRealName(request.getRealName());
            profile.setSchool(request.getSchool());
            profile.setMajor(request.getMajor());
            profile.setGrade(request.getGrade());
            profile.setBio(request.getBio());
            profile.setGithubUrl(request.getGithubUrl());
            userProfileMapper.updateByUserId(profile);
        }
    }

    /**
     * 上传用户头像
     */
    public String uploadAvatar(Long userId, MultipartFile file) {
        // 校验文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(400, "头像图片大小不能超过 5MB");
        }

        // 内容验证：用 ImageIO 读取确认是有效图片，并自动判断格式
        String ext;
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                throw new BusinessException(400, "文件不是有效的图片格式");
            }
            // 根据图片内容自动确定扩展名，不使用用户传入的文件名
            ext = img.getColorModel().hasAlpha() ? ".png" : ".jpg";
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            throw new BusinessException(400, "文件读取失败，不是有效的图片");
        }

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + ext;
        String relativePath = "/uploads/avatars/" + newFilename;

        // 保存文件
        try {
            File dir = new File("uploads/avatars");
            if (!dir.exists() && !dir.mkdirs()) {
                throw new BusinessException(500, "文件目录创建失败");
            }
            File dest = new File(dir, newFilename);
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("头像上传失败: userId={}", userId, e);
            throw new BusinessException(500, "头像上传失败，请稍后重试");
        }

        // 更新数据库
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setAvatar(relativePath);
        user.setUpdateTime(new Date());
        userMapper.updateById(user);

        log.info("用户头像更新成功: userId={}, avatar={}", userId, relativePath);
        return relativePath;
    }

    /**
     * 管理员获取用户列表
     */
    public List<UserVO> listUsers(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<User> users = userMapper.selectAllPaged(offset, pageSize);
        return users.stream().map(this::toUserVO).collect(Collectors.toList());
    }

    public long countUsers() {
        return userMapper.countAll();
    }

    /** 获取所有用户邮箱列表（用于管理员通知） */
    public List<String> getAllUserEmails() {
        List<User> users = userMapper.selectAllPaged(0, Integer.MAX_VALUE);
        List<String> emails = new ArrayList<>();
        for (User u : users) {
            if (u.getEmail() != null && !u.getEmail().isEmpty()) {
                emails.add(u.getEmail());
            }
        }
        return emails;
    }

    /**
     * 管理员更新用户状态
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException(400, "不能禁用管理员账号");
        }
        userMapper.updateStatus(userId, status);
    }

    // ---- 内部方法 ----

    private UserVO toUserVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setLastLogin(user.getLastLogin());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    // ---- 内部类 ----

    public static class LoginResult {
        private String token;
        private UserVO user;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public UserVO getUser() { return user; }
        public void setUser(UserVO user) { this.user = user; }
    }
}
