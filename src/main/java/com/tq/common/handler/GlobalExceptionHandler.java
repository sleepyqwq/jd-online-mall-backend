package com.tq.common.handler;

import com.tq.common.api.ErrorCode;
import com.tq.common.api.Result;
import com.tq.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 * 失败响应必须为统一结构
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusiness(BusinessException ex) {
        return Result.fail(ex.getCode(), ex.getMessage(), ex.getData());
    }


    /**
     * @Valid 请求体校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().isEmpty()
                ? ErrorCode.PARAM_INVALID.getMessage()
                : ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), msg);
    }

    /**
     * 表单对象绑定校验失败
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException ex) {
        String msg = ex.getAllErrors().isEmpty()
                ? ErrorCode.PARAM_INVALID.getMessage()
                : ex.getAllErrors().getFirst().getDefaultMessage();
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), msg);
    }

    /**
     * @Validated 参数校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraint(ConstraintViolationException ex) {
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), ex.getMessage());
    }

    /**
     * JSON 解析失败
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        return Result.fail(ErrorCode.PARAM_INVALID);
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleUnknown(Exception ex) {
        return Result.fail(ErrorCode.INTERNAL_ERROR);
    }
}
