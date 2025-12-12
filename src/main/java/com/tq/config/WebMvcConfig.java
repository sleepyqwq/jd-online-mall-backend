package com.tq.config;

import com.tq.config.properties.UploadProperties;
import com.tq.security.interceptor.AdminInterceptor;
import com.tq.security.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final UploadProperties uploadProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        /*
         * 用户端鉴权拦截
         * 仅对需要登录的路径生效，公开接口需要排除
         * 这里给出一个合理默认范围，后续你可以按接口文档细化白名单
         */
        registry.addInterceptor(authInterceptor)
                //先全部都拦截
                .addPathPatterns("/api/**")
                //在排除些不用拦截的
                .excludePathPatterns(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/upload", //文件上传应该被校验，但暂时先这么着
                        "/api/categories/**",
                        "/api/products/**",
                        // 关键：排除所有 admin 接口
                        "/api/admin/**"
                )
                //拦截器执行顺序，一个请求同时满足两个拦截器的规则时，数字越小，越先执行
                .order(1);

        /*
         * 管理端鉴权拦截
         * 通常所有 /api/admin/** 都需要管理员身份
         */
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns(
                        "/api/admin/auth/login"
                )
                .order(2);
    }

    //这是用来讲url转成本地路径用的
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 准备 URL 前缀，比如 "/images"
        String prefix = normalizePublicPrefix(uploadProperties.getPublicPrefix());

        // 2. 准备本地物理路径，比如 "D:/data/upload"
        String localDir = uploadProperties.getLocalDir();

        // 3. 转换路径格式
        Path absolute = Paths.get(localDir).toAbsolutePath().normalize();
        String location = absolute.toUri().toString();

        // 4. 建立映射关系
        registry.addResourceHandler(prefix + "/**")
                .addResourceLocations(location);
    }

    private String normalizePublicPrefix(String publicPrefix) {
        //防御性编程：无论你在配置文件里把 jd.upload.public-prefix 写成 images、/images 还是 /images/，这个方法都能把它统一处理成标准的 /images 格式
        if (!StringUtils.hasText(publicPrefix)) {
            return "/images";
        }
        String p = publicPrefix.trim();
        //防止路径开头缺少/
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        //防止路径末尾多了个/
        if (p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }
}
