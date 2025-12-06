package com.tq.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    PARAM_ERROR(40001, "参数错误"),
    AUTH_ERROR(40002, "未登录或登录已失效"),
    FORBIDDEN(40003, "无权限访问"),
    NOT_FOUND(40400, "资源不存在"),
    BIZ_ERROR(40004, "业务处理失败"),
    SYSTEM_ERROR(50000, "系统异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
