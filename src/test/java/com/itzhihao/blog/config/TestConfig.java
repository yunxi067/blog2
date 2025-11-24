package com.itzhihao.blog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.itzhihao.blog")
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
public class TestConfig {
    // 测试配置类
}