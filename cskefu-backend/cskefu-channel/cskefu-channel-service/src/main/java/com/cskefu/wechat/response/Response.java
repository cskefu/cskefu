package com.cskefu.wechat.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class Response {
    private Integer errcode;
    private String errmsg;

    public boolean success() {
        return Integer.valueOf(0).equals(errcode);
    }

    public boolean tokenExpire() {
        return Integer.valueOf(40014).equals(errcode);
    }
}
