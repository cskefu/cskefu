/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cskefu.cc.plugins.messenger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chatopera.bot.exception.ChatbotException;
import com.cskefu.cc.acd.ACDAgentService;
import com.cskefu.cc.acd.ACDVisitorDispatcher;
import com.cskefu.cc.acd.basic.ACDComposeContext;
import com.cskefu.cc.acd.basic.ACDMessageHelper;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.exception.CSKefuException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.peer.PeerSyncIM;
import com.cskefu.cc.persistence.blob.JpaBlobHelper;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.cskefu.cc.socketio.message.Message;
import com.cskefu.cc.util.HttpClientUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MessengerMessageProxy {

    private final static Logger logger = LoggerFactory.getLogger(MessengerMessageProxy.class);

    private final String FACEBOOK_MESSAGES_API = "https://graph.facebook.com/v2.6/me/messages";

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @Autowired
    private ACDVisitorDispatcher acdVisitorDispatcher;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private Cache cache;

    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private FbMessengerRepository fbMessengerRepository;

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private MessengerChatbot messengerChatbot;

    @Autowired
    private FbOTNRepository otnRepository;

    @Autowired
    private FbOTNFollowRepository otnFollowRepository;

    @Autowired
    private ACDAgentService acdAgentService;

    @Autowired
    private ChatMessageRepository chatMessageRes;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Autowired
    private StreamingFileRepository streamingFileRes;

    @Value("${uk.im.server.host}")
    private String host;

    public void acceptOTNReq(String fromId, String toId, String otnToken, String ref) {
        FbOTN otn = otnRepository.findOne(ref);
        if (otn != null) {
            FbOtnFollow follow = new FbOtnFollow();
            follow.setId(MainUtils.getUUID());
            follow.setOtnId(ref);
            follow.setUserId(fromId);
            follow.setPageId(toId);
            follow.setOtnToken(otnToken);
            follow.setCreatetime(new Date());
            follow.setUpdatetime(new Date());

            otnFollowRepository.save(follow);

            otnRepository.incOneSubNumById(otn.getId());

            sendOTNContent(toId, fromId, JSONObject.parseObject(otn.getSuccessMessage()));
        }
    }

    public void acceptMeLink(String fromId, String toId, String ref) {
        FbOTN otn = otnRepository.findOne(ref);
        if (otn != null) {
            if (StringUtils.isNotBlank(otn.getPreSubMessage())) {
                Object obj = JSON.parse(otn.getPreSubMessage());
                if (obj instanceof JSONObject) {
                    JSONObject json = (JSONObject) obj;
                    sendOTNContent(toId, fromId, json);
                } else if (obj instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) obj;
                    sendOTNContent(toId, fromId, jsonArray.getJSONObject(0));
                    sendOTNContent(toId, fromId, jsonArray.getJSONObject(1));
                }
                otnRepository.incOneMelinkNumById(otn.getId());
            }

            if (StringUtils.isNotBlank(otn.getSubMessage())) {
                sendOTNReq(toId, fromId, otn.getSubMessage(), ref);
            }
        }
    }

    public void accept(String fromId, String toId, MainContext.MediaType msgType, String msg) throws CSKefuException, ChatbotException, MalformedURLException {
        Optional<SNSAccount> optionalSNSAccount = snsAccountRes.findBySnsid(toId);

        if (!optionalSNSAccount.isPresent()) {
            logger.warn("[handle] SnsAccount is null.");
            return;
        }

        SNSAccount snsAccount = optionalSNSAccount.get();

        Date now = new Date();

        OnlineUser onlineUser = onlineUserRes.findOneByUseridAndOrgi(fromId, Constants.SYSTEM_ORGI);
        if (onlineUser == null) {
            onlineUser = new OnlineUser();
        }

        if (StringUtils.isBlank(onlineUser.getUserid())) {
            Map<String, String> profile = getPersonName(toId, fromId);

            // 创建新的Onlineuser
            onlineUser.setId(MainUtils.getUUID());
            onlineUser.setUpdatetime(now);
            onlineUser.setUsername(profile.get("name"));
            onlineUser.setHeadimgurl(profile.get("profile_pic"));
            onlineUser.setCreatetime(now);
            onlineUser.setLogintime(now);
            onlineUser.setChannel(MainContext.ChannelType.MESSENGER.toString());
            onlineUser.setAppid(snsAccount.getSnsid());
            onlineUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString());
            onlineUser.setOrgi(Constants.SYSTEM_ORGI);
            onlineUser.setUserid(fromId);
            onlineUser.setUsertype(MainContext.OnlineUserType.MESSENGER.toString());
            onlineUserRes.save(onlineUser);
        } else if (cache.existBlackEntityByUserIdAndOrgi(onlineUser.getUserid(), Constants.SYSTEM_ORGI)) {
            // 检查该访客是否被拉黑
            logger.info("[handle] online user {} is in black list.", onlineUser.getId());
            return;
        }

        Chatbot c = chatbotRes.findBySnsAccountIdentifierAndOrgi(toId, Constants.SYSTEM_ORGI);
        if (c != null && c.isEnabled()) {

            if (!StringUtils.equals(Constants.CHATBOT_HUMAN_FIRST, c.getWorkmode())) {
                Boolean sendService = messengerChatbot.receiveMessage(c.getId(), fromId, toId, onlineUser, msgType, msg);
                if (sendService) {
                    return;
                }
            }
        } else if (c != null && !c.isEnabled() && StringUtils.equals(Constants.CHATBOT_CHATBOT_ONLY, c.getWorkmode())) {
            return;
        } else {
            agentUserRes.findOneByUseridAndStatusNotAndChannelAndOrgi(fromId, MainContext.AgentUserStatusEnum.END.toString(), MainContext.ChannelType.MESSENGER.toString(), Constants.SYSTEM_ORGI).ifPresent(p -> {
                if (p.isChatbotops()) {
                    messengerChatbot.switchManualCustomerService(fromId);
                }
            });
        }

        /**
         * 得到OnlineUser后获取AgentUser
         * 因为AgentUser是和OnlineUser关联的
         */
        // 一个OnlineUser可以对应多个agentUser, 此处获得
        AgentUser agentUser = agentUserRes.findOneByUseridAndStatusNotAndChannelAndOrgi(fromId, MainContext.AgentUserStatusEnum.END.toString(), MainContext.ChannelType.MESSENGER.toString(), Constants.SYSTEM_ORGI)
                .orElseGet(() -> new AgentUser());

        AgentService agentService;

        if (StringUtils.isBlank(agentUser.getAgentserviceid())) {
            FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(toId);
            if (fbMessenger != null && StringUtils.equals(fbMessenger.getStatus(), Constants.MESSENGER_CHANNEL_DISABLED)) {
                return;
            }

            // 没有加载到进行中的AgentUser，创建一个新的
            agentService = scheduleMessengerAgentUser(
                    agentUser, onlineUser, snsAccount, Constants.SYSTEM_ORGI).orElseThrow(
                    () -> new CSKefuException("Can not resolve AgentService Object."));
        } else {
            agentService = agentServiceRes.findOne(agentUser.getAgentserviceid());
        }


        /**
         * 给客服发送消息
         */
        if (agentUser != null && agentService != null) {
            ChatMessage chatMessage = new ChatMessage();
            Message outMessage = new Message();

            chatMessage.setMessage(msg);
            chatMessage.setOrgi(Constants.SYSTEM_ORGI);
            chatMessage.setUsername(agentUser.getName());
            chatMessage.setCalltype(MainContext.CallType.IN.toString());
            if (StringUtils.isNotBlank(agentUser.getAgentno())) {
                chatMessage.setTouser(agentUser.getUserid());
            }
            chatMessage.setChannel(MainContext.ChannelType.MESSENGER.toString());
            chatMessage.setUsession(agentUser.getUserid());
            chatMessage.setId(MainUtils.getUUID());
            chatMessage.setContextid(agentUser.getContextid());
            chatMessage.setUserid(agentUser.getUserid());
            chatMessage.setUsession(agentUser.getUserid());
            chatMessage.setAgentserviceid(agentUser.getAgentserviceid());
            chatMessage.setUsername(agentUser.getUsername());

            chatMessage.setMsgtype(msgType.toString());

            outMessage.setMessageType(chatMessage.getMsgtype());
            outMessage.setMessage(msg);
            outMessage.setAttachmentid(chatMessage.getAttachmentid());
            outMessage.setCalltype(MainContext.CallType.IN.toString());
            outMessage.setContextid(agentUser.getContextid());
            outMessage.setAgentUser(agentUser);

            outMessage.setChannelMessage(chatMessage);
            outMessage.setCreatetime(Constants.DISPLAY_DATE_FORMATTER.format(
                    chatMessage.getCreatetime()));

            outMessage.setMessage(chatMessage.getMessage());

            outMessage.setChannelMessage(chatMessage);
            outMessage.setAgentUser(agentUser);
            outMessage.setAgentService(agentService);
            outMessage.setCalltype(
                    MainContext.CallType.IN.toString());
            outMessage.setCreatetime(
                    Constants.DISPLAY_DATE_FORMATTER.format(
                            now));


            // Notify customer service to refresh the page
            if (StringUtils.isNotBlank(agentService.getAgentno())) {
                peerSyncIM.send(MainContext.ReceiverType.AGENT,
                        MainContext.ChannelType.MESSENGER,
                        agentUser.getAppid(),
                        MainContext.MessageType.MESSAGE,
                        agentService.getAgentno(), outMessage, true);
            } else {
                chatMessageRes.save((chatMessage));
            }

        } else {
            logger.info("[handle] agent user not found");
        }
    }

    /**
     * 创建新的AgentUser
     *
     * @param onlineUser 访客
     * @param snsAccount 社交信息账号
     * @param orgi       租户ID
     * @return
     */
    public Optional<AgentService> scheduleMessengerAgentUser(
            final AgentUser agentUser,
            final OnlineUser onlineUser,
            final SNSAccount snsAccount,
            final String orgi) throws CSKefuException {
        if (agentUser == null) {
            throw new CSKefuException("Invalid param for agentUser, should not be null.");
        }

        String channel = MainContext.ChannelType.MESSENGER.toString();
        Date now = new Date();
        agentUser.setUsername(onlineUser.getUsername());
        agentUser.setSkill(snsAccount.getOrgan());
        agentUser.setOrgi(orgi);
        agentUser.setNickname(onlineUser.getUsername());
        agentUser.setUserid(onlineUser.getUserid());
        agentUser.setStatus(MainContext.AgentUserStatusEnum.END.toString());
        agentUser.setLogindate(now);
        agentUser.setServicetime(now);
        agentUser.setCreatetime(now);
        agentUser.setUpdatetime(now);
        agentUser.setSessiontimes(System.currentTimeMillis() - now.getTime());
        agentUser.setChannel(channel);
        agentUser.setAppid(snsAccount.getSnsid());
        agentUserRes.save(agentUser);

        // 为访客安排坐席
        ACDComposeContext ctx = acdMessageHelper.getComposeContextWithAgentUser(
                agentUser, false, MainContext.ChatInitiatorType.USER.toString());
        ctx.setOnlineUserHeadimgUrl(onlineUser.getHeadimgurl());

        acdVisitorDispatcher.enqueue(ctx);
        acdAgentService.notifyAgentUserProcessResult(ctx);


        return Optional.ofNullable(ctx.getAgentService());
    }

    public Map<String, String> getPersonName(String pageId, String psid) {
        Map<String, String> result = new HashMap<>();
        FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(pageId);
        if (fbMessenger != null) {
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("access_token", fbMessenger.getToken());
            searchParams.put("fields", "locale,first_name,last_name,profile_pic");
            try {
                String res = HttpClientUtil.doGet("https://graph.facebook.com/" + psid, searchParams);
                JSONObject json = JSONObject.parseObject(res);
                String firstName = json.getString("first_name");
                String lastName = json.getString("last_name");

                result.put("profile_pic", json.getString("profile_pic"));

                saveProfilePic(result, json);

                if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
                    result.put("name", firstName + " " + lastName);
                }
            } catch (IOException e) {
                logger.error("[messenger] 详情获取异常", e);
            }
        }

        return result;
    }

    private void saveProfilePic(Map<String, String> result, JSONObject json) {
        try {
            String profile_pic = json.getString("profile_pic");

            if (StringUtils.isBlank(profile_pic)) {
                return;
            }

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(profile_pic);

            HttpResponse response = httpClient.execute(httpGet);
            //获取Http响应的码 200
            int startCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (startCode == 200 && entity != null) {
                InputStream input = entity.getContent();
                long size = entity.getContentLength();
                String fileid = MainUtils.getUUID();
                StreamingFile sf = new StreamingFile();
                sf.setId(fileid);
                sf.setName(fileid);
                sf.setMime(entity.getContentType().getValue());
                sf.setData(jpaBlobHelper.createBlob(input, size));
                streamingFileRes.save(sf);
                String fileURL = "/res/image.html?id=" + fileid;
                result.put("profile_pic", fileURL);
            }
        } catch (IOException exception) {

        }
    }

    public void sendOTNContent(String fromId, String toId, JSONObject json) {
        if (json.getString("type").equals("image")) {
            sendImage(fromId, toId, json.getString("url"));
        } else if (json.getString("type").equals("text") && StringUtils.isNotBlank(json.getString("content"))) {
            send(fromId, toId, json.getString("content"));
        }
    }

    public void sendOtnText(String fbToken, String otnToken, String msg) {
        logger.info("send messenger  to otnToken:{} msg:{}", otnToken, msg);

        JSONObject inputJson = JSONObject.parseObject(msg);

        try {
            JSONObject json = new JSONObject();
            JSONObject recipient = new JSONObject();
            recipient.put("one_time_notif_token", otnToken);
            JSONObject message = new JSONObject();
            if (inputJson.getString("type").equals("image")) {
                JSONObject attachment = new JSONObject();
                attachment.put("type", "image");
                JSONObject payload = new JSONObject();
                payload.put("url", "https://" + host + inputJson.getString("url"));
                attachment.put("payload", payload);
                message.put("attachment", attachment);
                json.put("recipient", recipient);
                json.put("message", message);
            } else {
                message.put("text", inputJson.getString("content"));
                message.put("metadata", "DEVELOPER_DEFINED_METADATA");
                json.put("recipient", recipient);
                json.put("message", message);
            }

            String result = HttpClientUtil.doPost(FACEBOOK_MESSAGES_API + "?access_token=" + fbToken, json.toJSONString());
            logger.info(result);
        } catch (IOException e) {
            logger.error("[messenger] 发送消息异常", e);
        }
    }

    public void send(String fromId, String toId, JSONObject message) {
        send(fromId, toId, message, null);
    }

    public void send(String fromId, String toId, JSONObject message, FbMessenger fbMessenger) {
        if (fbMessenger == null) {
            fbMessenger = fbMessengerRepository.findOneByPageId(fromId);
        }

        if (fbMessenger != null) {
            try {
                JSONObject json = new JSONObject();
                JSONObject recipient = new JSONObject();
                recipient.put("id", toId);
                json.put("recipient", recipient);
                json.put("message", message);

                String result = HttpClientUtil.doPost(FACEBOOK_MESSAGES_API + "?access_token=" + fbMessenger.getToken(), json.toJSONString());
                logger.info(result);
            } catch (IOException e) {
                logger.error("[messenger] 发送消息异常", e);
            }
        }
    }

    public void sendImage(String fromId, String toId, String imageUrl) {
        logger.info("send messenger fromId:{} toId:{} image:{}", fromId, toId, imageUrl);

        JSONObject message = new JSONObject();
        JSONObject attachment = new JSONObject();
        attachment.put("type", "image");
        JSONObject payload = new JSONObject();
        if (StringUtils.indexOf(imageUrl, "http") > -1) {
            payload.put("url", imageUrl);
        } else {
            payload.put("url", "https://" + host + imageUrl);
        }
        attachment.put("payload", payload);
        message.put("attachment", attachment);

        send(fromId, toId, message);
    }

    public void send(String fromId, String toId, String msg) {
        logger.info("send messenger fromId:{} toId:{} msg:{}", fromId, toId, msg);

        JSONObject message = new JSONObject();
        message.put("text", StringEscapeUtils.unescapeHtml(msg));

        send(fromId, toId, message);
    }

    public void sendOTNReq(String fromId, String toId, String title, String ref) {
        logger.info("send messenger fromId:{} toId:{} title:{}", fromId, toId, title);

        FbMessenger fbMessenger = fbMessengerRepository.findOneByPageId(fromId);
        if (fbMessenger != null) {
            try {
                JSONObject json = new JSONObject();
                JSONObject recipient = new JSONObject();
                recipient.put("id", toId);
                JSONObject message = new JSONObject();
                JSONObject attachment = new JSONObject();
                attachment.put("type", "template");
                JSONObject payload = new JSONObject();
                payload.put("template_type", "one_time_notif_req");
                payload.put("title", title);
                payload.put("payload", ref);
                attachment.put("payload", payload);
                message.put("attachment", attachment);
                json.put("recipient", recipient);
                json.put("message", message);

                String result = HttpClientUtil.doPost(FACEBOOK_MESSAGES_API + "?access_token=" + fbMessenger.getToken(), json.toJSONString());
                logger.info(result);
            } catch (IOException e) {
                logger.error("[messenger] 发送消息异常", e);
            }
        }
    }
}
