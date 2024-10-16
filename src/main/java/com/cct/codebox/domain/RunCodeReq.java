package com.cct.codebox.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    String language;

    /**
     * 代码
     */
    String code;

    /**
     * 提交记录id
     */
    Long submitRecordId;

    /**
     * 测试内容
     */
    List<TestCase> testCases;

    /**
     * 时间限制
     */
    Integer timeLimit;

    /**
     * 内存限制
     */
    Integer memoryLimit;
}
