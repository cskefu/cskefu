/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.cskefu.cc.proxy;

import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.model.BlackEntity;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.AgentServiceRepository;
import com.cskefu.cc.persistence.repository.BlackListRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BlackEntityProxy {

    @Autowired
    private BlackListRepository blackListRes;

    @Autowired
    private Cache cache;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    /**
     * 更新或创建黑名单记录
     *
     * @param pre
     * @param owner
     * @param userid
     * @param agentserviceid
     * @param agentuserid
     */
    public void updateOrCreateBlackEntity(
            final BlackEntity pre,
            final User owner,
            final String userid,
            final String agentserviceid,
            final String agentuserid) {
        final BlackEntity blackEntityUpdated = cache.findOneBlackEntityByUserId(
                userid).orElseGet(
                () -> {
                    BlackEntity p = new BlackEntity();
                    p.setUserid(userid);
                    p.setCreater(owner.getId());
                    return p;
                });

        blackEntityUpdated.setAgentid(owner.getId());
        blackEntityUpdated.setAgentserviceid(agentserviceid);
        if (agentserviceid != null){
            AgentService service = agentServiceRes.findById(agentserviceid).orElse(null);
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

        AgentService agentService = agentServiceRes.findById(agentserviceid).orElse(null);
        if (agentService != null) {
            blackEntityUpdated.setChannel(agentService.getChanneltype());
            blackEntityUpdated.setAgentuser(agentService.getUsername());
            blackEntityUpdated.setSessionid(agentService.getSessionid());
            if (agentService.getSessiontimes() != 0) {
                blackEntityUpdated.setChattime((int) agentService.getSessiontimes());
            } else {
                blackEntityUpdated.setChattime((int) (System.currentTimeMillis() - agentService.getServicetime().getTime()));
            }
        }

        blackListRes.save(blackEntityUpdated);

    }
}
