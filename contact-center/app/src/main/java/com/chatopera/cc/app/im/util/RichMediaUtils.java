package com.chatopera.cc.app.im.util;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.cache.CacheHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RichMediaUtils {
    private final static Logger logger = LoggerFactory.getLogger(RichMediaUtils.class);

    /**
     * 上传图片
     *
     * @param image
     * @param attachid
     * @param size
     * @param name
     * @param userid
     * @return
     */
    public static ChatMessage uploadImage(String image, String attachid, int size, String name, String userid) {
        return createRichMediaMessage(image, size, name, MainContext.MediaTypeEnum.IMAGE.toString(), userid, attachid);
    }

    /**
     * 上传图片
     *
     * @param image
     * @param attachid
     * @param size
     * @param name
     * @param channel
     * @param userid
     * @param username
     * @param appid
     * @param orgi
     * @return
     */
    public static ChatMessage uploadImageWithChannel(String image, String attachid, int size, String name, String channel, String userid, String username, String appid, String orgi) {
        return createRichMediaMessageWithChannel(image, size, name, channel, MainContext.MediaTypeEnum.IMAGE.toString(), userid, username, appid, orgi, attachid);
    }


    /**
     * 上传文件
     *
     * @param url
     * @param size
     * @param name
     * @param userid
     * @param attachid
     * @return
     */
    public static ChatMessage uploadFile(String url, int size, String name, String userid, String attachid) {
        return createRichMediaMessage(url, size, name, MainContext.MediaTypeEnum.FILE.toString(), userid, attachid);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param size
     * @param name
     * @param channel
     * @param userid
     * @param username
     * @param appid
     * @param orgi
     * @param attachid
     * @return
     */
    public static ChatMessage uploadFileWithChannel(String url, int size, String name, String channel, String userid, String username, String appid, String orgi, String attachid) {
        return createRichMediaMessageWithChannel(url, size, name, channel, MainContext.MediaTypeEnum.FILE.toString(), userid, username, appid, orgi, attachid);
    }

    /**
     * 创建图片，文件消息
     *
     * @param message
     * @param length
     * @param name
     * @param msgtype
     * @param userid
     * @param attachid
     * @return
     */
    public static ChatMessage createRichMediaMessage(String message, int length, String name, String msgtype, String userid, String attachid) {
        AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI);
        ChatMessage data = new ChatMessage();
        data.setFilesize(length);
        data.setFilename(name);
        data.setAttachmentid(attachid);
        data.setMessage(message);
        data.setMsgtype(msgtype);
        data.setType(MainContext.MessageTypeEnum.MESSAGE.toString());

        if (agentUser != null) {
            data.setUserid(agentUser.getUserid());
            data.setUsername(agentUser.getUsername());
            data.setTouser(agentUser.getAgentno());
            data.setAppid(agentUser.getAppid());
            data.setOrgi(agentUser.getOrgi());
            if (agentUser.isChatbotops()) {
                // TODO #75 create Chatbot Message
                // https://github.com/chatopera/cosin/issues/75
                logger.info("[createRichMediaMessageWithChannel] TODO #75 create Chatbot Message");
            } else {
                HumanUtils.createMessage(data, msgtype, userid);
            }
        }
        return data;
    }

    /**
     * 创建图片，文件消息
     *
     * @param message
     * @param length
     * @param name
     * @param channel
     * @param msgtype
     * @param userid
     * @param username
     * @param appid
     * @param orgi
     * @param attachid
     * @return
     */
    public static ChatMessage createRichMediaMessageWithChannel(String message, int length, String name, String channel, String msgtype, String userid, String username, String appid, String orgi, String attachid) {
        ChatMessage data = new ChatMessage();
        data.setUserid(userid);
        data.setUsername(username);
        data.setTouser(userid);
        data.setAppid(appid);
        data.setOrgi(orgi);
        data.setChannel(channel);
        data.setMessage(message);
        data.setFilesize(length);
        data.setFilename(name);
        data.setAttachmentid(attachid);
        data.setMsgtype(msgtype);
        data.setType(MainContext.MessageTypeEnum.MESSAGE.toString());

        if (StringUtils.isNotBlank(userid)) {
            AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, MainContext.SYSTEM_ORGI);
            if ((agentUser != null) && StringUtils.equals(agentUser.getOpttype(), MainContext.OptTypeEnum.CHATBOT.toString())) {
                ChatbotUtils.createMessage(data, appid, channel, MainContext.CallTypeEnum.IN.toString(), MainContext.ChatbotItemType.USERINPUT.toString(), msgtype, data.getUserid(), orgi);
            } else {
                HumanUtils.createMessage(data, msgtype, userid);
            }
        }
        return data;
    }
}
