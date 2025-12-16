package com.tq.config;

import com.tq.config.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@NullMarked
@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (!corsProperties.isEnabled()) {
            return;
        }

        var registration = registry.addMapping("/**");

        List<String> origins = corsProperties.getAllowedOrigins();
        if (origins == null || origins.isEmpty()) {
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
