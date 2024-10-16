package com.cct.codebox.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @BelongsProject: redmeatojbackend
 * @Author: cct
 * @Description: 判题结果枚举
 * 判题结果，0 成功通过, 1 运行超时 ,3内存超限, 4运行时出错 ，5 编译失败 , 6 答案错误
 * @Version: 1.0
 */
@AllArgsConstructor
@Getter
public enum JudgeResultEnum {
    /**
     *
     */
    SUCCESS(0, "成功通过"),
    RUNTIME_OUT(1, "运行超时"),
    MEMORY_OUT(3, "内存超限"),
    RUNTIME_ERROR(4, "运行时出错"),
    COMPILE_ERROR(5, "编译失败"),
    ANSWER_ERROR(6, "答案错误");

    private final int code;
    private final String desc;

    public static JudgeResultEnum getEnumByCode(int code) {
        for (JudgeResultEnum judgeResultEnum : JudgeResultEnum.values()) {
            if (judgeResultEnum.getCode() == code) {
                return judgeResultEnum;
            }
        }
        return null;
    }

}
