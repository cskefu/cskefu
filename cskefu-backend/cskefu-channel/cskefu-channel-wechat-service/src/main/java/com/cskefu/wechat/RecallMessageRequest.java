package com.cskefu.wechat;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecallMessageRequest {
    private String msgid;
    private String open_kfid;

    @Builder
    public RecallMessageRequest(String msgid, String open_kfid) {
        this.msgid = msgid;
        this.open_kfid = open_kfid;
    }
}
