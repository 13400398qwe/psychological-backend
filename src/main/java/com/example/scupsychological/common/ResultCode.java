package com.example.scupsychological.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    // --- 通用成功 ---
    SUCCESS(200, "操作成功"),

    // --- 通用失败 ---
    ERROR(500, "操作失败"),

    // --- 客户端错误 4xxxx ---
    BAD_REQUEST(400, "错误的请求"),
    UNAUTHORIZED(401, "认证失败，请重新登录"),
    FORBIDDEN(403, "没有访问权限"),
    NOT_FOUND(404, "请求的资源不存在"),

    // --- 业务错误 5xxxx ---
    USER_NOT_FOUND(5001, "用户不存在"),
    USER_PASSWORD_ERROR(5002, "密码错误"),
    USERNAME_ALREADY_EXISTS(5003, "用户名已存在");


    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}