package com.aicode.mapper;

import com.aicode.entity.SysConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置 Mapper
 */
public interface SysConfigMapper {

    SysConfig selectByKey(@Param("configKey") String configKey);

    String selectValueByKey(@Param("configKey") String configKey);

    List<SysConfig> selectAll();

    int insert(SysConfig sysConfig);

    int updateByKey(SysConfig sysConfig);
}
