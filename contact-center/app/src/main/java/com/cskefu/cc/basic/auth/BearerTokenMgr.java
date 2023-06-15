/*
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.basic.auth;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.cache.RedisKey;
import com.cskefu.cc.model.User;
import com.cskefu.cc.util.SerializeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 认证和授权的API Token管理
 */
@Component
public class BearerTokenMgr {

    private final static Logger logger = LoggerFactory.getLogger(BearerTokenMgr.class);

    @Value("${server.session-timeout}")
    private int timeout;

    @Autowired
    private AuthRedisTemplate authRedisTemplate;

    private ValueOperations<String, String> redisValOps;

    @PostConstruct
    private void init() {
        redisValOps = authRedisTemplate.opsForValue();
    }

    /**
     * Remove token with Bearer prefix
     *
     * @param token
     * @return
     */
    private String trimToken(final String token) {
        if (token.startsWith(Constants.AUTH_TOKEN_TYPE_BEARER)) {
            return StringUtils.substring(token, 7);
        }
        return token;
    }

    /**
     * 设置一个KEY的过期时间
     *
     * @param key
     * @param seconds
     */
    private void expire(final String key, final long seconds) {
        authRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    private String resolveTokenKey(final String token) {
        return RedisKey.getApiTokenBearerKeyWithValue(trimToken(token));
    }

    /**********************************
     *  LOGIN USER API TOKEN 相关
     *  认证，授权，登录用户
     **********************************/

    /**
     * @param token 授权的KEY
     * @param user  已经登录的用户
     */
    public void update(final String token, final User user) {
        if (StringUtils.isNotBlank(token) && user != null) {
            String serialized = SerializeUtil.serialize(user);
            final String key = resolveTokenKey(token);
            redisValOps.set(key, serialized);
            expire(key, timeout);
        } else {
            logger.warn("[putLoginUserByAuth] error Invalid params.");
        }
    }


    /**
     * 判断一个Auth是否是有效的
     *
     * @param token
     * @return
     */
    public boolean existToken(final String token) {
        return authRedisTemplate.hasKey(resolveTokenKey(token));
    }

    /**
     * 根据租户ID和认证Auth获得一个登录用户
     *
     * @param token
     * @return
     */
    public User retrieve(final String token) {
        String serialized = redisValOps.get(resolveTokenKey(token));
        if (StringUtils.isNotBlank(serialized)) {
            return (User) SerializeUtil.deserialize(serialized);
        }
        return null;
    }

    /**
     * 登出已经登录的系统用户
     *
     * @param token
     */
    public void delete(final String token) {
        authRedisTemplate.delete(resolveTokenKey(token));
    }
}
