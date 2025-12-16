package com.tq.common.exception;

import com.tq.common.api.ErrorCode;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
