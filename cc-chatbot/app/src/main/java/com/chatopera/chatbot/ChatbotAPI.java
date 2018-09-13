/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.chatbot;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ChatbotAPI {
    private String schema;
    private String hostname;
    private int port;
    private String baseUrl;

    private ChatbotAPI() {
    }


    public ChatbotAPI(final String baseUrl) throws ChatbotAPIRuntimeException, MalformedURLException {
        if (StringUtils.isBlank(baseUrl))
            throw new ChatbotAPIRuntimeException("智能问答引擎URL不能为空。");

        URL url = new URL(baseUrl);
        this.schema = url.getProtocol();
        this.hostname = url.getHost();
        this.port = url.getPort();

        if (port == -1) {
            this.baseUrl = this.schema + "://" + this.hostname + "/api/v1";
        } else {
            this.baseUrl = this.schema + "://" + this.hostname + ":" + this.port + "/api/v1";
        }

    }

    public ChatbotAPI(final String schema, final String hostname, final int port, final String version) {
        this.schema = schema;
        this.hostname = hostname;
        this.port = port;
        this.baseUrl = schema + "://" + hostname + ":" + Integer.toString(this.port) + "/api/" + version;
    }

    public ChatbotAPI(final String schema, final String hostname, final int port) {
        this(schema, hostname, port, "v1");
    }

    public ChatbotAPI(final String hostname, final int port) {
        this("http", hostname, port);
    }

    public String getSchema() {
        return schema;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 获取聊天机器人列表
     *
     * @return
     * @throws ChatbotAPIRuntimeException
     */
    public JSONObject getChatbots(final String fields, final String q, final int page, final int limit) throws ChatbotAPIRuntimeException {
        try {
            HashMap<String, Object> queryString = new HashMap<String, Object>();
            if (StringUtils.isNotBlank(fields)) {
                queryString.put("fields", fields);
            }

            if (StringUtils.isNotBlank(q)) {
                queryString.put("q", q);
            }

            queryString.put("page", page);

            if (limit > 0) {
                queryString.put("limit", limit);
            }

            return RestAPI.get(this.getBaseUrl() + "/chatbot", queryString);
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }

    /**
     * 通过ChatbotID检查一个聊天机器人是否存在
     *
     * @param chatbotID
     * @return
     */
    public boolean exists(final String chatbotID) throws ChatbotAPIRuntimeException {
        try {
            JSONObject result = this.getChatbot(chatbotID);
            int rc = result.getInt("rc");
            if (rc == 0) {
                return true;
            } else if (rc == 3) {
                return false;
            } else {
                throw new ChatbotAPIRuntimeException("查询聊天机器人异常返回。");
            }
        } catch (Exception e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }


    /**
     * 创建聊天机器人
     *
     * @param chatbotID       聊天机器人标识，由[a-zA-Z0-9-]组成，字母开头
     * @param name            拟人化的名字
     * @param primaryLanguage 首选语言，支持 [zh_CN|en_US]
     * @param fallback        兜底回复
     * @param description     描述
     * @param welcome         欢迎语
     * @return
     */
    public JSONObject createBot(final String chatbotID,
                                final String name,
                                final String primaryLanguage,
                                final String fallback,
                                final String description,
                                final String welcome) throws ChatbotAPIRuntimeException {
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("chatbotID", chatbotID);
        body.put("name", name);
        body.put("primaryLanguage", primaryLanguage);
        body.put("description", description);
        body.put("fallback", fallback);
        body.put("welcome", welcome);

        try {
            return RestAPI.post(this.getBaseUrl() + "/chatbot/" + chatbotID, body);
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }

    /**
     * 更新聊天机器人
     *
     * @param chatbotID
     * @param description
     * @param fallback
     * @param welcome
     * @return
     * @throws ChatbotAPIRuntimeException
     */
    public boolean updateByChatbotID(final String chatbotID,
                                     final String name,
                                     final String description,
                                     final String fallback,
                                     final String welcome) throws ChatbotAPIRuntimeException {
        if (StringUtils.isBlank(chatbotID))
            throw new ChatbotAPIRuntimeException("不合法的参数，【chatbotID】不能为空。");

        HashMap<String, Object> body = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(description))
            body.put("description", description);
        if (StringUtils.isNotBlank(fallback))
            body.put("fallback", fallback);
        if (StringUtils.isNotBlank(welcome))
            body.put("welcome", welcome);
        if (StringUtils.isNotBlank(name))
            body.put("name", name);

        try {
            JSONObject result = RestAPI.put(this.baseUrl + "/chatbot/" + chatbotID, body, null);
            if (result.getInt("rc") == 0) {
                return true;
            } else {
                return false;
            }
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }

    }


    /**
     * 删除聊天机器人
     *
     * @param chatbotID
     * @return
     * @throws ChatbotAPIRuntimeException
     */
    public boolean deleteByChatbotID(final String chatbotID) throws ChatbotAPIRuntimeException {
        if (StringUtils.isBlank(chatbotID))
            throw new ChatbotAPIRuntimeException("聊天机器人ID不能为空。");
        try {
            JSONObject result = RestAPI.delete(this.getBaseUrl() + "/chatbot/" + chatbotID, null);
            if (result.getInt("rc") == 0)
                return true;
            return false;
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }

    /**
     * 获取聊天机器人详情
     *
     * @param chatbotID
     * @return
     * @throws ChatbotAPIRuntimeException
     */
    public JSONObject getChatbot(final String chatbotID) throws ChatbotAPIRuntimeException {
        try {
            return RestAPI.get(this.getBaseUrl() + "/chatbot/" + chatbotID);
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }

    /**
     * validate params
     *
     * @param chatbotID
     * @param fromUserId
     * @param textMessage
     */
    private void v(final String chatbotID, final String fromUserId, final String textMessage) throws ChatbotAPIRuntimeException {
        if (StringUtils.isBlank(chatbotID))
            throw new ChatbotAPIRuntimeException("[conversation] 不合法的聊天机器人标识。");

        if (StringUtils.isBlank(fromUserId))
            throw new ChatbotAPIRuntimeException("[conversation] 不合法的用户标识。");

        if (StringUtils.isBlank(textMessage))
            throw new ChatbotAPIRuntimeException("[conversation] 不合法的消息内容。");
    }

    /**
     * 与聊天机器人进行多轮对话
     *
     * @param fromUserId
     * @param textMessage
     * @param debug
     * @return
     */
    public JSONObject conversation(final String chatbotID, final String fromUserId, final String textMessage, boolean debug) throws ChatbotAPIRuntimeException {
        v(chatbotID, fromUserId, textMessage);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("fromUserId", fromUserId);
        body.put("textMessage", textMessage);
        body.put("isDebug", debug);

        try {
            JSONObject resp = RestAPI.post(this.getBaseUrl() + "/chatbot/" + chatbotID + "/conversation/query", body);
            return resp;
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }

    /**
     * 意图识别
     * @param chatbotID
     * @param clientId
     * @param textMessage
     * @return
     * @throws UnirestException
     */
    public JSONObject intent(final String chatbotID, final String clientId, final String textMessage) throws ChatbotAPIRuntimeException {
        if(StringUtils.isBlank(chatbotID) || StringUtils.isBlank(clientId) || StringUtils.isBlank(textMessage))
            throw new ChatbotAPIRuntimeException("参数不合法，不能为空。");

        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("clientId", clientId);
        body.put("query", textMessage);
        try {
            JSONObject result = RestAPI.post(this.baseUrl + "/chatbot/" + chatbotID, body);
            return result;
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }

    /**
     * 检索知识库
     *
     * @param chatbotID
     * @param fromUserId
     * @param textMessage
     * @param isDebug
     * @return
     */
    public JSONObject faq(final String chatbotID, final String fromUserId, final String textMessage, final boolean isDebug) throws ChatbotAPIRuntimeException {
        v(chatbotID, fromUserId, textMessage);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("fromUserId", fromUserId);
        body.put("query", textMessage);
        body.put("isDebug", isDebug);
        try {
            JSONObject resp = RestAPI.post(this.getBaseUrl() + "/chatbot/" + chatbotID + "/faq/query", body);
            return resp;
        } catch (UnirestException e) {
            throw new ChatbotAPIRuntimeException(e.toString());
        }
    }

}
