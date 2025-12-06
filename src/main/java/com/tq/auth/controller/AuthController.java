package com.tq.auth.controller;

import com.tq.auth.dto.UserLoginRequest;
import com.tq.auth.dto.UserLoginResponse;
import com.tq.auth.dto.UserMeResponse;
import com.tq.auth.dto.UserRegisterRequest;
import com.tq.auth.interceptor.UserContext;
import com.tq.auth.service.AuthService;
import com.tq.common.ApiResponse;
import com.tq.user.entity.User;
import com.tq.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody UserRegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }


    @GetMapping("/me")
    public ApiResponse<UserMeResponse> me() {
        Long userId = UserContext.getUserId();
        User user = userService.getByIdOrThrow(userId);

        UserMeResponse resp = new UserMeResponse();
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatar());

        return ApiResponse.success(resp);
    }

}
