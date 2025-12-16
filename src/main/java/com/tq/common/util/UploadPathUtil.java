package com.tq.common.util;

import org.springframework.util.StringUtils;

/**
 * 上传相关路径工具
 */
public final class UploadPathUtil {

    private static final String DEFAULT_PUBLIC_PREFIX = "/images";

    private UploadPathUtil() {
    }

    /**
     * 统一处理 publicPrefix，保证最终格式为 /images 这样的形式
     * - null/空白 -> /images
     * - images -> /images
     * - /images/ -> /images
     */
    public static String normalizePublicPrefix(String publicPrefix) {
        if (!StringUtils.hasText(publicPrefix)) {
            return DEFAULT_PUBLIC_PREFIX;
        }
        String p = publicPrefix.trim();
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        // 避免把 "/" 这种极端情况截断成空串
        if (p.endsWith("/") && p.length() > 1) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }
}
