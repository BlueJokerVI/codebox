package com.cct.codebox.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @author cct
 * @param <T>
 */
@Data
@ApiModel(description = "基础响应体")
public class BaseResponse<T> implements Serializable {

    @ApiModelProperty(value = "响应码")
    private int code;

    @ApiModelProperty(value = "响应数据")
    private T data;

    @ApiModelProperty("响应描述")
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }


    public BaseResponse(int code) {
        this(code, null, "");
    }




}
