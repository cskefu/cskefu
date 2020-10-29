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

package com.chatopera.cc.proxy;

import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.AgentService;
import com.chatopera.cc.model.AgentUser;
import com.chatopera.cc.model.BlackEntity;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.AgentServiceRepository;
import com.chatopera.cc.persistence.repository.AgentUserRepository;
import com.chatopera.cc.persistence.repository.BlackListRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class BlackEntityProxy {

    @Autowired
    private BlackListRepository blackListRes;

    @Autowired
    private Cache cache;

    @Autowired
    private AgentUserRepository agentUserRepository;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    /**
     * 更新或创建黑名单记录
     *
     * @param pre
     * @param owner
     * @param userid
     * @param orgi
     * @param agentserviceid
     * @param agentuserid
     * @return
     */
    public BlackEntity updateOrCreateBlackEntity(
            final BlackEntity pre,
            final User owner,
            final String userid,
            final String orgi,
            final String agentserviceid,
            final String agentuserid) {
        final BlackEntity blackEntityUpdated = cache.findOneBlackEntityByUserIdAndOrgi(
                userid, orgi).orElseGet(
                () -> {
                    BlackEntity p = new BlackEntity();
                    p.setUserid(userid);
                    p.setOrgi(orgi);
                    p.setCreater(owner.getId());
                    return p;
                });

        blackEntityUpdated.setAgentid(owner.getId());
        blackEntityUpdated.setAgentserviceid(agentserviceid);
        if (agentserviceid != null){
            AgentService service = agentServiceRes.findByIdAndOrgi(agentserviceid, orgi);
            blackEntityUpdated.setSkill(service.getSkill());
            blackEntityUpdated.setAgentusername(service.getAgentusername());
        }
        blackEntityUpdated.setControltime(pre.getControltime());

        if (StringUtils.isNotBlank(pre.getDescription())) {
            blackEntityUpdated.setDescription(pre.getDescription());
        }

        if (blackEntityUpdated.getControltime() > 0) {
            blackEntityUpdated.setEndtime(
                    new Date(System.currentTimeMillis() + pre.getControltime() * 3600 * 1000L));
        }

        AgentService agentService = agentServiceRes.findByIdAndOrgi(agentserviceid, orgi);
        if (agentService != null) {
            blackEntityUpdated.setChannel(agentService.getChannel());
            blackEntityUpdated.setAgentuser(agentService.getUsername());
            blackEntityUpdated.setSessionid(agentService.getSessionid());
            if (agentService.getSessiontimes() != 0) {
                blackEntityUpdated.setChattime((int) agentService.getSessiontimes());
            } else {
                blackEntityUpdated.setChattime((int) (System.currentTimeMillis() - agentService.getServicetime().getTime()));
            }
        }

        blackListRes.save(blackEntityUpdated);

        return blackEntityUpdated;
    }
}
