package com.tq.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.tq.**.mapper")
public class MybatisPlusConfig {
}