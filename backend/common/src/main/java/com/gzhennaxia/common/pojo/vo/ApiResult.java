package com.gzhennaxia.common.pojo.vo;

import com.gzhennaxia.common.enums.ResponseCode;

import java.time.LocalDateTime;

public class ApiResult<T> {
    private int code;
    private String message;
    private T data;
    private final String timestamp;

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // ------------------- 静态工厂方法（简化调用） -------------------

    /**
     * 成功响应（无业务数据）
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应（带业务数据）
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（带业务数据）
     * 自定义信息
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    /**
     * 失败响应（用预定义的错误码）
     */
    public static <T> ApiResult<T> error(ResponseCode responseCode) {
        return new ApiResult<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    /**
     * 失败响应（自定义错误信息）
     */
    public static <T> ApiResult<T> error(ResponseCode responseCode, String customMessage) {
        return new ApiResult<>(responseCode.getCode(), customMessage, null);
    }

    /**
     * 失败响应（完全自定义错误码和信息，用于特殊场景）
     */
    public static <T> ApiResult<T> error(int customCode, String customMessage) {
        return new ApiResult<>(customCode, customMessage, null);
    }
}
