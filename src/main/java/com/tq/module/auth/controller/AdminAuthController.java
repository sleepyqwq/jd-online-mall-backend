package com.tq.module.auth.controller;

import com.tq.common.api.Result;
import com.tq.module.auth.dto.LoginRequest;
import com.tq.module.auth.dto.LoginResponse;
import com.tq.module.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台认证接口
 * 对应 /api/admin/auth/...
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse resp = authService.adminLogin(request);
        return Result.ok(resp);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> me() {
        LoginResponse.UserInfo info = authService.currentAdmin();
        return Result.ok(info);
    }

    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.adminLogout();
        return Result.ok();
    }
}
