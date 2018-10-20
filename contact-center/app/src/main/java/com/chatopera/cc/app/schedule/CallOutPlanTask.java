/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app.schedule;


import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Constants;
import com.chatopera.cc.exception.CallOutRuntimeException;
import com.chatopera.cc.app.persistence.repository.CallOutDialplanRepository;
import com.chatopera.cc.app.persistence.repository.CallOutTargetRepository;
import com.chatopera.cc.app.model.CallOutDialplan;
import com.chatopera.cc.app.model.CallOutTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * 外呼系统计划任务处理
 */

@Component
@EnableScheduling
public class CallOutPlanTask {
    private final static Logger logger = LoggerFactory.getLogger(CallOutPlanTask.class);


    private ListOperations<String, String> redisListOps;
    private HashOperations<String, String, String> redisHashOps;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.database}")
    private String redisDB;


    @Autowired
    private CallOutDialplanRepository callOutDialplanRes;

    @Autowired
    private CallOutTargetRepository callOutTargetRes;


    /**
     * 使用StringRedisTemplate而不是RedisTemplate解决序列化问题
     * https://stackoverflow.com/questions/13215024/weird-redis-key-with-spring-data-jedis
     */
    @Autowired
    private StringRedisTemplate redis;

    @PostConstruct
    private void init() {
        redisListOps = redis.opsForList();
        redisHashOps = redis.opsForHash();
    }



    @Scheduled(fixedDelayString = "${cskefu.callout.watch.interval}") // 每分钟执行一次
    public void watch() {
        if(MainContext.isEnableCalloutModule()){
            logger.debug("[callout executor] check dialplan job running status ...");
            // load all jobs
            List<CallOutDialplan> dps = callOutDialplanRes.findByStatusAndIsarchive(MainContext.CallOutDialplanStatusEnum.RUNNING.toString(), false);
            for (CallOutDialplan dp : dps) {
                Long size = redisListOps.size(String.format(Constants.FS_DIALPLAN_TARGET, dp.getVoicechannel().getBaseURL(), dp.getId()));
                if (size > 0) {
                    logger.info("[callout executor] job [{}] is not done yet, remaining [{}]", dp.getName(), size);
                } else {
                    dp.setStatus(MainContext.CallOutDialplanStatusEnum.STOPPED.toString());
                    dp.setUpdatetime(new Date());
                    callOutDialplanRes.save(dp);

                    // 删除状态成员
                    delHashKey(String.format(Constants.FS_DIALPLAN_STATUS, dp.getVoicechannel().getBaseURL()), dp.getId());
                }
            }
        }
    }

    @Async("callOutTaskExecutor")
    @Transactional
    public void run(final CallOutDialplan dp, final JsonArray sips) throws CallOutRuntimeException {
        logger.info("[callout executor] dialplan name {}, id {}, concurrency {}", dp.getName(), dp.getId(), dp.getCurconcurrence());
        final String dialplanId = dp.getId();
        final String dialplanVoiceChannelIdef = dp.getVoicechannel().getBaseURL();
        final int curconcurrence = dp.getCurconcurrence();
        final String key = String.format(Constants.FS_DIALPLAN_TARGET, dialplanVoiceChannelIdef, dialplanId);

        if(sips.size() == 0)
            throw new CallOutRuntimeException("SIP话机列表没有成员。");

        try (Stream<CallOutTarget> stream = callOutTargetRes.findAllByOrgiAndInvalidAndDialplan(MainContext.SYSTEM_ORGI, false, dialplanId)) {
            // forEach是并行执行，无法使用上层变量，除非是final
            stream.forEach(target -> {
                logger.info("[callout executor] target phone {}, redis {}:{} database {}", target.getPhone(), redisHost, redisPort, redisDB);
                JsonObject payload = new JsonObject();
                payload.addProperty("to", target.getPhone());
                payload.addProperty("channel", dialplanVoiceChannelIdef);
                payload.addProperty("type", Constants.FS_CALL_TYPE_CALLOUT);
                redisListOps.leftPush(key, payload.toString());
            });

            JsonObject payload2 = new JsonObject();
            payload2.addProperty("concurrency", curconcurrence);
            payload2.addProperty("status", MainContext.CallOutDialplanStatusEnum.RUNNING.toString());
            payload2.addProperty("channel", dialplanVoiceChannelIdef);
            payload2.addProperty("updatetime", (new Date()).toString());
            payload2.add("sips", sips);
            setHashKeyValue(String.format(Constants.FS_DIALPLAN_STATUS, dialplanVoiceChannelIdef), dialplanId, payload2.toString());

            // 所有目标计划推到LIST中，再发送信号
            JsonObject payload = new JsonObject();
            payload.addProperty("dialplan", dialplanId);
            payload.addProperty("concurrency", curconcurrence);
            payload.addProperty("ops", "start");
            payload.addProperty("channel", dialplanVoiceChannelIdef);
            payload.add("sips", sips);
            publish(String.format(Constants.FS_CHANNEL_CC_TO_FS, dp.getVoicechannel().getBaseURL()), payload.toString());
        }
    }

    /**
     * Delete List by Key
     *
     * @param key
     */
    public void delKey(final String key) {
        redis.delete(key);
    }


    public void delHashKey(final String hash, final String key) {
        logger.info("[callout executor] delete hash [{}] key [{}]", hash, key);
        redisHashOps.delete(hash, key);
    }

    /**
     * Set Hash Key Value
     *
     * @param hash
     * @param key
     * @param value
     */
    public void setHashKeyValue(final String hash, final String key, final String value) {
        logger.info("[callout executor] set hash {} key {} value {}", hash, key, value);
        redisHashOps.put(hash, key, value);
    }

    /**
     * Publish Message into Channel with redis PubSub
     *
     * @param channel
     * @param payload
     */
    public void publish(final String channel, final String payload) {
        ChannelTopic ct = new ChannelTopic(channel);
        redis.convertAndSend(ct.getTopic(), payload);
    }

}
