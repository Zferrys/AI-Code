package com.aicode.service;

import com.aicode.dto.UserProgressVO;
import com.aicode.entity.UserLearningProgress;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.LearningPathCourseMapper;
import com.aicode.mapper.UserLearningProgressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户学习进度服务
 */
@Service
public class UserLearningProgressService {

    @Autowired
    private UserLearningProgressMapper progressMapper;

    @Autowired
    private LearningPathCourseMapper courseMapper;

    /**
     * 开始学习课程
     */
    @Transactional
    public void startCourse(Long userId, Long courseId, Long pathId) {
        // 检查课程是否存在
        if (courseMapper.selectById(courseId) == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 检查是否已经开始
        UserLearningProgress exist = progressMapper.selectByUserAndCourse(userId, courseId);
        if (exist != null) {
            return; // 已存在，不做操作
        }

        Date now = new Date();
        UserLearningProgress progress = new UserLearningProgress();
        progress.setUserId(userId);
        progress.setCourseId(courseId);
        progress.setPathId(pathId);
        progress.setStatus("IN_PROGRESS");
        progress.setCreateTime(now);
        progress.setUpdateTime(now);
        progressMapper.insert(progress);
    }

    /**
     * 完成课程
     */
    @Transactional
    public void completeCourse(Long userId, Long courseId, Long pathId) {
        UserLearningProgress progress = progressMapper.selectByUserAndCourse(userId, courseId);
        if (progress == null) {
            // 没有开始记录，直接创建已完成记录
            Date now = new Date();
            progress = new UserLearningProgress();
            progress.setUserId(userId);
            progress.setCourseId(courseId);
            progress.setPathId(pathId);
            progress.setStatus("COMPLETED");
            progress.setCompletedTime(now);
            progress.setCreateTime(now);
            progress.setUpdateTime(now);
            progressMapper.insert(progress);
        } else {
            progress.setStatus("COMPLETED");
            progress.setCompletedTime(new Date());
            progress.setUpdateTime(new Date());
            progressMapper.updateProgress(progress);
        }
    }

    /**
     * 获取用户在某路径下的学习进度
     */
    public UserProgressVO getPathProgress(Long userId, Long pathId) {
        int total = courseMapper.countByPathId(pathId);
        int completed = progressMapper.countCompletedByUserAndPath(userId, pathId);
        return new UserProgressVO(pathId, total, completed);
    }

    /**
     * 取消完成（重置为未开始）
     */
    @Transactional
    public void resetCourse(Long userId, Long courseId) {
        UserLearningProgress progress = progressMapper.selectByUserAndCourse(userId, courseId);
        if (progress != null) {
            progressMapper.deleteById(progress.getId());
        }
    }

    /**
     * 获取用户所有进度
     */
    public List<UserLearningProgress> getAllProgress(Long userId) {
        return progressMapper.selectByUserId(userId);
    }
}
