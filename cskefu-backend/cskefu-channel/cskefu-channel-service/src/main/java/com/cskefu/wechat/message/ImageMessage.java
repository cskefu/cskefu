package com.cskefu.wechat.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageMessage implements MessageRequest, MessageResponse {
    private String media_id;

    @Builder
    public ImageMessage(String media_id) {
        this.media_id = media_id;
    }
}