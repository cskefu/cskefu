package com.cskefu.wechat;

import com.cskefu.wechat.message.*;
import io.micrometer.common.util.StringUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageSendRequest {
    private String touser;// 指定接收消息的客户UserID
    private String open_kfid;// 指定发送消息的客服账号ID
    private String msgid;// 非必填，指定消息ID
    private MessageTypeEnum msgtype;
    private FileMessage file;
    private ImageMessage image;
    private LinkMessage link;
    private LocationMessage location;
    private MiniprogramMessage miniprogram;
    private TextMessage text;
    private VideoMessage video;
    private VoiceMessage voice;
    private MenuMessage msgmenu;
    private CardMessage business_card;

    @Builder
    public MessageSendRequest(String touser, String open_kfid, String msgid, MessageTypeEnum msgtype, String text, String fileId, String imageId, String videoId, String voiceId, String userId, LinkMessage link, LocationMessage location, MiniprogramMessage miniprogram, MenuMessage msgmenu) {
        this.touser = touser;
        this.open_kfid = open_kfid;
        this.msgid = msgid;
        this.msgtype = msgtype;
        if (link != null) {
            this.link = link;
        }
        if (location != null) {
            this.location = location;
        }
        if (miniprogram != null) {
            this.miniprogram = miniprogram;
        }
        if (msgmenu != null) {
            this.msgmenu = msgmenu;
        }
        if (StringUtils.isNotEmpty(fileId) && MessageTypeEnum.FILE.equals(this.msgtype)) {
            this.file = FileMessage.builder().media_id(fileId).build();
        }
        if (StringUtils.isNotEmpty(imageId) && MessageTypeEnum.IMAGE.equals(this.msgtype)) {
            this.image = ImageMessage.builder().media_id(imageId).build();
        }
        if (StringUtils.isNotEmpty(text) && MessageTypeEnum.TEXT.equals(this.msgtype)) {
            this.text = TextMessage.builder().content(text).build();
        }
        if (StringUtils.isNotEmpty(videoId) && MessageTypeEnum.VIDEO.equals(this.msgtype)) {
            this.video = VideoMessage.builder().media_id(videoId).build();
        }
        if (StringUtils.isNotEmpty(voiceId) && MessageTypeEnum.VOICE.equals(this.msgtype)) {
            this.voice = VoiceMessage.builder().media_id(voiceId).build();
        }
        if (StringUtils.isNotEmpty(userId) && MessageTypeEnum.CARD.equals(this.msgtype)) {
            this.business_card = CardMessage.builder().userid(userId).build();
        }
    }
}
