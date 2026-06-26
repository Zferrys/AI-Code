package com.aicode.mapper;

import com.aicode.entity.SysLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper {
    int insert(SysLog sysLog);
    List<SysLog> selectAllPaged(@Param("offset") Integer offset, @Param("limit") Integer limit);
    long countAll();
}
