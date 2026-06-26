package com.aicode.service;

import com.aicode.entity.AiFeedback;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.AiFeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private AiFeedbackMapper feedbackMapper;

    public void submitFeedback(Long userId, String type, Long targetId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) throw new BusinessException(400, "评分范围1-5");
        AiFeedback f = new AiFeedback();
        f.setUserId(userId); f.setFeedbackType(type); f.setTargetId(targetId);
        f.setRating(rating); f.setComment(comment); f.setCreateTime(new Date());
        feedbackMapper.insert(f);
    }

    public List<AiFeedback> getFeedbackByTarget(String type, Long targetId) {
        return feedbackMapper.selectByTarget(type, targetId);
    }

    public List<AiFeedback> getFeedbackByTargetAndUser(String type, Long targetId, Long userId) {
        return feedbackMapper.selectByTargetAndUser(type, targetId, userId);
    }

    public List<AiFeedback> getUserFeedback(Long userId, int page, int pageSize) {
        return feedbackMapper.selectByUserId(userId, (page-1)*pageSize, pageSize);
    }

    public long countUserFeedback(Long userId) { return feedbackMapper.countByUserId(userId); }
}
