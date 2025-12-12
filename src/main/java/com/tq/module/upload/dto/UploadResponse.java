package com.tq.module.upload.dto;

import lombok.Data;

/**
 * 上传文件响应体
 * 对应接口文档中 data 字段的结构
 */
@Data
public class UploadResponse {

    /**
     * 图片相对访问路径，例如 /images/2025/12/01/abc.jpg
     */
    private String url;

    /**
     * 图片完整访问地址，例如 http://localhost:8080/images/2025/12/01/abc.jpg
     */
    private String fullUrl;

    /**
     * 原始文件名，例如 abc.jpg
     */
    private String originalFilename;
}
