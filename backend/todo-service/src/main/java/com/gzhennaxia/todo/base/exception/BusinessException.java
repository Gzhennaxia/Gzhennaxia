package com.gzhennaxia.todo.base.exception;

import com.gzhennaxia.todo.common.ResponseCode;

/**
 * 自定义业务异常：用于业务逻辑错误（如参数校验失败、资源不存在等）
 */
public class BusinessException extends RuntimeException {
    private final ResponseCode responseCode; // 错误码
    private final String customMessage;      // 自定义错误信息（可选）

    // 构造方法1：用预定义的错误码
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.customMessage = responseCode.getMessage();
    }

    // 构造方法2：自定义错误信息
    public BusinessException(ResponseCode responseCode, String customMessage) {
        super(customMessage);
        this.responseCode = responseCode;
        this.customMessage = customMessage;
    }

    // Getter 方法
    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}