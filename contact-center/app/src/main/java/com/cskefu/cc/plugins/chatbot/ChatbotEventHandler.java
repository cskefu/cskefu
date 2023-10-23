/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.plugins.chatbot;

import com.chatopera.bot.sdk.Response;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.cskefu.cc.acd.ACDServiceRouter;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.AgentUserRepository;
import com.cskefu.cc.persistence.repository.ChatbotRepository;
import com.cskefu.cc.persistence.repository.PassportWebIMUserRepository;
import com.cskefu.cc.proxy.OnlineUserProxy;
import com.cskefu.cc.socketio.client.NettyClients;
import com.cskefu.cc.socketio.message.AgentStatusMessage;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.cskefu.cc.socketio.message.Message;
import com.cskefu.cc.socketio.util.IMServiceUtils;
import com.cskefu.cc.util.IP;
import com.cskefu.cc.util.IPTools;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.Date;

public class ChatbotEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotEventHandler.class);

    protected final SocketIOServer server;

    private static AgentUserRepository agentUserRes;
    private static PassportWebIMUserRepository onlineUserRes;
    private static ChatbotRepository chatbotRes;
    private static ChatbotProxy chatbotProxy;

    @Autowired
    public ChatbotEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            String user = client.getHandshakeData().getSingleUrlParam("userid");
            String nickname = client.getHandshakeData().getSingleUrlParam("nickname");
            String session = MainUtils.getContextID(client.getHandshakeData().getSingleUrlParam("session"));
            String appid = client.getHandshakeData().getSingleUrlParam("appid");
            String aiid = client.getHandshakeData().getSingleUrlParam("aiid");
            logger.info(
                    "[onConnect] userid {}, nickname {}, session {}, appid {}, aiid {}", user, nickname, session, appid,
                    aiid);

            client.set("aiid", aiid);
            client.set("session", session);
            client.set("userid", user);
            client.set("appid", appid);

            Date now = new Date();

            if (StringUtils.isNotBlank(user)) {
                /**
                 * 加入到 缓存列表
                 */
                NettyClients.getInstance().putChatbotEventClient(user, client);
                CousultInvite invite = OnlineUserProxy.consult(appid);

                /**
                 * 更新坐席服务类型
                 */
                IMServiceUtils.shiftOpsType(user, MainContext.OptType.CHATBOT);

                // send out tip
                Message tip = new Message();
                tip.setMessage("您正在使用机器人客服！");
                tip.setMessageType(MainContext.MessageType.MESSAGE.toString());
                tip.setCalltype(MainContext.CallType.IN.toString());
                tip.setCreatetime(MainUtils.dateFormate.format(now));

                client.sendEvent(MainContext.MessageType.STATUS.toString(), tip);

                // send out welcome message
                if (invite != null) {
                    Chatbot chatbot = getChatbotRes().findById(invite.getAiid()).orElse(null);
                    com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                            chatbot.getClientId(), chatbot.getSecret(), chatbot.getBaseUrl());
                    Response result = bot.command("GET", "/");

                    // 发送欢迎语
                    if (result.getRc() == 0) {
                        JSONObject details = (JSONObject) result.getData();
                        ChatMessage welcome = new ChatMessage();
                        String welcomeTextMessage = details.getString("welcome");
                        if (StringUtils.isNotBlank(welcomeTextMessage)) {
                            welcome.setCalltype(MainContext.CallType.OUT.toString());
                            welcome.setAppid(appid);
                            welcome.setAiid(aiid);
                            welcome.setMessage(welcomeTextMessage);
                            welcome.setTouser(user);
                            welcome.setMsgtype(MainContext.MessageType.MESSAGE.toString());
                            welcome.setUserid(user);
                            welcome.setUsername(invite.getAiname());
                            welcome.setUpdatetime(System.currentTimeMillis());
                            client.sendEvent(MainContext.MessageType.MESSAGE.toString(), welcome);
                        }

                        // 发送常见问题列表
                        JSONObject faqhotresp = bot.conversation(user, "__faq_hot_list");
                        logger.info("faqhot {}", faqhotresp.toString());
                        if (faqhotresp.getInt("rc") == 0) {
                            JSONObject faqhotdata = faqhotresp.getJSONObject("data");
                            if ((!faqhotdata.getBoolean("logic_is_fallback")) &&
                                    faqhotdata.has("string") &&
                                    faqhotdata.has("params")) {
                                ChatMessage faqhotmsg = new ChatMessage();
                                faqhotmsg.setCalltype(MainContext.CallType.OUT.toString());
                                faqhotmsg.setAppid(appid);
                                faqhotmsg.setAiid(aiid);
                                faqhotmsg.setMessage(faqhotdata.getString("string"));
                                faqhotmsg.setExpmsg(faqhotdata.getJSONArray("params").toString());
                                faqhotmsg.setTouser(user);
                                faqhotmsg.setMsgtype(MainContext.MessageType.MESSAGE.toString());
                                faqhotmsg.setUserid(user);
                                faqhotmsg.setUsername(invite.getAiname());
                                faqhotmsg.setUpdatetime(System.currentTimeMillis());
                                client.sendEvent(MainContext.MessageType.MESSAGE.toString(), faqhotmsg);
                            }
                        }
                    } else if (result.getRc() == 999 || result.getRc() == 998) {
                        logger.error("[chat] chatbot agent response rc {}, error {}", result.getRc(), result.getError());
                    } else {
                        logger.error("[chat] chatbot agent response rc {}, error {}", result.getRc(), result.getError());
                    }
                }

                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());
                PassportWebIMUser passportWebIMUser = getOnlineUserRes().findById(user).orElse(null);

                if (passportWebIMUser == null) {
                    passportWebIMUser = new PassportWebIMUser();
                    passportWebIMUser.setAppid(appid);
                    if (StringUtils.isNotBlank(nickname)) {
                        passportWebIMUser.setUsername(nickname);
                    } else {
                        passportWebIMUser.setUsername(Constants.GUEST_USER + "_" + MainUtils.genIDByKey(user));
                    }

                    passportWebIMUser.setSessionid(session);
                    passportWebIMUser.setOptype(MainContext.OptType.CHATBOT.toString());
                    passportWebIMUser.setUserid(user);
                    passportWebIMUser.setId(user);
                    passportWebIMUser.setChannel(MainContext.ChannelType.WEBIM.toString());
                    passportWebIMUser.setIp(ip);
                    passportWebIMUser.setUpdatetime(now);
                    passportWebIMUser.setLogintime(now);
                    passportWebIMUser.setCreatetime(now);
                    IP ipdata = IPTools.getInstance().findGeography(ip);
                    passportWebIMUser.setCity(ipdata.getCity());
                    passportWebIMUser.setCountry(ipdata.getCountry());
                    passportWebIMUser.setProvince(ipdata.getProvince());
                    passportWebIMUser.setIsp(ipdata.getIsp());
                    passportWebIMUser.setRegion(ipdata.getRegion());
                    passportWebIMUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString());
                }

                // 在线客服访客咨询记录
                AgentUser agentUser = new AgentUser(
                        passportWebIMUser.getId(),
                        MainContext.ChannelType.WEBIM.toString(), // callout
                        passportWebIMUser.getId(),
                        passportWebIMUser.getUsername(),
                        appid);

                agentUser.setServicetime(now);
                agentUser.setCreatetime(now);
                agentUser.setUpdatetime(now);
                agentUser.setSessionid(session);
                agentUser.setRegion(passportWebIMUser.getRegion());

                // 聊天机器人处理的请求
                agentUser.setOpttype(MainContext.OptType.CHATBOT.toString());
                agentUser.setAgentno(aiid); // 聊天机器人ID
                agentUser.setAgentname(invite != null ? invite.getAiname() : "机器人客服");
                agentUser.setCity(passportWebIMUser.getCity());
                agentUser.setProvince(passportWebIMUser.getProvince());
                agentUser.setCountry(passportWebIMUser.getCountry());
                AgentService agentService = ACDServiceRouter.getAcdChatbotService().processChatbotService(
                        invite != null ? invite.getAiname() : "机器人客服", agentUser);
                agentUser.setAgentserviceid(agentService.getId());

                // 标记为机器人坐席
                agentUser.setChatbotops(true);

                // 保存到MySQL
                getAgentUserRes().save(agentUser);
                getOnlineUserRes().save(passportWebIMUser);
            }
        } catch (Exception e) {
            logger.info("[onConnect] error", e);
        }
    }

    // 添加 @OnDisconnect 事件，客户端断开连接时调用，刷新客户端信息
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        if (StringUtils.isNotBlank(user)) {
            NettyClients.getInstance().removeChatbotEventClient(
                    user, MainUtils.getContextID(client.getSessionId().toString()));
            PassportWebIMUser passportWebIMUser = MainContext.getCache().findOneOnlineUserByUserId(user);

            MainContext.getCache().findOneAgentUserByUserId(user).ifPresent(p -> {
                ACDServiceRouter.getAcdChatbotService().processChatbotService(null, p);

                MainContext.getCache().deleteAgentUserByUserId(p);
                MainContext.getCache().deleteOnlineUserById(user);

                p.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                passportWebIMUser.setStatus(MainContext.OnlineUserStatusEnum.OFFLINE.toString());

                getAgentUserRes().save(p);
                getOnlineUserRes().save(passportWebIMUser);
            });
        }
        client.disconnect();
    }

    // 消息接收入口，网站有新用户接入对话
    @OnEvent(value = "new")
    public void onEvent(SocketIOClient client, AckRequest request, Message data) {

    }

    // 消息接收入口，坐席状态更新
    @OnEvent(value = "agentstatus")
    public void onEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data) {
        logger.info("[onEvent] agentstatus: ", data.getMessage());
    }

    // 消息接收入口，收发消息，用户向机器人发送消息
    @OnEvent(value = "message")
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data) {
        String aiid = client.get("aiid");
        String user = client.get("userid");
        String sessionid = client.get("session");
        String appid = client.get("appid");
        logger.info(
                "[onEvent]  message: session {}, aiid {}, userid {}, dataType {}, appid {}", sessionid, aiid,
                user, data.getType(), appid);

        // ignore event if dataType is not message.
        if (!StringUtils.equals(data.getType(), Constants.IM_MESSAGE_TYPE_MESSAGE)) {
            return;
        }

        MainContext.getCache().findOneAgentUserByUserId(user).ifPresent(p -> {
            /**
             * 以下代码主要用于检查 访客端的字数限制
             */
            CousultInvite invite = OnlineUserProxy.consult(data.getAppid());
            // ignore event if no invite found.
            if (invite == null) {
                return;
            }

            // ignore if Chatbot is turnoff.
            if (!invite.isAi()) {
                return;
            }

            Date now = new Date();
            if (invite.getMaxwordsnum() > 0) {
                if (StringUtils.isNotBlank(data.getMessage()) && data.getMessage().length() > invite.getMaxwordsnum()) {
                    data.setMessage(data.getMessage().substring(0, invite.getMaxwordsnum()));
                }
            } else if (StringUtils.isNotBlank(data.getMessage()) && data.getMessage().length() > 300) {
                data.setMessage(data.getMessage().substring(0, 300));
            }

            data.setUsession(user); // 绑定唯一用户
            data.setSessionid(sessionid);
            data.setMessage(MainUtils.processEmoti(data.getMessage())); // 处理表情
            data.setTouser(aiid);
            data.setUsername(p.getUsername());
            data.setAiid(aiid);
            data.setAgentserviceid(p.getAgentserviceid());
            data.setChannel(p.getChanneltype());
            data.setContextid(p.getAgentserviceid()); // 一定要设置 ContextID
            data.setCalltype(MainContext.CallType.IN.toString());

            // 保存并发送消息给访客
            getChatbotProxy().createTextMessage(
                    data,
                    MainContext.CallType.IN.toString());

            // 更新访客咨询记录
            p.setUpdatetime(now);
            p.setLastmessage(now);
            p.setLastmsg(data.getMessage());
            getAgentUserRes().save(p);

            // 发送消息给Bot
            getChatbotProxy().publishMessage(data, Constants.CHATBOT_EVENT_TYPE_CHAT);
        });


    }

    /**
     * Lazy load
     *
     * @return
     */
    private AgentUserRepository getAgentUserRes() {
        if (agentUserRes == null) {
            agentUserRes = MainContext.getContext().getBean(AgentUserRepository.class);
        }

        return agentUserRes;
    }

    /**
     * Lazy load
     *
     * @return
     */
    private ChatbotProxy getChatbotProxy() {
        if (chatbotProxy == null) {
            chatbotProxy = MainContext.getContext().getBean(ChatbotProxy.class);
        }
        return chatbotProxy;
    }

    private PassportWebIMUserRepository getOnlineUserRes() {
        if (onlineUserRes == null) {
            onlineUserRes = MainContext.getContext().getBean(PassportWebIMUserRepository.class);
        }

        return onlineUserRes;
    }

    private ChatbotRepository getChatbotRes() {
        if (chatbotRes == null) {
            chatbotRes = MainContext.getContext().getBean(ChatbotRepository.class);
        }

        return chatbotRes;
    }
}
