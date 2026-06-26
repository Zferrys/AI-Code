package com.aicode.mapper;

import com.aicode.entity.UserProfile;
import org.apache.ibatis.annotations.Param;

/**
 * 用户扩展信息 Mapper
 */
public interface UserProfileMapper {

    UserProfile selectByUserId(@Param("userId") Long userId);

    int insert(UserProfile profile);

    int updateByUserId(UserProfile profile);
}
