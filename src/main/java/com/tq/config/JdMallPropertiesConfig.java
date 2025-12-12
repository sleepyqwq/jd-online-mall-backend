package com.tq.config;

import com.tq.config.properties.AuthTokenProperties;
import com.tq.config.properties.CorsProperties;
import com.tq.config.properties.JwtProperties;
import com.tq.config.properties.OrderTimeoutProperties;
import com.tq.config.properties.UploadProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AuthTokenProperties.class,
        JwtProperties.class,
        UploadProperties.class,
        OrderTimeoutProperties.class,
        CorsProperties.class
})
public class JdMallPropertiesConfig {
}
