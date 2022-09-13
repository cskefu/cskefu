package com.cskefu.cc.plugins.messenger;

import com.chatopera.bot.exception.ChatbotException;
import com.cskefu.cc.acd.ACDServiceRouter;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.exception.CSKefuException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.plugins.chatbot.ChatbotProxy;
import com.cskefu.cc.proxy.OnlineUserProxy;
import com.cskefu.cc.socketio.message.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.Optional;

@Component
public class MessengerChatbot {
    private final static Logger logger = LoggerFactory.getLogger(MessengerChatbot.class);

    @Autowired
    private ChatbotProxy chatbotProxy;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private ChatMessageRepository chatMessageRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private MessengerMessageProxy messengerMessageProxy;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    public boolean receiveMessage(String chatbotId, String fromId, String snsID, OnlineUser onlineUser, MainContext.MediaType msgType, String msg) throws ChatbotException, MalformedURLException {
        logger.info("[receiveMessage]  message: chatbotId {},fromId {},toId {},msg {} ", chatbotId, fromId, snsID, msg);
        // 在线客服访客咨询记录
        Date now = new Date();
        Optional<SNSAccount> optionalSNSAccount = snsAccountRes.findBySnsid(snsID);
        SNSAccount snsAccount = optionalSNSAccount.get();
        CousultInvite invite = OnlineUserProxy.consult(onlineUser.getAppid(), Constants.SYSTEM_ORGI);
        AgentUser agentUser = agentUserRes.findOneByUseridAndStatusNotAndChannelAndOrgi(fromId, MainContext.AgentUserStatusEnum.END.toString(), MainContext.ChannelType.MESSENGER.toString(), Constants.SYSTEM_ORGI).orElseGet(() -> {
            AgentUser au = new AgentUser(
                    onlineUser.getUserid(),
                    MainContext.ChannelType.MESSENGER.toString(),
                    onlineUser.getId(),
                    onlineUser.getUsername(),
                    Constants.SYSTEM_ORGI,
                    onlineUser.getAppid());

            au.setServicetime(now);
            au.setCreatetime(now);
            au.setUpdatetime(now);
            au.setLogindate(now);
            au.setSessionid(onlineUser.getSessionid());
            au.setRegion(onlineUser.getRegion());
            au.setUsername(onlineUser.getUsername());
            au.setSkill(snsAccount.getOrgan());
            au.setAppid(snsAccount.getSnsid());
            au.setOrgi(Constants.SYSTEM_ORGI);
            au.setNickname(onlineUser.getUsername());
            au.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());

            // 聊天机器人处理的请求
            au.setOpttype(MainContext.OptType.CHATBOT.toString());
            au.setAgentno(chatbotId); // 聊天机器人ID
            au.setAgentname(invite != null ? invite.getAiname() : "机器人客服");
            au.setCity(onlineUser.getCity());
            au.setProvince(onlineUser.getProvince());
            au.setCountry(onlineUser.getCountry());
            AgentService agentService = ACDServiceRouter.getAcdChatbotService().processChatbotService(
                    invite != null ? invite.getAiname() : "机器人客服", au, Constants.SYSTEM_ORGI);
            au.setAgentserviceid(agentService.getId());
            // 标记为机器人坐席
            au.setChatbotops(true);
            // 保存到MySQL
            agentUserRes.save(au);

            return au;
        });

        if (agentUser.isChatbotops()) {
            ChatMessage data = new ChatMessage();
            data.setMessage(msg);
            data.setUserid(fromId);
            data.setUsession(fromId); // 绑定唯一用户
            data.setSessionid(agentUser.getSessionid());
            data.setMessage(MainUtils.processEmoti(data.getMessage())); // 处理表情
            data.setTouser(chatbotId);
            data.setUsername(agentUser.getUsername());
            data.setAiid(chatbotId);
            data.setAgentserviceid(agentUser.getAgentserviceid());
            data.setChannel(agentUser.getChannel());
            data.setOrgi(Constants.SYSTEM_ORGI);
            data.setContextid(agentUser.getAgentserviceid()); // 一定要设置 ContextID
            data.setCalltype(MainContext.CallType.IN.toString());
            data.setMsgtype(msgType.toString());

            chatMessageRes.save(data);

            if (MainContext.MediaType.TEXT == msgType) {
                // 发送消息给Bot
                chatbotProxy.publishMessage(data, Constants.CHATBOT_EVENT_TYPE_CHAT);
            }

            return true;
        } else {
            return false;
        }
    }

    public void switchManualCustomerService(String fromId) {
        agentUserRes.findOneByUseridAndStatusNotAndChannelAndOrgi(fromId, MainContext.AgentUserStatusEnum.END.toString(), MainContext.ChannelType.MESSENGER.toString(), Constants.SYSTEM_ORGI).ifPresent(agentUser -> {
            if (agentUser.isChatbotops()) {
                Date now = new Date();
                AgentService agentService = agentServiceRes.findOne(agentUser.getAgentserviceid());
                agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                agentService.setEndtime(now);
                agentServiceRes.save(agentService);
                agentUser.setAgentserviceid(null);
                agentUser.setChatbotops(false);
                agentUser.setAgentno(null);
                agentUserRes.save(agentUser);

                snsAccountRes.findBySnsid(agentUser.getAppid()).ifPresent(snsAccount -> {
                    OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(fromId, Constants.SYSTEM_ORGI);
                    try {
                        messengerMessageProxy.scheduleMessengerAgentUser(agentUser, onlineUser, snsAccount, Constants.SYSTEM_ORGI);
                    } catch (CSKefuException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
