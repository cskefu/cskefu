/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.socketio.util;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.proxy.AgentUserProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMServiceUtils {
    private final static Logger logger = LoggerFactory.getLogger(IMServiceUtils.class);

    private static Cache cache;
    private static AgentUserProxy agentUserProxy;

    public static void shiftOpsType(final String userId, final MainContext.OptType opsType) {
        getCache().findOneAgentUserByUserId(userId).ifPresent(p -> {
            switch (opsType) {
                case CHATBOT:
                    p.setOpttype(MainContext.OptType.CHATBOT.toString());
                    p.setChatbotops(true);
                    break;
                case HUMAN:
                    p.setOpttype(MainContext.OptType.HUMAN.toString());
                    p.setChatbotops(false);
                    break;
                default:
                    logger.warn("shiftOpsType unknown type.");
                    break;
            }
            getAgentUserProxy().save(p);
        });
    }

    /**
     * Lazy load cache mgr
     *
     * @return
     */
    static private Cache getCache() {
        if (cache == null) {
            cache = MainContext.getContext().getBean(Cache.class);
        }
        return cache;
    }

    private static AgentUserProxy getAgentUserProxy() {
        if (agentUserProxy == null) {
            agentUserProxy = MainContext.getContext().getBean(
                    AgentUserProxy.class);
        }
        return agentUserProxy;
    }


}
