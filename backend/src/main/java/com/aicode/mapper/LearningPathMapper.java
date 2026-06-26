package com.aicode.mapper;

import com.aicode.entity.LearningPath;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习路径 Mapper
 */
public interface LearningPathMapper {

    LearningPath selectById(@Param("id") Long id);

    List<LearningPath> selectAllPublished();

    List<LearningPath> selectAllPublishedPaged(@Param("offset") Integer offset, @Param("limit") Integer limit);

    List<LearningPath> selectAllPaged(@Param("offset") Integer offset, @Param("limit") Integer limit);

    long countAll();

    long countAllPublished();

    int insert(LearningPath path);

    int updateById(LearningPath path);

    int updateCourseCount(@Param("id") Long id, @Param("count") Integer count);

    int deleteById(@Param("id") Long id);
}
