package com.aicode.mapper;

import com.aicode.entity.UserLearningProgress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户学习进度 Mapper
 */
public interface UserLearningProgressMapper {

    UserLearningProgress selectByUserAndCourse(@Param("userId") Long userId,
                                               @Param("courseId") Long courseId);

    List<UserLearningProgress> selectByUserId(@Param("userId") Long userId);

    List<UserLearningProgress> selectByUserAndPath(@Param("userId") Long userId,
                                                   @Param("pathId") Long pathId);

    int countCompletedByUserAndPath(@Param("userId") Long userId, @Param("pathId") Long pathId);

    List<UserLearningProgress> selectByUserAndCourses(@Param("userId") Long userId,
                                                       @Param("courseIds") List<Long> courseIds);

    int insert(UserLearningProgress progress);

    int updateProgress(UserLearningProgress progress);

    int deleteById(@Param("id") Long id);
}
