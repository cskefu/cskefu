package com.cskefu.wechat.message;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LinkMessage implements MessageRequest {
    private String title;
    private String desc; // 非必填
    private String url;//点击后跳转的链接
    private String thumb_media_id;//缩略图的media_id, 可以通过[素材管理](https://kf.weixin.qq.com/api/doc/path/94744#25551)接口获得

    @Builder
    public LinkMessage(String title, String desc, String url, String thumb_media_id) {
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.thumb_media_id = thumb_media_id;
    }
}
