package com.tq.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jd.order")
public class OrderTimeoutProperties {

    /**
     * 正式语义超时，单位秒
     * 默认 900 秒，即 15 分钟
     */
    private long timeoutSeconds = 900;

    /**
     * 测试用超时，单位秒
     */
    private long testTimeoutSeconds = 20;

    /**
     * 是否启用测试用超时
     */
    private boolean useTestTimeout = false;

    /**
     * 获取当前生效的超时时间
     * 仅用于配置层的便捷计算，不涉及业务逻辑
     */
    public long effectiveTimeoutSeconds() {
        return useTestTimeout ? testTimeoutSeconds : timeoutSeconds;
    }
}
