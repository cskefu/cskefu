package com.chatopera.cc.app.im.util;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.persistence.impl.AgentUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMServiceUtils {
    private final static Logger logger = LoggerFactory.getLogger(IMServiceUtils.class);

    public static void shiftOpsType(final String userId, final String orgi, final MainContext.OptTypeEnum opsType){
        AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userId, orgi);
        AgentUserService service = MainContext.getContext().getBean(
                AgentUserService.class);
        if (agentUser == null) {
            agentUser = service.findByUseridAndOrgi(userId, orgi);
        }
        if (agentUser != null) {
            switch (opsType){
                case CHATBOT:
                    agentUser.setOpttype(MainContext.OptTypeEnum.CHATBOT.toString());
                    agentUser.setChatbotops(true);
                    break;
                case HUMAN:
                    agentUser.setOpttype(MainContext.OptTypeEnum.HUMAN.toString());
                    agentUser.setChatbotops(false);
                    break;
                default:
                    logger.warn("shiftOpsType unknown type.");
                    break;
            }
        }
        service.save(agentUser);
        CacheHelper.getAgentUserCacheBean().put(agentUser.getUserid(), agentUser, orgi);
    }
}
