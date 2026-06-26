package com.aicode.mapper;

import com.aicode.entity.Favorite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FavoriteMapper {
    int insert(Favorite favorite);
    int deleteByUserAndTarget(@Param("userId") Long userId, @Param("contentType") String contentType, @Param("contentId") Long contentId);
    Favorite selectByUserAndTarget(@Param("userId") Long userId, @Param("contentType") String contentType, @Param("contentId") Long contentId);
    List<Favorite> selectByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    long countByUserId(@Param("userId") Long userId);
    long countByTarget(@Param("contentType") String contentType, @Param("contentId") Long contentId);
}
