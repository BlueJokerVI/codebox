package com.cct.codebox.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @BelongsProject: redmeatojbackend
 * @Author: cct
 * @Description: 代码运行结果枚举类
 * @Version: 1.0
 */
@AllArgsConstructor
@Getter
public enum RunCodeResultEnum {
    /**
     *
     */
    SUCCESS(0,"运行成功"),
    TIME_LIMIT_EXCEEDED(1,"运行超时"),
    MEMORY_LIMIT_EXCEEDED(2,"内存超限"),
    OUTPUT_LIMIT_EXCEEDED(3,"输出超限"),
    RUNTIME_ERROR(4,"运行时错误"),
    COMPILE_ERROR(5,"编译错误");

    private final Integer value;
    private final String desc;

}
