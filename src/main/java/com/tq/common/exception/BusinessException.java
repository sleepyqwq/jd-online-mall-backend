package com.tq.common.exception;

import com.tq.common.api.ErrorCode;
import lombok.Getter;

/**
 * 业务异常基类
 * 统一由 GlobalExceptionHandler 转换为 Result 结构
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
