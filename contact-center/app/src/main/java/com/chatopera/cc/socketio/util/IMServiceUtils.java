package com.chatopera.cc.socketio.util;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.proxy.AgentUserProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMServiceUtils {
    private final static Logger logger = LoggerFactory.getLogger(IMServiceUtils.class);

    private static Cache cache;
    private static AgentUserProxy agentUserProxy;

    public static void shiftOpsType(final String userId, final String orgi, final MainContext.OptType opsType) {
        getCache().findOneAgentUserByUserIdAndOrgi(userId, orgi).ifPresent(p -> {
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
