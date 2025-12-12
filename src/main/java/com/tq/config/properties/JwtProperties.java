package com.tq.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jd.jwt")
public class JwtProperties {

    /**
     * 建议使用至少 32 位长度的安全密钥
     */
    private String secret;

    /**
     * 过期时间，单位秒
     */
    private long expireSeconds = 7200;
}
