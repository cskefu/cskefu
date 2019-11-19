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

package com.chatopera.cc.acd.visitor;

import com.chatopera.cc.acd.ACDComposeContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.model.AgentUserContacts;
import com.chatopera.cc.model.Contacts;
import com.chatopera.cc.persistence.es.ContactsRepository;
import com.chatopera.cc.persistence.repository.AgentUserContactsRepository;
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
