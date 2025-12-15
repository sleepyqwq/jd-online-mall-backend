package com.tq.config;

import com.tq.config.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * CORS 跨域配置
 * 说明：
 * 1 默认关闭（jd.cors.enabled=false）
 * 2 当需要本地直连调试时，只需在 application.yml 中将 jd.cors.enabled 调为 true，
 *   并根据需要配置允许的来源、方法和请求头，不必改动代码
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 未开启时不做任何 CORS 配置，保持与当前行为一致
        if (!corsProperties.isEnabled()) {
            return;
        }

        // 对所有接口生效
        var registration = registry.addMapping("/**");

        List<String> origins = corsProperties.getAllowedOrigins();
        if (origins == null || origins.isEmpty()) {
            // 未显式配置时给出一个便于调试的默认配置
            registration.allowedOriginPatterns("*");
        } else {
            registration.allowedOrigins(origins.toArray(new String[0]));
        }

        List<String> methods = corsProperties.getAllowedMethods();
        if (methods == null || methods.isEmpty()) {
            registration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        } else {
            registration.allowedMethods(methods.toArray(new String[0]));
        }

        List<String> headers = corsProperties.getAllowedHeaders();
        if (headers == null || headers.isEmpty()) {
            registration.allowedHeaders("Authorization", "Content-Type");
        } else {
            registration.allowedHeaders(headers.toArray(new String[0]));
        }

        registration.allowCredentials(corsProperties.isAllowCredentials());
    }
}
