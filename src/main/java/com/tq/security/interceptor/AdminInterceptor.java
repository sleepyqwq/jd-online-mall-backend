package com.tq.security.interceptor;

import com.tq.common.exception.ForbiddenException;
import com.tq.common.exception.UnauthorizedException;
import com.tq.common.enums.RoleEnum;
import com.tq.common.util.JwtUtil;
import com.tq.config.properties.AuthTokenProperties;
import com.tq.security.context.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 管理端鉴权拦截器
 * 统一拦截 /api/admin/**
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final AuthTokenProperties tokenProperties;
    private final JwtUtil jwtUtil;

    public AdminInterceptor(AuthTokenProperties tokenProperties, JwtUtil jwtUtil) {
        this.tokenProperties = tokenProperties;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String auth = request.getHeader(tokenProperties.getHeader());
        String prefix = tokenProperties.getPrefix();

        if (auth == null || !auth.startsWith(prefix)) {
            // 缺失、非法、过期统一 40002
            throw new UnauthorizedException("未登录或登录已过期");
        }

        String token = auth.substring(prefix.length()).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        UserContext.UserPrincipal principal = jwtUtil.parsePrincipal(token);

        // 管理端必须为 ADMIN
        if (principal.getRole() != RoleEnum.ADMIN) {
            throw new ForbiddenException("无权限");
        }

        UserContext.set(principal);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContext.clear();
    }
}
