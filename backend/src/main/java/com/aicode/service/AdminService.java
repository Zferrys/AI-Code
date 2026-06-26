package com.aicode.service;

import com.aicode.dto.DashboardStatsVO;
import com.aicode.entity.SysConfig;
import com.aicode.entity.User;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    @Autowired private UserMapper userMapper;
    @Autowired private CodeReviewMapper codeReviewMapper;
    @Autowired private QaQuestionMapper qaQuestionMapper;
    @Autowired private LearningPathMapper learningPathMapper;
    @Autowired private SysConfigMapper sysConfigMapper;
    @Autowired private AiFeedbackMapper feedbackMapper;
    @Autowired private SysLogMapper sysLogMapper;

    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setUserCount(userMapper.countAll());
        vo.setReviewCount(codeReviewMapper.countAll());
        vo.setQuestionCount(qaQuestionMapper.countAll());
        vo.setPathCount(learningPathMapper.countAll());
        Double avg = feedbackMapper.avgRating();
        vo.setAvgRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0);
        return vo;
    }

    public List<User> listUsers(int page, int pageSize) {
        return userMapper.selectAllPaged((page-1)*pageSize, pageSize);
    }

    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if ("ADMIN".equals(user.getRole())) throw new BusinessException(400, "不能禁用管理员");
        userMapper.updateStatus(userId, status);
    }

    private static final Set<String> ALLOWED_CONFIG_KEYS = new HashSet<>(Arrays.asList(
            "ai.api.endpoint", "ai.api.key", "ai.api.model", "ai.api.timeout",
            "site.title", "site.description"));

    public SysConfig getConfig(String key) { return sysConfigMapper.selectByKey(key); }
    public List<SysConfig> getAllConfig() { return sysConfigMapper.selectAll(); }

    @Transactional
    public void updateConfig(SysConfig config) {
        String configKey = config.getConfigKey();
        if (!ALLOWED_CONFIG_KEYS.contains(configKey)) {
            throw new BusinessException(400, "不允许修改配置项: " + configKey);
        }
        SysConfig exist = sysConfigMapper.selectByKey(configKey);
        if (exist == null) {
            config.setCreateTime(new Date());
            config.setUpdateTime(new Date());
            sysConfigMapper.insert(config);
        } else {
            sysConfigMapper.updateByKey(config);
        }
    }

    public List<com.aicode.entity.SysLog> listLogs(int page, int pageSize) {
        return sysLogMapper.selectAllPaged((page-1)*pageSize, pageSize);
    }
    public long countLogs() { return sysLogMapper.countAll(); }
}
