package com.aicode.mapper;

import com.aicode.entity.Tag;
import com.aicode.entity.UserTechTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户技术标签 Mapper
 */
public interface UserTechTagMapper {

    UserTechTag selectByUserAndTag(@Param("userId") Long userId, @Param("tagId") Long tagId);

    List<Tag> selectTagsByUserId(@Param("userId") Long userId);

    List<Long> selectTagIdsByUserId(@Param("userId") Long userId);

    int insert(UserTechTag userTechTag);

    int deleteByUserAndTag(@Param("userId") Long userId, @Param("tagId") Long tagId);

    int deleteByUserId(@Param("userId") Long userId);

    int countByUserId(@Param("userId") Long userId);
}
