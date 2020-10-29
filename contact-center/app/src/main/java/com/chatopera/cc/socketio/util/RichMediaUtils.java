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
package com.chatopera.cc.socketio.util;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.socketio.message.ChatMessage;
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
        return createRichMediaMessage(image, size, name, MainContext.MediaType.IMAGE.toString(), userid, attachid);
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
        return createRichMediaMessageWithChannel(
                image, size, name, channel, MainContext.MediaType.IMAGE.toString(), userid, username, appid, orgi,
                attachid);
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
        return createRichMediaMessage(url, size, name, MainContext.MediaType.FILE.toString(), userid, attachid);
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
        return createRichMediaMessageWithChannel(
                url, size, name, channel, MainContext.MediaType.FILE.toString(), userid, username, appid, orgi,
                attachid);
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

        ChatMessage data = new ChatMessage();
        data.setFilesize(length);
        data.setFilename(name);
        data.setAttachmentid(attachid);
        data.setMessage(message);
        data.setMsgtype(msgtype);
        data.setType(MainContext.MessageType.MESSAGE.toString());

        MainContext.getCache().findOneAgentUserByUserIdAndOrgi(userid, Constants.SYSTEM_ORGI).ifPresent(p -> {
            data.setUserid(p.getUserid());
            data.setUsername(p.getUsername());
            data.setTouser(p.getAgentno());
            data.setAppid(p.getAppid());
            data.setOrgi(p.getOrgi());
            if (p.isChatbotops()) {
                // TODO #75 create Chatbot Message
                // https://github.com/chatopera/cosin/issues/75
                logger.info("[createRichMediaMessageWithChannel] TODO #75 create Chatbot Message");
            } else {
                HumanUtils.processMessage(data, msgtype, userid);
            }
        });

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
        data.setType(MainContext.MessageType.MESSAGE.toString());

        if (StringUtils.isNotBlank(userid)) {
            if (MainContext.getCache().findOneAgentUserByUserIdAndOrgi(
                    userid, Constants.SYSTEM_ORGI).filter(p -> StringUtils.equals(
                    p.getOpttype(), MainContext.OptType.CHATBOT.toString())).isPresent()) {
                // TODO 给聊天机器人发送图片或文字
                // #652 创建聊天机器人插件时去掉了对它的支持，需要将来实现
//                getChatbotProxy().createMessage(
//                        data, appid, channel, MainContext.CallType.IN.toString(),
//                        MainContext.ChatbotItemType.USERINPUT.toString(), msgtype, data.getUserid(), orgi);
            } else {
                HumanUtils.processMessage(data, msgtype, userid);
            }
        }
        return data;
    }
}
