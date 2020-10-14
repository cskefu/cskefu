/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.plugins.chatbot;

import com.chatopera.bot.exception.ChatbotException;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.Chatbot;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.persistence.repository.ChatbotRepository;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.cc.util.SerializeUtil;
import com.chatopera.cc.util.SystemEnvHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcloudapi.tbp.v20190627.models.Group;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

import com.tencentcloudapi.tbp.v20190627.TbpClient;

import com.tencentcloudapi.tbp.v20190627.models.TextProcessRequest;
import com.tencentcloudapi.tbp.v20190627.models.TextProcessResponse;

/**
 * 发送消息给聊天机器人并处理返回结果
 */
@Component
public class ChatbotEventSubscription {
    private final static Logger logger = LoggerFactory.getLogger(ChatbotEventSubscription.class);

    @Autowired
    private Cache cache;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private ChatbotRepository chatbotRes;

    // 机器人服务提供地址
    private final static String botServiecProvider = SystemEnvHelper.getenv(
            ChatbotConstants.BOT_PROVIDER, ChatbotConstants.DEFAULT_BOT_PROVIDER);

    // FAQ最佳回复阀值
    private final static double thresholdFaqBestReply = Double.parseDouble(SystemEnvHelper.getenv(
            ChatbotConstants.THRESHOLD_FAQ_BEST_REPLY, "0.8"));
    // FAQ建议回复阀值
    private final static double thresholdFaqSuggReply = Double.parseDouble(SystemEnvHelper.getenv(
            ChatbotConstants.THRESHOLD_FAQ_SUGG_REPLY, "0.6"));

    @Autowired
    private ChatbotProxy chatbotProxy;

    /**
     * 接收发送消息给聊天机器人的请求
     *
     * @param payload
     */
    @JmsListener(destination = Constants.INSTANT_MESSAGING_MQ_QUEUE_CHATBOT, containerFactory = "jmsListenerContainerQueue")
    public void onMessage(final String payload) {
        System.out.println(payload);
        ChatMessage message = SerializeUtil.deserialize(payload);
        System.out.println("Hello chat began");
        try {
            chat(message);
        } catch (MalformedURLException e) {
            logger.error("[onMessage] error", e);
        } catch (ChatbotException e) {
            logger.error("[onMessage] error", e);
        }
    }


    private void chat(final ChatMessage request) throws MalformedURLException, ChatbotException, JSONException {
        Chatbot c = chatbotRes.findOne(request.getAiid());
        //System.out.println(c);
        //System.out.println(request);
        logger.info("[chat] chat request  request baseUrl {},RobotType {}, chatbot {}, fromUserId {}, textMessage {}", botServiecProvider,c.getrobottype(), c.getName(), request.getUserid(), request.getMessage());
        // Get response from Conversational Engine.
        String RobotType = c.getrobottype();
        switch (RobotType) {
            case "cosin":
                CosinChat(c,request);
                break;
            case "tencent":
                //System.out.println("腾讯");
                TencentChat(c,request);
                break;
            case "rasa":
                //System.out.println("RASA");
                rasaChat(c,request);
                break;
        }


    }

