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

package com.chatopera.cc.acd.middleware.visitor;

import com.chatopera.cc.acd.ACDQueueService;
import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.acd.basic.ACDMessageHelper;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.model.AgentUserContacts;
import com.chatopera.cc.model.Contacts;
import com.chatopera.cc.persistence.es.ContactsRepository;
import com.chatopera.cc.persistence.repository.AgentUserContactsRepository;
import com.chatopera.cc.proxy.AgentStatusProxy;
import com.chatopera.cc.proxy.AgentUserProxy;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Resolve AgentUser
 */
@Component
public class ACDVisBodyParserMw implements Middleware<ACDComposeContext> {
    private final static Logger logger = LoggerFactory.getLogger(ACDVisBodyParserMw.class);

    @Autowired
    private AgentUserContactsRepository agentUserContactsRes;

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private Cache cache;

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Autowired
    private AgentStatusProxy agentStatusProxy;

    @Autowired
    private ACDQueueService acdQueueService;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    /**
     * 设置AgentUser基本信息
     *
     * @param ctx
     * @param next
     */
    @Override
    public void apply(final ACDComposeContext ctx, final Functional next) {

        /**
         * NOTE AgentUser代表一次会话记录，在上一个会话结束，并且由坐席人员点击"清除"后，会从数据库中删除
         * 此处查询到的，可能是之前的会话。其状态需要验证，所以不一定是由TA来服务本次会话。
         */
        AgentUser agentUser = cache.findOneAgentUserByUserIdAndOrgi(ctx.getOnlineUserId(), ctx.getOrgi()).orElseGet(
                () -> {
                    /**
                     * NOTE 新创建的AgentUser不需要设置Status和Agentno
                     * 因为两个值在后面会检查，如果存在则不会申请新的Agent
                     */
                    AgentUser p = new AgentUser(
                            ctx.getOnlineUserId(),
                            ctx.getChannel(),
                            ctx.getOnlineUserId(),
                            ctx.getOnlineUserNickname(),
                            ctx.getOrgi(),
                            ctx.getAppid());
                    logger.info("[apply] create new agent user id {}", p.getId());
                    return p;
                });


        logger.info("[apply] resolve agent user id {}", agentUser.getId());

        agentUser.setOrgi(ctx.getOrgi());
        agentUser.setUsername(resolveAgentUsername(agentUser, ctx.getOnlineUserNickname()));
        agentUser.setOsname(ctx.getOsname());
        agentUser.setBrowser(ctx.getBrowser());
        agentUser.setAppid(ctx.getAppid());
        agentUser.setSessionid(ctx.getSessionid());

        if (ctx.getIpdata() != null) {
            logger.info("[apply] set IP data for agentUser {}", agentUser.getId());
            agentUser.setCountry(ctx.getIpdata().getCountry());
            agentUser.setProvince(ctx.getIpdata().getProvince());
            agentUser.setCity(ctx.getIpdata().getCity());
            if (StringUtils.isNotBlank(ctx.getIp())) {
                agentUser.setRegion(ctx.getIpdata().toString() + "[" + ctx.getIp() + "]");
            } else {
                agentUser.setRegion(ctx.getIpdata().toString());
            }
        }

        agentUser.setOwner(ctx.getOwnerid());        // 智能IVR的 EventID
        agentUser.setHeadimgurl(ctx.getOnlineUserHeadimgUrl());
        agentUser.setTitle(ctx.getTitle());
        agentUser.setUrl(ctx.getUrl());
        agentUser.setTraceid(ctx.getTraceid());
        ctx.setAgentUser(agentUser);

        next.apply();

        /**
         * 发送通知
         */
        if (ctx.getAgentService() != null && StringUtils.isNotBlank(ctx.getAgentService().getStatus())) {
            /**
             * 找到空闲坐席，如果未找到坐席，则将该用户放入到 排队队列
             */
            switch (MainContext.AgentUserStatusEnum.toValue(ctx.getAgentService().getStatus())) {
                case INSERVICE:
                    ctx.setMessage(
                            acdMessageHelper.getSuccessMessage(
                                    ctx.getAgentService(),
                                    ctx.getChannel(),
                                    ctx.getOrgi()));

                    // TODO 判断 INSERVICE 时，agentService 对应的  agentUser
                    logger.info(
                            "[apply] agent service: agentno {}, \n agentuser id {} \n user {} \n channel {} \n status {} \n queue index {}",
                            ctx.getAgentService().getAgentno(), ctx.getAgentService().getAgentuserid(),
                            ctx.getAgentService().getUserid(),
                            ctx.getAgentService().getChannel(),
                            ctx.getAgentService().getStatus(),
                            ctx.getAgentService().getQueneindex());

                    if (StringUtils.isNotBlank(ctx.getAgentService().getAgentuserid())) {
                        agentUserProxy.findOne(ctx.getAgentService().getAgentuserid()).ifPresent(p -> {
                            ctx.setAgentUser(p);
                        });
                    }

                    // TODO 如果是 INSERVICE 那么  agentService.getAgentuserid 就一定不能为空？
//                            // TODO 此处需要考虑 agentService.getAgentuserid 为空的情况
//                            // 那么什么情况下，agentService.getAgentuserid为空？
//                            if (StringUtils.isNotBlank(agentService.getAgentuserid())) {
//                                logger.info("[handle] set Agent User with agentUser Id {}", agentService.getAgentuserid());
//                                getAgentUserProxy().findOne(agentService.getAgentuserid()).ifPresent(p -> {
//                                    outMessage.setChannelMessage(p);
//                                });
//                            } else {
//                                logger.info("[handle] agent user id is null.");
//                            }

                    agentStatusProxy.broadcastAgentsStatus(
                            ctx.getOrgi(), "user", MainContext.AgentUserStatusEnum.INSERVICE.toString(),
                            ctx.getAgentUser().getId());
                    break;
                case INQUENE:
                    // 处理结果：进入排队队列
                    ctx.getAgentService().setQueneindex(
                            acdQueueService.getQueueIndex(
                                    ctx.getAgentUser().getAgentno(), ctx.getOrgi(), ctx.getAgentUser().getSkill()));

                    if (ctx.getAgentService().getQueneindex() > 0) {
                        // 当前有坐席，要排队
                        ctx.setMessage(acdMessageHelper.getQueneMessage(
                                ctx.getAgentService().getQueneindex(),
                                ctx.getAgentUser().getChannel(),
                                ctx.getOrganid(),
                                ctx.getOrgi()));
                    } else {
                        // TODO 什么是否返回 noAgentMessage, 是否在是 INQUENE 时 getQueneindex == 0
                        // 当前没有坐席，要留言
                        ctx.setNoagent(true);
                        ctx.setMessage(acdMessageHelper.getNoAgentMessage(
                                ctx.getAgentService().getQueneindex(),
                                ctx.getChannel(),
                                ctx.getOrganid(),
                                ctx.getOrgi()));
                    }

                    agentStatusProxy.broadcastAgentsStatus(
                            ctx.getOrgi(), "user", MainContext.AgentUserStatusEnum.INQUENE.toString(),
                            ctx.getAgentUser().getId());

                    break;
                case END:
                    logger.info("[handler] should not happen for new onlineUser service request.");
                default:
            }
            ctx.setChannelMessage(ctx.getAgentUser());
        }

        logger.info(
                "[apply] message text: {}, noagent {}", ctx.getMessage(), ctx.isNoagent());
    }


