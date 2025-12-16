package com.tq.security.interceptor;

import com.tq.common.enums.RoleEnum;
import com.tq.common.exception.ForbiddenException;
import com.tq.common.exception.UnauthorizedException;
import com.tq.common.util.JwtUtil;
import com.tq.config.properties.AuthTokenProperties;
import com.tq.security.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

@NullMarked
public abstract class AbstractRoleAuthInterceptor implements HandlerInterceptor {

    protected final AuthTokenProperties tokenProperties;
    protected final JwtUtil jwtUtil;

    protected AbstractRoleAuthInterceptor(AuthTokenProperties tokenProperties, JwtUtil jwtUtil) {
        this.tokenProperties = tokenProperties;
        this.jwtUtil = jwtUtil;
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
        if (token.isEmpty()) {
            throw new UnauthorizedException("未登录或登录已过期");
        }

        UserContext.UserPrincipal principal = jwtUtil.parsePrincipal(token);
        if (principal.role() != requiredRole()) {
            throw new ForbiddenException("无权限");
        }

        UserContext.set(principal);
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
