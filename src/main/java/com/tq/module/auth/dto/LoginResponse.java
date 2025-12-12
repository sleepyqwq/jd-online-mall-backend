package com.tq.module.auth.dto;

import lombok.Data;

/**
 * 登录成功响应体
 * data 中需包含 token、expireAt 与 user 信息
 */
@Data
public class LoginResponse {

    /**
     * JWT 字符串
     */
    private String token;

    /**
     * 过期时间字符串，格式 yyyy-MM-dd HH:mm:ss
     */
    private String expireAt;

    /**
     * 当前登录用户信息
     */
    private UserInfo user;

    @Data
    public static class UserInfo {
        private String id;
        private String username;
        private String nickname;
        private String avatar;
        private String role; // USER / ADMIN
    }
}
