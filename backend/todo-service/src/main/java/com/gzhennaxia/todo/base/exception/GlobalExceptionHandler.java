package com.gzhennaxia.todo.base.exception;

import com.gzhennaxia.todo.common.ApiResult;
import com.gzhennaxia.todo.common.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器：捕获所有异常，封装为统一的 ApiResponse 格式
 */
@RestControllerAdvice(basePackages = "com.yourproject.controller") // 只处理指定包下的异常
public class GlobalExceptionHandler {

    /**
     * 1. 处理自定义业务异常（BusinessException）
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        // 返回自定义的错误码和信息
        return ApiResult.error(e.getResponseCode(), e.getCustomMessage());
    }

    /**
     * 2. 处理参数校验异常（@Valid 注解触发，如 @NotNull、@Size 等）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        // 拼接所有参数错误信息（如："username: 不能为空; password: 长度不能小于6"）
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        // 返回 400 错误码 + 拼接的错误信息
        return ApiResult.error(ResponseCode.BAD_REQUEST, errorMsg.toString().trim());
    }

    /**
     * 3. 处理 404 异常（资源未找到）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 设置 HTTP 状态码为 404（可选，前端可通过状态码快速判断）
    public ApiResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return ApiResult.error(ResponseCode.NOT_FOUND);
    }

    /**
     * 4. 处理其他未捕获的异常（兜底，如空指针、IO异常等）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置 HTTP 状态码为 500
    public ApiResult<Void> handleOtherException(Exception e) {
        // 生产环境建议记录日志（避免暴露敏感信息），这里简化处理
        e.printStackTrace();
        return ApiResult.error(ResponseCode.INTERNAL_ERROR, "服务器内部错误，请联系管理员");
    }
}