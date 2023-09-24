package com.cskefu.wechat.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MiniprogramMessage implements MessageRequest, MessageResponse {
    private String title;
    private String appid;
    private String pagepath;
    private String thumb_media_id;

    @Builder
    public MiniprogramMessage(String title, String appid, String pagepath, String thumb_media_id) {
        this.title = title;
        this.appid = appid;
        this.pagepath = pagepath;
        this.thumb_media_id = thumb_media_id;
    }
}
