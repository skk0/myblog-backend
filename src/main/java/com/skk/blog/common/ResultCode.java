package com.skk.blog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(400, "操作失败"),

    // 认证相关 401xx
    UNAUTHORIZED(401, "未登录或登录已过期"),
    TOKEN_INVALID(4011, "Token无效"),
    TOKEN_EXPIRED(4012, "Token已过期"),
    USERNAME_OR_PASSWORD_ERROR(4013, "用户名或密码错误"),
    ACCOUNT_DISABLED(4014, "账号已被禁用"),

    // 权限相关 403xx
    FORBIDDEN(403, "没有操作权限"),

    // 资源相关 404xx
    NOT_FOUND(404, "资源不存在"),

    // 参数相关 400xx
    PARAM_ERROR(400, "参数错误"),
    PARAM_IS_INVALID(4001, "参数无效"),
    PARAM_IS_BLANK(4002, "参数为空"),
    PARAM_TYPE_ERROR(4003, "参数类型错误"),

    // 服务器错误 500xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    FILE_UPLOAD_ERROR(5001, "文件上传失败"),
    DATABASE_ERROR(5002, "数据库异常");

    private final Integer code;
    private final String message;
}
