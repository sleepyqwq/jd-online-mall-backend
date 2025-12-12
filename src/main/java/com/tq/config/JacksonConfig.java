package com.tq.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * 全局 Jackson 配置:
 * 1. 统一将 Long / long 序列化为字符串，避免前端精度丢失
 * 2. 其他 Jackson 配置继续使用 application.yml 中 spring.jackson 的配置
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> {
            SimpleModule longToStringModule = new SimpleModule();
            // Long / long 统一转为字符串，使用单例实例，而不是 new 构造
            longToStringModule.addSerializer(Long.class, ToStringSerializer.instance);
            longToStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            // 在 JsonMapper.Builder 上注册自定义模块
            builder.addModules(longToStringModule);
        };
    }
}
