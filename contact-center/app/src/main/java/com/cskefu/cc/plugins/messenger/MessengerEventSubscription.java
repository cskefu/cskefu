package com.cskefu.cc.plugins.messenger;

import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.model.FbMessenger;
import com.cskefu.cc.model.FbOTN;
import com.cskefu.cc.model.FbOtnFollow;
import com.cskefu.cc.persistence.repository.FbMessengerRepository;
import com.cskefu.cc.persistence.repository.FbOTNFollowRepository;
import com.cskefu.cc.persistence.repository.FbOTNRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MessengerEventSubscription {
    private final static Logger logger = LoggerFactory.getLogger(MessengerEventSubscription.class);

    @Autowired
    private FbMessengerRepository fbMessengerRepository;

    @Autowired
    private FbOTNRepository otnRepository;

    @Autowired
    private FbOTNFollowRepository otnFollowRepository;

    @Autowired
    private MessengerMessageProxy messengerMessageProxy;

    @JmsListener(destination = Constants.INSTANT_MESSAGING_MQ_QUEUE_FACEBOOK_OTN, containerFactory = "jmsListenerContainerQueue")
    public void onPublish(final String jsonStr) {
        JSONObject payload = JSONObject.parseObject(jsonStr);
        String otnId = payload.getString("otnId");
        Date sendtime = payload.getTimestamp("sendtime");

        FbOTN otn = otnRepository.getOne(otnId);
        FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(otn.getPageId());
        if (fbMessenger != null && otn != null) {
            if (otn.getStatus().equals("create") && otn.getSendtime() != null && otn.getSendtime().equals(sendtime)) {
                otn.setStatus("sending");
                otnRepository.save(otn);
            }

            if (otn.getStatus().equals("sending")) {
                List<FbOtnFollow> follows = otnFollowRepository.findByOtnId(otn.getId());
                for (FbOtnFollow f : follows) {
                    if (f.getSendtime() == null) {
                        messengerMessageProxy.sendOtnText(fbMessenger.getToken(), f.getOtnToken(), otn.getOtnMessage());
                        f.setSendtime(new Date());
                        otnFollowRepository.save(f);
                    }
                }

                otn.setStatus("finish");
                otnRepository.save(otn);
            }
        }
    }
}
