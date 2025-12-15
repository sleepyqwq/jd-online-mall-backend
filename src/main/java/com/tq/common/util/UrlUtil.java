package com.tq.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class UrlUtil {

    private UrlUtil() {}

    public static String buildBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    public static String toFullUrl(String baseUrl, String url) {
        if (!StringUtils.hasText(url)) {
            return url;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (!StringUtils.hasText(baseUrl)) {
            return url;
        }
        if (baseUrl.endsWith("/") && url.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + url;
        }
        if (!baseUrl.endsWith("/") && !url.startsWith("/")) {
            return baseUrl + "/" + url;
        }
        return baseUrl + url;
    }
}
