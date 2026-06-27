package com.aicode.mapper;

import com.aicode.entity.LearningPathCourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习路径课程 Mapper
 */
public interface LearningPathCourseMapper {

    LearningPathCourse selectById(@Param("id") Long id);

    List<LearningPathCourse> selectByPathId(@Param("pathId") Long pathId);

    int countByPathId(@Param("pathId") Long pathId);

    int insert(LearningPathCourse course);

    int batchInsert(List<LearningPathCourse> courses);

    int updateById(LearningPathCourse course);

    int deleteById(@Param("id") Long id);

    int deleteByPathId(@Param("pathId") Long pathId);
}
