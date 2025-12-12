package com.tq.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jd.auth.token")
public class AuthTokenProperties {

    /**
     * 认证头名称
     * 例如 Authorization
     */
    private String header = "Authorization";

    /**
     * Token 前缀
     * 例如 Bearer
     */
    private String prefix = "Bearer ";
}
