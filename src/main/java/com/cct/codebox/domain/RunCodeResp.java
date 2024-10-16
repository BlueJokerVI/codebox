package com.cct.codebox.domain;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @BelongsProject: redmeatojbackend
 * @Author: cct
 * @Description: 运行代码结果封装类
 */
@Data
@ToString
public class RunCodeResp {

    /**
     * 运行输出
     */
    List<String> outputContexts;

    /**
     * 运行结果编号
     */
    Integer code;

    /**
     * 运行结果描述
     */
    String result;

    /**
     * 代码运行的最后测试用例编号
     */
    Integer lastTestCaseId;

    /**
     * 运行时间 单位ms
     */
    Integer timeConsume;

    /**
     * 运行内存 单位kb
     */
    Integer memoryConsume;
}
