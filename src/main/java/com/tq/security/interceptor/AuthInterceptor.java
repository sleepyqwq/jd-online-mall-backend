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
 * 用户端鉴权拦截器
 * 依赖 WebMvcConfig 的路径排除策略，仅处理需要登录的 /api/**
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenProperties tokenProperties;
    private final JwtUtil jwtUtil;

    public AuthInterceptor(AuthTokenProperties tokenProperties, JwtUtil jwtUtil) {
        this.tokenProperties = tokenProperties;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String uri = request.getRequestURI();
        System.out.println("AuthInterceptor preHandle, uri = " + uri);

        String auth = request.getHeader(tokenProperties.getHeader()); // 拿 Authorization 头
        String prefix = tokenProperties.getPrefix(); // 拿 Bearer 前缀

        System.out.println("Auth header = " + auth);

        if (auth == null || !auth.startsWith(prefix)) {
            System.out.println("Auth header missing or prefix not match");
            throw new UnauthorizedException("未登录或登录已过期");
        }

        String token = auth.substring(prefix.length()).trim();
        System.out.println("Raw token = " + token);
        if (token.isEmpty()) {
            System.out.println("Token empty after prefix trim");
            throw new UnauthorizedException("未登录或登录已过期");
        }

        UserContext.UserPrincipal principal = jwtUtil.parsePrincipal(token);
        System.out.println("Parsed principal: userId=" + principal.getUserId()
                + ", role=" + principal.getRole());

        // 用户端接口按约定要求普通用户角色
        if (principal.getRole() != RoleEnum.USER) {
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
