package com.tq.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tq.auth.dto.UserLoginRequest;
import com.tq.auth.dto.UserLoginResponse;
import com.tq.auth.dto.UserRegisterRequest;
import com.tq.auth.service.AuthService;
import com.tq.auth.token.JwtTokenService;
import com.tq.common.exception.BizException;
import com.tq.common.exception.ErrorCode;
import com.tq.user.entity.User;
import com.tq.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Override
    public void register(UserRegisterRequest request) {
        long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (count > 0) {
            throw new BizException(ErrorCode.BIZ_ERROR, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (user == null) {
            throw new BizException(ErrorCode.AUTH_ERROR, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.AUTH_ERROR, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ErrorCode.FORBIDDEN, "账号已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        String token = jwtTokenService.generateToken(user.getId(), user.getUsername());

        UserLoginResponse resp = new UserLoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setNickname(user.getNickname());
        resp.setAvatar(user.getAvatar());

        return resp;
    }
}
