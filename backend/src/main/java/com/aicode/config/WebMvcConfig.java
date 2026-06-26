package com.aicode.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置
 * 静态资源映射已移至 spring-mvc.xml 中通过 <mvc:resources> 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

}
