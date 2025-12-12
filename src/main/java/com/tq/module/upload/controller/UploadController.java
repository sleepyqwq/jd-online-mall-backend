package com.tq.module.upload.controller;

import com.tq.common.api.Result;
import com.tq.module.upload.dto.UploadResponse;
import com.tq.module.upload.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件上传接口
 * 对应接口文档中的 /api/upload
 */
@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     * 上传图片文件
     */
    @PostMapping("/upload")
    public Result<UploadResponse> upload(@RequestParam("file") MultipartFile file,
                                         HttpServletRequest request) {
        String baseUrl = buildBaseUrl(request);
        UploadResponse resp = uploadService.uploadImage(file, baseUrl);
        return Result.ok(resp);
    }

    /**
     * 计算当前请求的基础 URL，例如 http://localhost:8080
     */
    private String buildBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();

        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://").append(host);
        if (!("http".equalsIgnoreCase(scheme) && port == 80)
                && !("https".equalsIgnoreCase(scheme) && port == 443)) {
            sb.append(":").append(port);
        }
        return sb.toString();
    }
}
