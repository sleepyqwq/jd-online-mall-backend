package com.tq.common.exception;

import com.tq.common.api.ErrorCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
