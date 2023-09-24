package com.cskefu.wechat.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class SendMessageResponse extends Response {
    private String msgid;
}
