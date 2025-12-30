package com.tq.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tq.common.api.ErrorCode;
import com.tq.common.exception.BusinessException;
import com.tq.common.exception.UnauthorizedException;
import com.tq.common.enums.RoleEnum;
import com.tq.common.util.JwtUtil;
import com.tq.config.properties.JwtProperties;
import com.tq.module.auth.dto.LoginRequest;
import com.tq.module.auth.dto.LoginResponse;
import com.tq.module.auth.dto.RegisterRequest;
import com.tq.module.auth.entity.User;
import com.tq.module.auth.mapper.UserMapper;
import com.tq.module.auth.service.AuthService;
import com.tq.security.context.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    private static final DateTimeFormatter EXPIRE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuthServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           JwtProperties jwtProperties,
                           StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String register(RegisterRequest request) {
        // 校验用户名唯一
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname())
                ? request.getNickname()
                : request.getUsername());
        user.setAvatar(request.getAvatar());
        user.setRole(RoleEnum.USER.name());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 接口文档要求返回 userId 字符串
        return String.valueOf(user.getId());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return doLogin(request, RoleEnum.USER);
    }

    @Override
    public LoginResponse adminLogin(LoginRequest request) {
        return doLogin(request, RoleEnum.ADMIN);
    }

    private LoginResponse doLogin(LoginRequest request, RoleEnum expectedRole) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .last("limit 1"));

        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "用户名或密码错误");
        }

        // 管理员登录接口要求 role=ADMIN，用户登录要求 role=USER
        if (!expectedRole.name().equals(user.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限登录该系统");
        }

        // 读取或初始化登录版本号
        String versionKey = "login_version:" + user.getId();
        String versionStr = stringRedisTemplate.opsForValue().get(versionKey);
        int version;
        if (!StringUtils.hasText(versionStr)) {
            version = 1;
            // 初始化版本号
            stringRedisTemplate.opsForValue().set(versionKey, String.valueOf(version));
        } else {
            try {
                version = Integer.parseInt(versionStr);
            } catch (NumberFormatException e) {
                version = 1;
                stringRedisTemplate.opsForValue().set(versionKey, String.valueOf(version));
            }
        }

        // 生成 JWT，包含版本号
        String token = jwtUtil.generateToken(user.getId(), expectedRole, version);

        long nowMs = System.currentTimeMillis();
        long expMs = nowMs + jwtProperties.getExpireSeconds() * 1000L;

        LocalDateTime expireAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(expMs),
                ZoneId.systemDefault()
        );

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setExpireAt(expireAt.format(EXPIRE_FORMATTER));
        resp.setUser(toUserInfo(user));

        return resp;
    }

    @Override
    public LoginResponse.UserInfo currentUser() {
        UserContext.UserPrincipal principal = UserContext.get();
        if (principal == null || principal.role() != RoleEnum.USER) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
        User user = userMapper.selectById(principal.userId());
        if (user == null) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
        return toUserInfo(user);
    }

    @Override
    public LoginResponse.UserInfo currentAdmin() {
        UserContext.UserPrincipal principal = UserContext.get();
        if (principal == null || principal.role() != RoleEnum.ADMIN) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
        User user = userMapper.selectById(principal.userId());
        if (user == null) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
        return toUserInfo(user);
    }

    @Override
    public void logout() {
        // 退出登录：增加版本号，令旧 Token 失效
        Long userId = UserContext.getUserId();
        if (userId != null) {
            String versionKey = "login_version:" + userId;
            // 若不存在则初始化为 1，再自增；若存在则直接自增
            stringRedisTemplate.opsForValue().increment(versionKey);
        }
    }

    @Override
    public void adminLogout() {
        // 管理端退出登录同样增加版本号，使旧 Token 失效
        Long userId = UserContext.getUserId();
        if (userId != null) {
            String versionKey = "login_version:" + userId;
            stringRedisTemplate.opsForValue().increment(versionKey);
        }
    }

    private LoginResponse.UserInfo toUserInfo(User user) {
        LoginResponse.UserInfo info = new LoginResponse.UserInfo();
        info.setId(String.valueOf(user.getId()));
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatar());
        info.setRole(user.getRole());
        return info;
    }
}