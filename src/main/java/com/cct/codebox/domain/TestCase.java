package com.cct.codebox.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cct
 * @description 题目测试用例视图
 */
@Data
public class TestCase implements Serializable {

    /**
     * 规定默认testCaseId从1开始顺序增长的正整数
     */
    @ApiModelProperty(value = "测试用例id")
    Integer testCaseId;

    @ApiModelProperty(value = "测试用例输入")
    String inputContent;

    @ApiModelProperty(value = "测试用例输出")
    String outputContent;

    private static final long serialVersionUID = 1L;
}