package com.cskefu.mvc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultEnum {
    SERVLET_EXCEPTION(-5, "服务器内部错误"),
    DATA_ACCESS_EXCEPTION(-4, "服务器内部错误"),
    SQL_EXCEPTION(-3, "服务器内部错误"),
    BUSINESS_EXCEPTION(-2, "服务器内部错误"),
    UNKNOWN_EXCEPTION(-1, "服务器内部错误"),
    OK(0, "成功"),
    ILLEGAL_ARGUMENT_EXCEPTION(10, "请求参数错误");

    private Integer code;
    private String message;
}
