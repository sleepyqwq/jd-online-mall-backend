package com.tq.module.upload.service.impl;

import com.tq.common.api.ErrorCode;
import com.tq.common.exception.BusinessException;
import com.tq.common.util.UploadPathUtil;
import com.tq.config.properties.UploadProperties;
import com.tq.module.upload.dto.UploadResponse;
import com.tq.module.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 文件上传服务实现
 */
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final UploadProperties uploadProperties;

    @Override
    public UploadResponse uploadImage(MultipartFile file, String baseUrl) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            originalFilename = "unknown";
        }

        // 提取扩展名
        String ext = StringUtils.getFilenameExtension(originalFilename);
        if (!StringUtils.hasText(ext)) {
            ext = "jpg";
        }
        ext = ext.toLowerCase();

        // 生成日期目录：年 / 月 / 日
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());

        // 本地保存目录：localDir/year/month/day
        String baseDir = System.getProperty("user.dir");
        Path dir = Paths.get(baseDir, "data", "upload", year, month, day);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建上传目录失败");
        }

        // 生成不重复文件名
        String newName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path target = dir.resolve(newName);
        try {
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    "文件保存失败: " + e.getClass().getSimpleName() + " - " + e.getMessage()
            );
        }

        // 计算相对访问路径，例如 /images/2025/12/01/xxx.jpg
        String publicPrefix = UploadPathUtil.normalizePublicPrefix(uploadProperties.getPublicPrefix());
        String url = publicPrefix + "/" + year + "/" + month + "/" + day + "/" + newName;

        // 计算完整访问地址
        String fullUrl = url;
        if (uploadProperties.isReturnFullUrl() && StringUtils.hasText(baseUrl)) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                fullUrl = url;
            } else {
                fullUrl = baseUrl + url;
            }
        }

        UploadResponse resp = new UploadResponse();
        resp.setUrl(url);
        resp.setFullUrl(fullUrl);
        resp.setOriginalFilename(originalFilename);
        return resp;
    }

}
