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

import com.cskefu.cc.acd.basic.ACDComposeContext;
import com.cskefu.cc.acd.basic.ACDMessageHelper;
import com.cskefu.cc.acd.basic.IACDDispatcher;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.cache.RedisCommand;
import com.cskefu.cc.cache.RedisKey;
import com.cskefu.cc.model.AgentStatus;
import com.cskefu.cc.model.AgentUser;
import com.cskefu.cc.persistence.repository.AgentStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ACDAgentDispatcher implements IACDDispatcher {
    private final static Logger logger = LoggerFactory.getLogger(ACDAgentDispatcher.class);

    @Autowired
    private Cache cache;

    @Autowired
    private AgentStatusRepository agentStatusRes;

    @Autowired
    private RedisCommand redisCommand;

    @Autowired
    private ACDVisitorDispatcher acdVisitorDispatcher;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @Override
    public void enqueue(ACDComposeContext ctx) {

    }

    /**
     * 撤退一个坐席
     * 1）将该坐席状态置为"非就绪"
     * 2) 将该坐席的访客重新分配给其它坐席
     *
     * @param ctx agentno为必填
     * @return 有没有成功将所有其服务的访客都分配出去
     */
    @Override
    public void dequeue(final ACDComposeContext ctx) {
        // 先将该客服切换到非就绪状态
        final AgentStatus agentStatus = cache.findOneAgentStatusByAgentno(ctx.getAgentno());
        if (agentStatus != null) {
            agentStatus.setBusy(false);
            agentStatus.setUpdatetime(new Date());
            agentStatus.setStatus(MainContext.AgentStatusEnum.NOTREADY.toString());
            agentStatusRes.save(agentStatus);
            cache.putAgentStatus(agentStatus);
        }

        // 然后将该坐席的访客分配给其它坐席
        // 获得该租户在线的客服的多少
        // TODO 对于agentUser的技能组过滤，在下面再逐个考虑？
        // 该信息同样也包括当前用户
        List<AgentUser> agentUsers = cache.findInservAgentUsersByAgentno(ctx.getAgentno());
        int sz = agentUsers.size();
        for (final AgentUser x : agentUsers) {
            try {
                // TODO 此处没有考虑遍历过程中，系统中坐席的服务访客的信息实际上是变化的
                // 可能会发生maxusers超过设置的情况，如果做很多检查，会带来一定一系统开销
                // 因为影响不大，放弃实时的检查
                ACDComposeContext y = acdMessageHelper.getComposeContextWithAgentUser(
                        x, false, MainContext.ChatInitiatorType.USER.toString());
                acdVisitorDispatcher.enqueue(y);

                // 因为重新分配该访客，将其从撤离的坐席中服务集合中删除
                // 此处类似于 Transfer
                redisCommand.removeSetVal(
                        RedisKey.getInServAgentUsersByAgentno(ctx.getAgentno()), x.getUserid());
                sz--;
            } catch (Exception e) {
                logger.warn("[dequeue] throw error:", e);
            }
        }

        if (sz == 0) {
            logger.info(
                    "[dequeue] after re-allotAgent, the agentUsers size is {} for agentno {}", sz,
                    ctx.getAgentno());
        } else {
            logger.warn(
                    "[dequeue] after re-allotAgent, the agentUsers size is {} for agentno {}", sz,
                    ctx.getAgentno());
        }

        ctx.setResolved(sz == 0);
    }
}