    /**
     * 确定该访客的名字，优先级
     * 1. 如果AgentUser username 与 nickName 不一致，则用 agentUser username
     * 2. 如果AgentUser username 与 nickName 一致，则查找 AgentUserContact对应的联系人
     * 2.1 如果联系人存在，则用联系人的名字
     * 2.2 如果联系人不存在，则使用 nickName
     * <p>
     * TODO 此处有一些问题：如果联系人更新了名字，那么么后面TA的会话用的还是旧的名字，
     * 所以，在更新联系人名字的时候，也应更新其对应的AgentUser里面的名字
     *
     * @param agentUser
     * @param nickname
     * @return
     */
    private String resolveAgentUsername(final AgentUser agentUser, final String nickname) {
        if (!StringUtils.equals(agentUser.getUsername(), nickname)) {
            return agentUser.getUsername();
        }

        // 查找会话联系人关联表
        AgentUserContacts agentUserContact = agentUserContactsRes.findOneByUseridAndOrgi(
                agentUser.getUserid(), agentUser.getOrgi()).orElse(null);
        if (agentUserContact != null) {
            Contacts contact = contactsRes.findOneById(agentUserContact.getContactsid()).orElseGet(null);
            if (contact != null) {
                return contact.getName();
            }
        }

        return nickname;
    }
}
