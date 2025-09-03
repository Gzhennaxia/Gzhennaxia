package com.personal.management.common;

public enum ResponseCode {
    // 成功状态
    SUCCESS(200, "success"),
    // 客户端错误
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    // 服务器错误
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getter 方法
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}