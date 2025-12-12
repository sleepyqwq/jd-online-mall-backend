package com.tq.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jd.upload")
public class UploadProperties {

    /**
     * 本地保存目录
     * 例如 ./data/upload
     */
    private String localDir = "./data/upload";

    /**
     * 对外静态访问前缀
     * 例如 /images
     */
    private String publicPrefix = "/images";

    /**
     * 是否在需要时返回完整 URL
     */
    private boolean returnFullUrl = true;
}
