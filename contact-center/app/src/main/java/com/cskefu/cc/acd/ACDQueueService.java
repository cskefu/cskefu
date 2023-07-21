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

package com.cskefu.cc.acd;

import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.AgentUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ACDQueueService {
    private final static Logger logger = LoggerFactory.getLogger(ACDQueueService.class);


    @Autowired
    private Cache cache;

    @SuppressWarnings("unchecked")
    public int getQueueIndex(String agent, String skill) {
        int queneUsers = 0;
        Map<String, AgentUser> map = cache.getAgentUsersInQue();

        for (final Map.Entry<String, AgentUser> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(skill)) {
                if (StringUtils.equals(entry.getValue().getSkill(), skill)) {
                    queneUsers++;
                }
                continue;
            } else {
                if (StringUtils.isNotBlank(agent)) {
                    if (StringUtils.equals(entry.getValue().getAgentno(), agent)) {
                        queneUsers++;
                    }
                    continue;
                } else {
                    queneUsers++;
                }
            }
        }
        return queneUsers;
    }
}
