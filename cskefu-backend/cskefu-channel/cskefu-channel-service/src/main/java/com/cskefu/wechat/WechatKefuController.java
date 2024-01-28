package com.cskefu.wechat;

import com.cskefu.wechat.response.Response;
import com.cskefu.wechat.response.SendMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/wechat/kefu/")
public class WechatKefuController {

    @Autowired
    private WechatKefuServiceImpl wechatKefuService;

    @PostMapping("/send")
    public SendMessageResponse sendMessage(String message) {
        MessageSendRequest request = MessageSendRequest.builder()
                .msgid(UUID.randomUUID().toString().replaceAll("-", ""))
                .open_kfid("wkN-IbUAAASo2A8u9GYPqGsmw_dtF7EA")
                .msgtype(MessageTypeEnum.TEXT)
                .touser("wmN-IbUAAA7_QTkIKOb4TMAggf1U-8ww")
                .text(message)
                .build();
        return wechatKefuService.sendMessage(request);
    }

    @PostMapping("/recall")
    public Response recallMessage(@RequestBody RecallMessageRequest request) {
        request.setOpen_kfid("wkN-IbUAAASo2A8u9GYPqGsmw_dtF7EA");
        return wechatKefuService.recallMessage(request);
    }
}
