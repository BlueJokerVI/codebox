package com.cct.codebox.domain.enums;

import lombok.Getter;

/**
 * @BelongsProject: redmeatojbackend
 * @Author: cct
 * @Description: 返回码枚举
 * @Version: 1.0
 */
@Getter
public enum RespCodeEnum {
    /**
     *
     */
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    ACCESS_TOO_FAST_ERROR(40102, "访问太快了"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    OSS_ERROR(50002, "oss操作失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String message;
    RespCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
