/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.peer;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext.ChannelType;
import com.chatopera.cc.basic.MainContext.MessageType;
import com.chatopera.cc.basic.MainContext.ReceiverType;
import com.chatopera.cc.basic.plugins.PluginRegistry;
import com.chatopera.cc.peer.im.ComposeMw1;
import com.chatopera.cc.peer.im.ComposeMw2;
import com.chatopera.cc.peer.im.ComposeMw3;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.compose4j.Composer;
import com.chatopera.compose4j.Middleware;
import com.chatopera.compose4j.exception.Compose4jRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 客服与访客间消息处理类
 * 1. 路由消息
 * 2. 保存并业务处理
 */
@Component
public class PeerSyncIM implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(
            PeerSyncIM.class);

    private Composer<PeerContext> composer;

    private static ApplicationContext applicationContext;

    @Autowired
    private PluginRegistry pluginRegistry;

    @Autowired
    private ComposeMw1 imMw1;

    @Autowired
    private ComposeMw2 imMw2;

    @Autowired
    private ComposeMw3 imMw3;

    @PostConstruct
    public void postConstruct() {
        composer = new Composer<>();

        /**
         * 加载中间件
         */
        // 发布消息的准备工作
        composer.use(imMw1);

        // 通过webim发送消息
        composer.use(imMw2);

        // 通过Skype发送消息
        pluginRegistry.getPlugin(Constants.CSKEFU_MODULE_SKYPE).ifPresent(p -> {
            composer.use((Middleware) applicationContext.getBean(
                    p.getPluginId() + PluginRegistry.PLUGIN_CHANNEL_MESSAGER_SUFFIX));
        });

        composer.use(imMw3);

    }

    /**
     * 坐席与访客之间的对话
     *
     * @param receiverType 接收人类型
     * @param channelType  渠道类型
     * @param appid        渠道AppId
     * @param msgType      消息类型
     * @param touser       接收人ID
     * @param outMessage   发送内容
     * @param distribute   本机不存在连接，是否分发到其它机器
     */
    public void send(
            final ReceiverType receiverType,
            final ChannelType channelType,
            final String appid,
            final MessageType msgType,
            final String touser,
            final Message outMessage,
            final boolean distribute
    ) {
        logger.info(
                "[send] instant message \n msgType {}, \n channelType {}, \n appId {}, \n receiverType {}, \n touser {}, \n distribute {} \n",
                msgType.toString(), channelType.toString(), appid, receiverType.toString(), touser,
                distribute);

        PeerContext ctx = new PeerContext();
        ctx.setReceiverType(receiverType);
        ctx.setAppid(appid);
        ctx.setMsgType(msgType);
        ctx.setChannel(channelType);
        ctx.setTouser(touser);
        ctx.setDist(distribute);
        ctx.setMessage(outMessage);

        try {
            composer.handle(ctx);
        } catch (Compose4jRuntimeException e) {
            logger.info("[send] error", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }
}
