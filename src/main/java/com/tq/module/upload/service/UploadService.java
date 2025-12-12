package com.tq.module.upload.service;

import com.tq.module.upload.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface UploadService {

    /**
     * 上传图片文件
     *
     * @param file    上传的文件
     * @param baseUrl 当前请求的基础 URL，例如 http://localhost:8080
     * @return 上传结果
     */
    UploadResponse uploadImage(MultipartFile file, String baseUrl);
}
