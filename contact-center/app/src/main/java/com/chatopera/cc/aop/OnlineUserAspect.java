package com.chatopera.cc.aop;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.OnlineUser;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OnlineUserAspect {
    private final static Logger logger = LoggerFactory.getLogger(OnlineUserAspect.class);

    @Autowired
    private Cache cache;

    /**
     * 因为会定期从缓存序列化到数据库
     *
     * @param joinPoint
     */
    @Before("execution(* com.chatopera.cc.persistence.repository.OnlineUserRepository.save(..))")
    public void save(final JoinPoint joinPoint) {
        final OnlineUser onlineUser = (OnlineUser) joinPoint.getArgs()[0];
        logger.info(
                "[save] put onlineUser id {}, status {}, invite status {}", onlineUser.getId(), onlineUser.getStatus(),
                onlineUser.getInvitestatus());
        if (StringUtils.isNotBlank(onlineUser.getStatus())) {
            switch (MainContext.OnlineUserStatusEnum.toValue(onlineUser.getStatus())) {
                case OFFLINE:
                    cache.deleteOnlineUserByIdAndOrgi(onlineUser.getId(), onlineUser.getOrgi());
                    break;
                default:
                    cache.putOnlineUserByOrgi(onlineUser, onlineUser.getOrgi());
            }
        }
    }

}
