package com.tq.module.auth.dto;

import lombok.Data;

/**
 * 用户注册响应体
 * data 中按照接口文档约定返回 userId 字符串。
 */
@Data
public class RegisterResponse {

    private String userId;
}
