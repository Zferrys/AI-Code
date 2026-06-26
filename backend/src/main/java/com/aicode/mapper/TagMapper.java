package com.aicode.mapper;

import com.aicode.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签 Mapper
 */
public interface TagMapper {

    Tag selectById(@Param("id") Long id);

    Tag selectByName(@Param("name") String name);

    List<Tag> selectAll();

    List<Tag> selectByCategory(@Param("category") String category);

    int insert(Tag tag);

    int updateById(Tag tag);

    int deleteById(@Param("id") Long id);
}
