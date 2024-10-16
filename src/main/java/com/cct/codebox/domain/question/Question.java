package com.cct.codebox.domain.question;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    /**
     * 题目id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目名
     */
    @TableField(value = "question_name")
    private String questionName;

    /**
     * 输入输出样例{
  "input":"",
  "output":""
}
     */
    @TableField(value = "question_io_example")
    private String questionIoExample;

    /**
     * 记录该题目的测试用例有多少个，同时测试用例命名规则：
输入文件名：x.in
输出文件名：x.out
x取值：从1开始顺序增长的正整数
     */
    @TableField(value = "question_io_total")
    private Integer questionIoTotal;

    /**
     * 题目描述
     */
    @TableField(value = "question_desc")
    private String questionDesc;

    /**
     * 题目标签  如：["数学","二叉树"]
     */
    @TableField(value = "question_tags")
    private String questionTags;

    /**
     * 题目解法内存限制，单位kb
     */
    @TableField(value = "question_mem_limit")
    private Integer questionMemLimit;

    /**
     * 题目解法时间限制，单位毫秒
     */
    @TableField(value = "question_time_limit")
    private Integer questionTimeLimit;

    /**
     * 题目提交次数
     */
    @TableField(value = "question_submit_num")
    private Integer questionSubmitNum;

    /**
     * 题目通过次数
     */
    @TableField(value = "question_ac_num")
    private Integer questionAcNum;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}