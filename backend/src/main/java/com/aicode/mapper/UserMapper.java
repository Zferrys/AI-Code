package com.aicode.mapper;

import com.aicode.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户 Mapper
 */
public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    List<User> selectAllPaged(@Param("offset") Integer offset, @Param("limit") Integer limit);

    long countAll();

    int insert(User user);

    int updateById(User user);

    int updateLastLogin(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updatePassword(@Param("id") Long id, @Param("password") String password,
                       @Param("updateTime") Date updateTime);

    int deleteById(@Param("id") Long id);
}
