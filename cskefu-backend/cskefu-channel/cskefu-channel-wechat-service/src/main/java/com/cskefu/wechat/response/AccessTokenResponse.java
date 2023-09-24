package com.cskefu.wechat.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AccessTokenResponse extends Response {
    private String access_token;
    private Long expires_in;
}
