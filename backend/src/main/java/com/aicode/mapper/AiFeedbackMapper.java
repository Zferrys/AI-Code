package com.aicode.mapper;

import com.aicode.entity.AiFeedback;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiFeedbackMapper {
    int insert(AiFeedback feedback);
    List<AiFeedback> selectByTarget(@Param("feedbackType") String type, @Param("targetId") Long targetId);
    List<AiFeedback> selectByTargetAndUser(@Param("feedbackType") String type, @Param("targetId") Long targetId, @Param("userId") Long userId);
    List<AiFeedback> selectByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    long countByUserId(@Param("userId") Long userId);
    Double avgRating();
}
