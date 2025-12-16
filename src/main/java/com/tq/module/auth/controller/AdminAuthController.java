package com.tq.module.auth.controller;

import com.tq.common.api.Result;
import com.tq.common.util.UrlUtil;
import com.tq.module.auth.dto.LoginRequest;
import com.tq.module.auth.dto.LoginResponse;
import com.tq.module.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request, HttpServletRequest httpReq) {
            LoginResponse resp = authService.adminLogin(request);

        // 补全头像 fullUrl（避免前端不显示）
        String baseUrl = UrlUtil.buildBaseUrl(httpReq);
        if (resp != null && resp.getUser() != null) {
            resp.getUser().setAvatar(UrlUtil.toFullUrl(baseUrl, resp.getUser().getAvatar()));
        }
        return Result.ok(resp);
    }

    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> me(HttpServletRequest httpReq) {
        LoginResponse.UserInfo info = authService.currentAdmin();

        String baseUrl = UrlUtil.buildBaseUrl(httpReq);
        if (info != null) {
            info.setAvatar(UrlUtil.toFullUrl(baseUrl, info.getAvatar()));
        }
        return Result.ok(info);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.adminLogout();
        return Result.ok();
    }
}
