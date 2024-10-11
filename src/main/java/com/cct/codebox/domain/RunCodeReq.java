package com.cct.codebox.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @BelongsProject: redmeatojbackend
 * @Author: cct
 * @Description: 运行代码所需的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RunCodeReq {

    /**
     * 语言
     */
    @NotNull
    String language;

    /**
     * 代码
     */
    @NotNull
    String code;

    /**
     * 输入内容
     */
    @NotNull
    String inputContent;

    /**
     * 时间限制
     */
    @NotNull
    Integer timeLimit;

    /**
     * 内存限制
     */
    @NotNull
    Integer memoryLimit;
}
