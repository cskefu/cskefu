package com.chatopera.chatbot;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Chatbot {
    private static final Logger logger = LoggerFactory.getLogger(Chatbot.class);
    private String schema;
    private String hostname;
    private int port;
    private String baseUrl;

    private Chatbot() {
    }

    public Chatbot(final String schema, final String hostname, final int port, final String version) {
        this.schema = schema;
        this.hostname = hostname;
        this.port = port;
        this.baseUrl = schema + "://" + hostname + ":" + Integer.toString(this.port) + "/api/" + version;
    }

    public Chatbot(final String schema, final String hostname, final int port) {
        this(schema, hostname, port, "v1");
    }

    public Chatbot(final String hostname, final int port) {
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
     * @throws ChatbotRuntimeException
     */
    public JSONObject getChatbots(final String fields, final String q, final int page, final int limit) throws ChatbotRuntimeException {
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
            throw new ChatbotRuntimeException(e.toString());
        }
    }

    /**
     * 获取聊天机器人详情
     *
     * @param chatbotID
     * @return
     * @throws ChatbotRuntimeException
     */
    public JSONObject getChatbot(final String chatbotID) throws ChatbotRuntimeException {
        try {
            return RestAPI.get(this.getBaseUrl() + "/chatbot/" + chatbotID);
        } catch (UnirestException e) {
            throw new ChatbotRuntimeException(e.toString());
        }
    }

    /**
     * validate params
     * @param chatbotID
     * @param fromUserId
     * @param textMessage
     */
    private void v(final String chatbotID, final String fromUserId, final String textMessage) throws ChatbotRuntimeException {
        if(StringUtils.isBlank(chatbotID))
            throw new ChatbotRuntimeException("[conversation] 不合法的聊天机器人标识。");

        if(StringUtils.isBlank(fromUserId))
            throw new ChatbotRuntimeException("[conversation] 不合法的用户标识。");

        if(StringUtils.isBlank(textMessage))
            throw new ChatbotRuntimeException("[conversation] 不合法的消息内容。");
    }

    /**
     * 与聊天机器人进行多轮对话
     * @param fromUserId
     * @param textMessage
     * @param debug
     * @return
     */
    public JSONObject conversation(final String chatbotID, final String fromUserId, final String textMessage, boolean debug) throws ChatbotRuntimeException {
        v(chatbotID, fromUserId, textMessage);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("fromUserId", fromUserId);
        body.put("textMessage", textMessage);
        body.put("isDebug", debug);

        logger.info("conversation body {}", body);

        try {
            JSONObject resp = RestAPI.post(this.getBaseUrl() + "/chatbot/" + chatbotID + "/conversation/query", body);
            return resp;
        } catch (UnirestException e) {
            throw new ChatbotRuntimeException(e.toString());
        }
    }

    /**
     * 检索知识库
     * @param chatbotID
     * @param fromUserId
     * @param textMessage
     * @param isDebug
     * @return
     */
    public JSONObject faq(final String chatbotID, final String fromUserId, final String textMessage, final boolean isDebug) throws ChatbotRuntimeException {
        v(chatbotID, fromUserId, textMessage);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("fromUserId", fromUserId);
        body.put("query", textMessage);
        body.put("isDebug", isDebug);
        try {
            JSONObject resp = RestAPI.post(this.getBaseUrl() + "/chatbot/" + chatbotID + "/faq/query", body);
            return resp;
        } catch (UnirestException e) {
            throw new ChatbotRuntimeException(e.toString());
        }
    }

}
