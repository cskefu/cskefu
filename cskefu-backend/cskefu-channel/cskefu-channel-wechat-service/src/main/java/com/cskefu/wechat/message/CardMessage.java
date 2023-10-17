package com.cskefu.wechat.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardMessage implements MessageRequest {
    private String userid;

    @Builder
    public CardMessage(String userid) {
        this.userid = userid;
    }
}
