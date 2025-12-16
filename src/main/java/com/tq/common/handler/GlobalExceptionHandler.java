package com.tq.common.handler;

import com.tq.common.api.ErrorCode;
import com.tq.common.api.Result;
import com.tq.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusiness(BusinessException ex) {
        return Result.fail(ex.getCode(), ex.getMessage(), ex.getData());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().isEmpty()
                ? ErrorCode.PARAM_INVALID.getMessage()
                : ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException ex) {
        String msg = ex.getAllErrors().isEmpty()
                ? ErrorCode.PARAM_INVALID.getMessage()
                : ex.getAllErrors().getFirst().getDefaultMessage();
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraint(ConstraintViolationException ex) {
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("JSON 解析失败: {}", ex.getMessage());
        return Result.fail(ErrorCode.PARAM_INVALID);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleUnknown(Exception ex) {
        log.error("未捕获异常", ex);
        return Result.fail(ErrorCode.INTERNAL_ERROR);
    }
}
