package com.cct.codebox.utils;


import com.cct.codebox.domain.enums.RespCodeEnum;
import com.cct.codebox.exception.BusinessException;

/**
 * 抛异常工具类
 * @author cct
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition, RespCodeEnum errorCode) {
        throwIf(condition, new BusinessException(errorCode,errorCode.getMessage()));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition, RespCodeEnum errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