    private void rasaChat(Chatbot c,ChatMessage request)
    {

        String BotUrl = c.getBaseUrl();
        JsonObject result = new JsonObject();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            URIBuilder uriBuilder = new URIBuilder(BotUrl);
            uriBuilder.setParameter("content",request.getMessage());

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000) //连接超时时间
                    .setConnectionRequestTimeout(5000) //请求超时时间
                    .setSocketTimeout(5000) //socket读写超时时间
                    .setRedirectsEnabled(true) //是否允许重定向
                    .build();

            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = null;
            response = httpClient.execute(httpGet);
            System.out.println(response);
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "utf8");
                JsonArray tempArray = new JsonParser().parse(content).getAsJsonArray();
                result = tempArray.get(0).getAsJsonObject();
                //System.out.println(result);
            }
            //请求内容

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // parse response
        if (result != null) {
            logger.info("[chat] chat response {}", result.toString());
            if (result.size()>0) {
                // reply
                //System.out.println(result.toString());
                JSONObject data = new JSONObject(result.toString());

                if (data != null) {
                    ChatMessage resp = new ChatMessage();
                    resp.setCalltype(MainContext.CallType.OUT.toString());
                    resp.setAppid(resp.getAppid());
                    resp.setOrgi(request.getOrgi());
                    resp.setAiid(request.getAiid());
                    resp.setMessage(result.get("text").getAsString());
                    if (data.has("logic_is_fallback"))
                    {
                    if (data.getBoolean("logic_is_fallback")) {
                        // 兜底回复，检查FAQ
                        JSONArray faqReplies = data.getJSONArray("faq");
                        JSONArray suggs = new JSONArray();
                        for (int i = 0; i < faqReplies.length(); i++) {
                            JSONObject sugg = new JSONObject();
                            JSONObject faqReply = faqReplies.getJSONObject(i);
                            sugg.put("label", Integer.toString(i + 1) + ". " + faqReply.getString("post"));
                            sugg.put("text", faqReply.getString("post"));
                            sugg.put("type", "qlist");
                            suggs.put(sugg);
                        }
                        if (suggs.length() > 0) {
                            // TODO set help message on View Page
                            resp.setMessage("为您找到如下信息：");
                            resp.setExpmsg(suggs.toString());
                        }
                    } else if (data.has("params")) {
                        resp.setExpmsg(data.get("params").toString());
                    }
                    }

                    resp.setTouser(request.getUserid());
                    resp.setAgentserviceid(request.getAgentserviceid());
                    resp.setMsgtype(request.getMsgtype());
                    resp.setUserid(request.getUserid());
                    resp.setType(request.getType());
                    resp.setChannel(request.getChannel());

                    resp.setContextid(request.getContextid());
                    resp.setSessionid(request.getSessionid());
                    resp.setUsession(request.getUsession());
                    resp.setUsername(c.getName());
                    resp.setUpdatetime(System.currentTimeMillis());

                    // 更新聊天机器人累计值
                    updateAgentUserWithRespData(request.getUserid(), request.getOrgi(), data);
                    // 保存并发送
                    System.out.println("resp");
                    System.out.println(resp);
                    chatbotProxy.saveAndPublish(resp);
                }
            } else {
                logger.warn("[chat] can not get expected response {}", result.toString());
            }
        }
    }
    private void TencentChat(Chatbot c,ChatMessage request){
        try{

            Credential cred = new Credential("AKID7yYrvk4adQx0nXHY7WfbbK1Sv84kAON5", "ZSmv33wYu0pLf162nHVSDiAMPgjW7tP9");

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tbp.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            TbpClient client = new TbpClient(cred, "", clientProfile);
            String BotId = c.getBotId();
            String BotEnv = c.getBotEnv();
            String TerminalId = c.getTerminalId();
            String InputText = request.getMessage();
            String params = "{" +
                    "\"BotId\":\""+BotId+"\"," +
                    "\"BotEnv\":\""+BotEnv+"\"," +
                    "\"TerminalId\":\""+TerminalId+"\"," +
                    "\"InputText\":\""+InputText+"\" }";
            System.out.println(params);
            TextProcessRequest req = TextProcessRequest.fromJsonString(params, TextProcessRequest.class);
            TextProcessResponse respout = client.TextProcess(req);

            //System.out.println(TextProcessResponse.toJsonString(respout));
            ChatMessage resp = new ChatMessage();
            resp.setCalltype(MainContext.CallType.OUT.toString());
            resp.setAppid(resp.getAppid());
            resp.setOrgi(request.getOrgi());
            resp.setAiid(request.getAiid());
            Group[] ResResult = respout.getResponseMessage().getGroupList();
            if (ResResult.length==1)
            {
                resp.setMessage(ResResult[0].getContent());
            }

            resp.setTouser(request.getUserid());
            resp.setAgentserviceid(request.getAgentserviceid());
            resp.setMsgtype(request.getMsgtype());
            resp.setUserid(request.getUserid());
            resp.setType(request.getType());
            resp.setChannel(request.getChannel());

            resp.setContextid(request.getContextid());
            resp.setSessionid(request.getSessionid());
            resp.setUsession(request.getUsession());
            resp.setUsername(c.getName());
            resp.setUpdatetime(System.currentTimeMillis());

            System.out.println(TextProcessResponse.toJsonString(respout));
            JSONObject resObject = new JSONObject(TextProcessResponse.toJsonString(respout));

            // 更新聊天机器人累计值
            updateAgentUserWithRespData(request.getUserid(), request.getOrgi(), resObject);
            // 保存并发送
            chatbotProxy.saveAndPublish(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

    private void CosinChat(Chatbot c,ChatMessage request) throws ChatbotException, MalformedURLException {
        com.chatopera.bot.sdk.Chatbot bot = new com.chatopera.bot.sdk.Chatbot(
                c.getClientId(), c.getSecret(), botServiecProvider);
        JSONObject result = bot.conversation(
                request.getUserid(), request.getMessage(), thresholdFaqBestReply, thresholdFaqSuggReply);

        // parse response
        if (result != null) {
            logger.info("[chat] chat response {}", result.toString());
            if (result.getInt(RestUtils.RESP_KEY_RC) == 0) {
                // reply
                JSONObject data = result.getJSONObject("data");
                if (data.has("logic_is_fallback")) {
                    ChatMessage resp = new ChatMessage();
                    resp.setCalltype(MainContext.CallType.OUT.toString());
                    resp.setAppid(resp.getAppid());
                    resp.setOrgi(request.getOrgi());
                    resp.setAiid(request.getAiid());
                    resp.setMessage(data.getString("string"));

                    if (data.getBoolean("logic_is_fallback")) {
                        // 兜底回复，检查FAQ
                        JSONArray faqReplies = data.getJSONArray("faq");
                        JSONArray suggs = new JSONArray();
                        for (int i = 0; i < faqReplies.length(); i++) {
                            JSONObject sugg = new JSONObject();
                            JSONObject faqReply = faqReplies.getJSONObject(i);
                            sugg.put("label", Integer.toString(i + 1) + ". " + faqReply.getString("post"));
                            sugg.put("text", faqReply.getString("post"));
                            sugg.put("type", "qlist");
                            suggs.put(sugg);
                        }
                        if (suggs.length() > 0) {
                            // TODO set help message on View Page
                            resp.setMessage("为您找到如下信息：");
                            resp.setExpmsg(suggs.toString());
                        }
                    } else if (data.has("params")) {
                        resp.setExpmsg(data.get("params").toString());
                    }

                    resp.setTouser(request.getUserid());
                    resp.setAgentserviceid(request.getAgentserviceid());
                    resp.setMsgtype(request.getMsgtype());
                    resp.setUserid(request.getUserid());
                    resp.setType(request.getType());
                    resp.setChannel(request.getChannel());

                    resp.setContextid(request.getContextid());
                    resp.setSessionid(request.getSessionid());
                    resp.setUsession(request.getUsession());
                    resp.setUsername(c.getName());
                    resp.setUpdatetime(System.currentTimeMillis());

                    // 更新聊天机器人累计值
                    updateAgentUserWithRespData(request.getUserid(), request.getOrgi(), data);
                    // 保存并发送
                    //System.out.println(resp);
                    chatbotProxy.saveAndPublish(resp);
                }
            } else {
                logger.warn("[chat] can not get expected response {}", result.toString());
            }
        }
    }


    /**
     * 根据聊天机器人返回数据更新agentUser
     *
     * @param userid
     * @param data
     *
     * 待修改 加入对腾讯对话机器人错误的兼容
     */
    private void updateAgentUserWithRespData(final String userid, final String orgi, final JSONObject data) throws JSONException {
        cache.findOneAgentUserByUserIdAndOrgi(userid, orgi).ifPresent(p -> {
            p.setChatbotround(p.getChatbotround() + 1);
            if (data.has("logic_is_unexpected") && data.getBoolean("logic_is_unexpected")) {
                p.setChatbotlogicerror(p.getChatbotlogicerror() + 1);
            }
            agentUserRes.save(p);
        });

    }

}
