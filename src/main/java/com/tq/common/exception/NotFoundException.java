package com.tq.common.exception;

import com.tq.common.api.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
