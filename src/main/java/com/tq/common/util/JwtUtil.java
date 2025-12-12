package com.tq.common.util;

import com.tq.common.api.ErrorCode;
import com.tq.common.enums.RoleEnum;
import com.tq.common.exception.UnauthorizedException;
import com.tq.config.properties.JwtProperties;
import com.tq.security.context.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 负责生成、解析、校验 Token
 */
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void initKey() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.length() < 32) {
            // 密钥不符合要求时，直接用业务异常阻断启动或运行
            throw new IllegalStateException("jwt.secret 长度不足 32");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, RoleEnum role) {
        long now = System.currentTimeMillis();
        long expMs = jwtProperties.getExpireSeconds() * 1000L;

        return Jwts.builder()
                // A. 标准字段 (Subject)：通常放用户ID，这是 Token 的核心身份标识
                .subject(String.valueOf(userId))
                // B. 自定义字段 (Claim)：放入角色信息（USER 或 ADMIN）
                .claim("role", role.name())
                // C. 签发时间 (Issued At)
                .issuedAt(new Date(now))
                // D. 过期时间 (Expiration)：当前时间 + 7200秒
                .expiration(new Date(now + expMs))
                // E. 盖章签名 (Sign)：这是最关键的一步！
                // 使用你的私钥和算法对内容进行加密签名
                // 别人没有密钥，就无法伪造这个签名
                .signWith(key)
                // F. 压缩成字符串：生成最终的 eyJhbG... 字符串
                .compact();
    }

    /**
     * 解析 Token 并转换为 UserPrincipal
     * 解析失败、过期等情况统一按 40002 处理
     */
    public UserContext.UserPrincipal parsePrincipal(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)// <--- 关键：拿出验钞机（密钥）来验防伪标
                    .build()
                    .parseSignedClaims(token)// 如果签名不对，或者过期，这里会直接抛出异常
                    .getPayload();// 拿到里面的数据（ID, Role）

            // 2. 数据提取
            String sub = claims.getSubject();
            String roleStr = claims.get("role", String.class);

            if (sub == null || roleStr == null) {
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED.getMessage());
            }

            Long userId = Long.valueOf(sub);
            RoleEnum role = RoleEnum.valueOf(roleStr);

            return new UserContext.UserPrincipal(userId, role);
        } catch (Exception e) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
    }
}
