package com.tq.security.interceptor;

import com.tq.common.enums.RoleEnum;
import com.tq.common.util.JwtUtil;
import com.tq.config.properties.AuthTokenProperties;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@NullMarked
@Component
public class AdminInterceptor extends AbstractRoleAuthInterceptor {

    public AdminInterceptor(AuthTokenProperties tokenProperties,
                            JwtUtil jwtUtil,
                            StringRedisTemplate stringRedisTemplate) {
        super(tokenProperties, jwtUtil, stringRedisTemplate);
    }

    @Override
    protected RoleEnum requiredRole() {
        return RoleEnum.ADMIN;
    }
}