package com.tq.module.auth.service;

import com.tq.module.auth.dto.LoginRequest;
import com.tq.module.auth.dto.LoginResponse;
import com.tq.module.auth.dto.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    String register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 管理员登录
     */
    LoginResponse adminLogin(LoginRequest request);

    /**
     * 获取当前登录用户信息（用户端）
     */
    LoginResponse.UserInfo currentUser();

    /**
     * 获取当前管理员信息
     */
    LoginResponse.UserInfo currentAdmin();

    /**
     * 用户退出登录（如果后续使用 Redis/黑名单，可以在这里处理）
     */
    void logout();

    /**
     * 管理员退出登录
     */
    void adminLogout();
}
