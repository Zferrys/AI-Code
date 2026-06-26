package com.aicode.mapper;

import com.aicode.entity.CourseResource;

import java.util.List;

/**
 * 课程资源 Mapper
 */
public interface CourseResourceMapper {

    List<CourseResource> selectByCourseId(Long courseId);

    int insert(CourseResource resource);

    int deleteById(Long id);

    int deleteByCourseId(Long courseId);
}
