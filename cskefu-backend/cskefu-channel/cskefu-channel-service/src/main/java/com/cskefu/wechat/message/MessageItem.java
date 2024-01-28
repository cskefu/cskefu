package com.cskefu.wechat.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class MessageItem {

    private static Map<String, Class> messageTypeMap = new HashMap(16);

    static {
        messageTypeMap.put("text", TextMessage.class);
        messageTypeMap.put("image", ImageMessage.class);
        messageTypeMap.put("video", VideoMessage.class);
        messageTypeMap.put("voice", VoiceMessage.class);
        messageTypeMap.put("file", FileMessage.class);
        messageTypeMap.put("location", LocationMessage.class);//位置消息
        messageTypeMap.put("event", EventMessage.class);//事件消息
        messageTypeMap.put("miniprogram", MiniprogramMessage.class);//小程序消息
        messageTypeMap.put("channels_shop_product", ShopProductMessage.class);//视频号商品消息
        messageTypeMap.put("channels_shop_order", ShopOrderMessage.class);// 视频号订单消息
    }

    private String msgid;
    private Long send_time;
    private Integer origin;
    private String msgtype;
    private TextMessage text;
    private ImageMessage image;
    private VideoMessage video;
    private VoiceMessage voice;
    private FileMessage file;
    private LocationMessage location;
    private EventMessage event;
    private MiniprogramMessage miniprogram;
    private ShopProductMessage channels_shop_product;
    private ShopOrderMessage channels_shop_order;
    private String open_kfid;
    private String external_userid;

    public Class messageType() {
        return messageTypeMap.get(msgtype);
    }
}
