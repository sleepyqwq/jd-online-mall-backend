package com.tq.security.context;

import com.tq.common.enums.RoleEnum;

/**
 * 请求级用户上下文
 * 使用 ThreadLocal 保存当前请求解析出的用户信息
 */
public class UserContext {

    private static final ThreadLocal<UserPrincipal> HOLDER = new ThreadLocal<>();

    public static void set(UserPrincipal principal) {
        HOLDER.set(principal);
    }

    public static UserPrincipal get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        UserPrincipal p = HOLDER.get();
        return p == null ? null : p.userId();
    }

    public static void clear() {
        HOLDER.remove();
    }

    /**
         * 最小用户身份对象
         * 后续如需用户名、头像等，可再扩展字段
         */
        public record UserPrincipal(Long userId, RoleEnum role) {

    }
}
