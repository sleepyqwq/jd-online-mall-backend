package com.tq.security.interceptor;

import com.tq.common.enums.RoleEnum;
import com.tq.common.util.JwtUtil;
import com.tq.config.properties.AuthTokenProperties;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@NullMarked
@Component
public class AuthInterceptor extends AbstractRoleAuthInterceptor {

    public AuthInterceptor(AuthTokenProperties tokenProperties, JwtUtil jwtUtil) {
        super(tokenProperties, jwtUtil);
    }

    @Override
    protected RoleEnum requiredRole() {
        return RoleEnum.USER;
    }
}
