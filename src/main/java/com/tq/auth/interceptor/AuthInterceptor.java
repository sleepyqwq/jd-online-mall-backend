package com.tq.auth.interceptor;

import com.tq.auth.token.JwtTokenService;
import com.tq.common.exception.BizException;
import com.tq.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BizException(ErrorCode.AUTH_ERROR, "请先登录");
        }

        String token = auth.substring(7);
        try {
            Claims claims = jwtTokenService.parse(token);
            Long userId = Long.valueOf(claims.getSubject());
            UserContext.setUserId(userId);
            return true;
        } catch (Exception e) {
            throw new BizException(ErrorCode.AUTH_ERROR, "登录已失效");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
