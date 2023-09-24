package com.cskefu.wechat.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileMessage implements MessageRequest, MessageResponse {
    private String media_id;

    @Builder
    public FileMessage(String media_id) {
        this.media_id = media_id;
    }
}
