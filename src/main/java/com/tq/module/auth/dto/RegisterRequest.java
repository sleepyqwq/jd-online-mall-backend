package com.tq.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册请求体
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    //昵称，可选
    private String nickname;

    /**
     * 头像地址，可选
     * 可以是上传接口返回的 url 或 fullUrl
     */
    private String avatar;
}
