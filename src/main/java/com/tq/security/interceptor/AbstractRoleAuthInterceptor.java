package com.tq.security.interceptor;

import com.tq.common.enums.RoleEnum;
import com.tq.common.exception.ForbiddenException;
import com.tq.common.exception.UnauthorizedException;
import com.tq.common.util.JwtUtil;
import com.tq.config.properties.AuthTokenProperties;
import com.tq.security.context.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@NullMarked
public abstract class AbstractRoleAuthInterceptor implements HandlerInterceptor {

    protected final AuthTokenProperties tokenProperties;
    protected final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    protected AbstractRoleAuthInterceptor(AuthTokenProperties tokenProperties,
                                          JwtUtil jwtUtil,
                                          StringRedisTemplate stringRedisTemplate) {
        this.tokenProperties = tokenProperties;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    protected abstract RoleEnum requiredRole();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String auth = request.getHeader(tokenProperties.getHeader());
        String prefix = tokenProperties.getPrefix();

        if (auth == null || !auth.startsWith(prefix)) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        String token = auth.substring(prefix.length()).trim();
        if (!StringUtils.hasText(token)) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        // 解析出 Claims 信息
        Claims claims = jwtUtil.parseClaims(token);
        String sub = claims.getSubject();
        String roleStr = claims.get("role", String.class);
        Integer version = claims.get("version", Integer.class);
        if (sub == null || roleStr == null || version == null) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        Long userId;
        RoleEnum role;
        try {
            userId = Long.valueOf(sub);
            role = RoleEnum.valueOf(roleStr);
        } catch (Exception e) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        // 角色校验
        if (role != requiredRole()) {
            throw new ForbiddenException("无权限");
        }

        // 版本号校验：从 Redis 取最新版本号
        String versionKey = "login_version:" + userId;
        String redisVersionStr = stringRedisTemplate.opsForValue().get(versionKey);
        int currentVersion = 1;
        if (StringUtils.hasText(redisVersionStr)) {
            try {
                currentVersion = Integer.parseInt(redisVersionStr);
            } catch (NumberFormatException ignored) {
                currentVersion = 1;
            }
        }
        if (!version.equals(currentVersion)) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        // 将用户信息放入上下文
        UserContext.set(new UserContext.UserPrincipal(userId, role));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                @Nullable Exception ex) {
        UserContext.clear();
    }
}