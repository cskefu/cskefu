package com.cskefu.wechat.message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventMessage implements MessageResponse {
    private String event_type;//事件类型，msg_send_fail,user_recall_msg
    private String open_kfid;//客服账号ID
    private String external_userid;//客户UserID

    /* 用户进入会话事件 */
    private Integer scene;
    private String scene_param;
    private String welcome_code;
    private EventMessageChannels wechat_channels;

    /* 消息发送失败事件 */
    private String fail_msgid;
    private String fail_type;

    /* 用户撤回消息事件 */
    private String recall_msgid;//撤回的消息msgid
}

@Data
@NoArgsConstructor
class EventMessageChannels {
    private String nickname;//进入会话的视频号名称
    private String shop_nickname;//频号小店名称
    private Integer scene;
}
