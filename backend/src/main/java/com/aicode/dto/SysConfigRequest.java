package com.aicode.dto;

/**
 * 系统配置请求 DTO
 */
public class SysConfigRequest {

    private String configKey;
    private String configValue;

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
}
