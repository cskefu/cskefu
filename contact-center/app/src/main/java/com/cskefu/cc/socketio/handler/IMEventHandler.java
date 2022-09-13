/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.socketio.handler;

import com.cskefu.cc.acd.ACDServiceRouter;
import com.cskefu.cc.acd.ACDVisitorDispatcher;
import com.cskefu.cc.acd.basic.ACDComposeContext;
import com.cskefu.cc.acd.basic.ACDMessageHelper;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.model.Contacts;
import com.cskefu.cc.model.CousultInvite;
import com.cskefu.cc.persistence.repository.AgentServiceRepository;
import com.cskefu.cc.proxy.AgentUserProxy;
import com.cskefu.cc.proxy.OnlineUserProxy;
import com.cskefu.cc.socketio.client.NettyClients;
import com.cskefu.cc.socketio.message.AgentStatusMessage;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.cskefu.cc.socketio.util.HumanUtils;
import com.cskefu.cc.socketio.util.IMServiceUtils;
import com.cskefu.cc.util.IP;
import com.cskefu.cc.util.IPTools;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class IMEventHandler {
    private final static Logger logger = LoggerFactory.getLogger(IMEventHandler.class);
    protected SocketIOServer server;

    public IMEventHandler(SocketIOServer server) {
        this.server = server;
    }

    static private AgentUserProxy agentUserProxy;
    static private AgentServiceRepository agentServiceRepository;
    static private ACDVisitorDispatcher acdVisitorDispatcher;

    /**
     * 接入访客并未访客寻找坐席服务人员
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            final String user = client.getHandshakeData().getSingleUrlParam("userid");
            final String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
            final String session = MainUtils.getContextID(client.getHandshakeData().getSingleUrlParam("session"));
            // 渠道标识
            final String appid = client.getHandshakeData().getSingleUrlParam("appid");
            // 要求目标坐席服务
            final String agent = client.getHandshakeData().getSingleUrlParam("agent");
            // 要求目标技能组服务
            final String skill = client.getHandshakeData().getSingleUrlParam("skill");
            // 是否是邀请后加入会话
            final boolean isInvite = StringUtils.equalsIgnoreCase(
                    client.getHandshakeData().getSingleUrlParam("isInvite"), "true");

            final String title = client.getHandshakeData().getSingleUrlParam("title");
            final String url = client.getHandshakeData().getSingleUrlParam("url");
            final String traceid = client.getHandshakeData().getSingleUrlParam("traceid");

            String nickname = client.getHandshakeData().getSingleUrlParam("nickname");

            final String osname = client.getHandshakeData().getSingleUrlParam("osname");
            final String browser = client.getHandshakeData().getSingleUrlParam("browser");

            logger.info(
                    "[onConnect] user {}, orgi {}, session {}, appid {}, agent {}, skill {}, title {}, url {}, traceid {}, nickname {}, isInvite {}",
                    user, orgi, session, appid, agent, skill, title, url, traceid, nickname, isInvite);

            // save connection info
            client.set("session", session);
            client.set("userid", user);
            client.set("appid", appid);
            client.set("isInvite", isInvite);

            // 保证传入的Nickname不是null
            if (StringUtils.isBlank(nickname)) {
                logger.info("[onConnect] reset nickname as it does not present.");
                nickname = "Guest_" + user;
            }

            if (StringUtils.isNotBlank(user)) {
                InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress();
                String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString());

                /**
                 * 加入到 缓存列表
                 */
                NettyClients.getInstance().putIMEventClient(user, client);

                /**
                 * 更新坐席服务类型
                 */
                IMServiceUtils.shiftOpsType(user, orgi, MainContext.OptType.HUMAN);

                IP ipdata = null;
                if ((StringUtils.isNotBlank(ip))) {
                    ipdata = IPTools.getInstance().findGeography(ip);
                }

                /**
                 * 用户进入到对话连接 ， 排队用户请求 , 如果返回失败，
                 * 表示当前坐席全忙，用户进入排队状态，当前提示信息 显示 当前排队的队列位置，
                 * 不可进行对话，用户发送的消息作为留言处理
                 */
                final ACDComposeContext ctx = ACDMessageHelper.getWebIMComposeContext(
                        user,
                        nickname,
                        orgi,
                        session,
                        appid,
                        ip,
                        osname,
                        browser,
                        "",
                        ipdata,
                        MainContext.ChannelType.WEBIM.toString(),
                        skill,
                        agent,
                        title,
                        url,
                        traceid,
                        user,
                        isInvite,
                        MainContext.ChatInitiatorType.USER.toString());
                getAcdVisitorDispatcher().enqueue(ctx);
                ACDServiceRouter.getAcdAgentService().notifyAgentUserProcessResult(ctx);
            } else {
                logger.warn("[onConnect] invalid connection, no user present.");
                //非法链接
                client.disconnect();
            }
        } catch (Exception e) {
            logger.error("[onConnect] error", e);
            client.disconnect();
        }
    }

    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        final String user = client.getHandshakeData().getSingleUrlParam("userid");
        final String orgi = client.getHandshakeData().getSingleUrlParam("orgi");
        logger.info("[onDisconnect] user {}, orgi {}", user, orgi);
        if (user != null) {
            try {
                /**
                 * 用户主动断开服务
                 */
                MainContext.getCache().findOneAgentUserByUserIdAndOrgi(user, orgi).ifPresent(p -> {
                    ACDServiceRouter.getAcdAgentService().finishAgentService(p
                            , orgi);
                });
            } catch (Exception e) {
                logger.warn("[onDisconnect] error", e);
            }
            NettyClients.getInstance().removeIMEventClient(
                    user, MainUtils.getContextID(client.getSessionId().toString()));
        }
    }

    // 消息接收入口，用于接受网站资源用户传入的 个人信息
    @OnEvent(value = "new")
    public void onNewEvent(SocketIOClient client, AckRequest request, Contacts contacts) {
        String user = client.getHandshakeData().getSingleUrlParam("userid");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");

        MainContext.getCache().findOneAgentUserByUserIdAndOrgi(user, orgi).ifPresent(p -> {
            p.setName(contacts.getName());
            p.setPhone(contacts.getPhone());
            p.setEmail(contacts.getEmail());
            p.setResion(contacts.getMemo());
            p.setChatbotops(false); // 非机器人客服
            p.setOpttype(MainContext.OptType.HUMAN.toString());
            getAgentUserProxy().save(p);
        });

        getAgentServiceRepository().findOneByUseridAndOrgiOrderByLogindateDesc(
                user, orgi).ifPresent(p -> {
            p.setName(contacts.getName());
            p.setPhone(contacts.getPhone());
            p.setEmail(contacts.getEmail());
            p.setResion(contacts.getMemo());
            agentServiceRepository.save(p);
        });
    }

    // 消息接收入口，坐席状态更新
    @OnEvent(value = "agentstatus")
    public void onAgentStatusEvent(SocketIOClient client, AckRequest request, AgentStatusMessage data) {
        logger.info("[onEvent] {}", data.getMessage());
    }

    // 消息接收入口，收发消息，用户向 坐席发送消息 和 向用户发送消息
    @OnEvent(value = "message")
    public void onMessageEvent(SocketIOClient client, AckRequest request, ChatMessage data) {
        if (data.getType() == null) {
            data.setType("message");
        }
        /**
         * 以下代码主要用于检查 访客端的字数限制
         */
        CousultInvite invite = OnlineUserProxy.consult(data.getAppid(), data.getOrgi());

        int dataLength = data.getMessage().length();
        if (invite != null && invite.getMaxwordsnum() > 0) {
            if (StringUtils.isNotBlank(data.getMessage()) && dataLength > invite.getMaxwordsnum()) {
                data.setMessage(data.getMessage().substring(0, invite.getMaxwordsnum()));
            }
        }
//        else if (StringUtils.isNotBlank(data.getMessage()) && dataLength > 600) {
//            data.setMessage(data.getMessage().substring(0, 600));
//        }
        /**
         * 处理表情
         */
        data.setMessage(MainUtils.processEmoti(data.getMessage()));
        HumanUtils.processMessage(data, data.getUserid());
    }

    private static AgentUserProxy getAgentUserProxy() {
        if (agentUserProxy == null) {
            agentUserProxy = MainContext.getContext().getBean(AgentUserProxy.class);
        }
        return agentUserProxy;
    }

    private static AgentServiceRepository getAgentServiceRepository() {
        if (agentServiceRepository == null) {
            agentServiceRepository = MainContext.getContext().getBean(AgentServiceRepository.class);
        }
        return agentServiceRepository;
    }

    private static ACDVisitorDispatcher getAcdVisitorDispatcher() {
        if (acdVisitorDispatcher == null) {
            acdVisitorDispatcher = MainContext.getContext().getBean(ACDVisitorDispatcher.class);
        }
        return acdVisitorDispatcher;
    }

}
