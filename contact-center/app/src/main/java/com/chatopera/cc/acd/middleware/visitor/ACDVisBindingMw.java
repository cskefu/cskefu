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

import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.model.Organ;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.compose4j.Functional;
import com.chatopera.compose4j.Middleware;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ACDVisBindingMw implements Middleware<ACDComposeContext> {

    private final static Logger logger = LoggerFactory.getLogger(ACDVisBindingMw.class);

    @Autowired
    private UserRepository userRes;

    @Autowired
    private OrganRepository organRes;

    /**
     * 绑定技能组或坐席
     *
     * @param ctx
     * @param next
     */
    @Override
    public void apply(final ACDComposeContext ctx, final Functional next) {
        /**
         * 访客新上线的请求
         */
        /**
         * 技能组 和 坐席
         */
        if (StringUtils.isNotBlank(ctx.getOrganid())) {
            logger.info("[apply] bind skill {}", ctx.getOrganid());
            // 绑定技能组
            Organ organ = organRes.findOne(ctx.getOrganid());
            if (organ != null) {
                ctx.getAgentUser().setSkill(organ.getId());
                ctx.setOrgan(organ);
            }
        } else {
            // 如果没有绑定技能组，则清除之前的标记
            ctx.getAgentUser().setSkill(null);
        }

        if (StringUtils.isNotBlank(ctx.getAgentno()) && (!StringUtils.equalsIgnoreCase(ctx.getAgentno(), "null"))) {
            logger.info("[apply] bind agentno {}, isInvite {}", ctx.getAgentno(), ctx.isInvite());
            // 绑定坐席
            // 绑定坐席有可能是因为前端展示了技能组和坐席
            // 也有可能是坐席发送了邀请，该访客接收邀请
            ctx.getAgentUser().setAgentno(ctx.getAgentno());
            User agent = userRes.findOne(ctx.getAgentno());
            ctx.setAgent(agent);
            ctx.getAgentUser().setAgentname(agent.getUname());
        } else {
            // 如果没有绑定坐席，则清除之前的标记
            ctx.getAgentUser().setAgentno(null);
            ctx.getAgentUser().setAgentname(null);
            ctx.setAgent(null);
        }

        next.apply();
    }
}
