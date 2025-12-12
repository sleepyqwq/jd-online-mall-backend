package com.tq.module.auth.controller;

import com.tq.common.api.Result;
import com.tq.module.auth.dto.LoginRequest;
import com.tq.module.auth.dto.LoginResponse;
import com.tq.module.auth.dto.RegisterRequest;
import com.tq.module.auth.dto.RegisterResponse;
import com.tq.module.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端认证接口
 * 对应 /api/auth/...
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        String userId = authService.register(request);
        RegisterResponse resp = new RegisterResponse();
        resp.setUserId(userId);
        return Result.ok(resp);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse resp = authService.login(request);
        return Result.ok(resp);
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> me() {
        LoginResponse.UserInfo info = authService.currentUser();
        return Result.ok(info);
    }
}
