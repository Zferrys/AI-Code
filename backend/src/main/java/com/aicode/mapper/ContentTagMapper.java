package com.aicode.mapper;

import com.aicode.entity.ContentTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容标签 Mapper（多态关联）
 */
public interface ContentTagMapper {

    List<ContentTag> selectByTarget(@Param("contentType") String contentType,
                                    @Param("contentId") Long contentId);

    List<Long> selectTagIdsByTarget(@Param("contentType") String contentType,
                                    @Param("contentId") Long contentId);

    int insert(ContentTag contentTag);

    int deleteByTarget(@Param("contentType") String contentType,
                       @Param("contentId") Long contentId);

    int deleteById(@Param("id") Long id);
}
