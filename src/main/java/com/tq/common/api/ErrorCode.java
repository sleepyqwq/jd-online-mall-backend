package com.tq.common.api;

import lombok.Getter;

/**
 * 统一业务错误码
 * 与接口文档约定保持一致
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "成功"),

    PARAM_INVALID(40001, "参数校验失败"),
    UNAUTHORIZED(40002, "未登录或登录已过期"),
    FORBIDDEN(40003, "无权限"),
    STOCK_NOT_ENOUGH(40004, "库存不足"),

    NOT_FOUND(40400, "资源不存在"),

    INTERNAL_ERROR(50000, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
