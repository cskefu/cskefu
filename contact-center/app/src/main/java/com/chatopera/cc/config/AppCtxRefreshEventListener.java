/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.config;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.BlackEntity;
import com.chatopera.cc.model.SysDic;
import com.chatopera.cc.model.SystemConfig;
import com.chatopera.cc.persistence.repository.BlackListRepository;
import com.chatopera.cc.persistence.repository.SysDicRepository;
import com.chatopera.cc.persistence.repository.SystemConfigRepository;
import com.chatopera.cc.persistence.repository.TablePropertiesRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;

public class AppCtxRefreshEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AppCtxRefreshEventListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (MainContext.getContext() == null) {
            logger.info("[onApplicationEvent] set main context and initialize the Cache System.");
            MainContext.setApplicationContext(event.getApplicationContext());
            SysDicRepository sysDicRes = event.getApplicationContext().getBean(SysDicRepository.class);
            BlackListRepository blackListRes = event.getApplicationContext().getBean(BlackListRepository.class);
            Cache cache = event.getApplicationContext().getBean(Cache.class);
            String cacheSetupStrategy = event.getApplicationContext().getEnvironment().getProperty("cache.setup.strategy");


            if (!StringUtils.equalsIgnoreCase(cacheSetupStrategy, Constants.cache_setup_strategy_skip)) {

                /**************************
                 * 加载系统到缓存
                 * 加载系统词典大约只需要5s左右
                 **************************/

                // 首先将之前缓存清空，此处使用系统的默认租户信息
                cache.eraseSysDicByOrgi(MainContext.SYSTEM_ORGI);

                List<SysDic> sysDicList = sysDicRes.findAll();
                Map<String, List<SysDic>> rootDictItems = new HashMap<>(); // 关联根词典及其子项
                Map<String, SysDic> rootDics = new HashMap<>();
                Set<String> parents = new HashSet<>();

                // 获得所有根词典
                for (final SysDic dic : sysDicList) {
                    if (StringUtils.equals(dic.getParentid(), "0")) {
                        parents.add(dic.getId());
                        rootDics.put(dic.getId(), dic);
                    }
                }

                // 向根词典中添加子项
                for (final SysDic dic : sysDicList) {
                    if ((!StringUtils.equals(dic.getParentid(), "0")) &&
                            parents.contains(dic.getDicid())) {
                        // 不是根词典，并且包含在一个根词典内
                        if (!rootDictItems.containsKey(dic.getDicid())) {
                            rootDictItems.put(dic.getDicid(), new ArrayList<SysDic>());
                        }
                        rootDictItems.get(dic.getDicid()).add(dic);
                    }
                }

                // 更新缓存
                // TODO 集群时注意!!!
                // 此处为长时间的操作，如果在一个集群中，会操作共享内容，非常不可靠
                // 所以，当前代码不支持集群，需要解决启动上的这个问题！

                // 存储根词典 TODO 此处只考虑了系统默认租户
                cache.putSysDicByOrgi(new ArrayList<>(rootDics.values()), MainContext.SYSTEM_ORGI);

                for (final Map.Entry<String, List<SysDic>> entry : rootDictItems.entrySet()) {
                    SysDic rootDic = rootDics.get(entry.getKey());
                    // 打印根词典信息
                    logger.debug("[onApplicationEvent] root dict: {}, code {}, name {}, item size {}", entry.getKey(), rootDics.get(entry.getKey()).getCode(), rootDics.get(entry.getKey()).getName(), entry.getValue().size());
                    // 存储子项列表
                    cache.putSysDicByOrgi(rootDic.getCode(), MainContext.SYSTEM_ORGI, entry.getValue());
                    // 存储子项成员
                    cache.putSysDicByOrgi(entry.getValue(), MainContext.SYSTEM_ORGI);
                }

                List<BlackEntity> blackList = blackListRes.findByOrgi(MainContext.SYSTEM_ORGI);
                for (final BlackEntity black : blackList) {
                    if (StringUtils.isNotBlank(black.getUserid())) {
                        if (black.getEndtime() == null || black.getEndtime().after(new Date())) {
                            cache.putSystemByIdAndOrgi(black.getUserid(), black.getOrgi(), black);
                        }
                    }
                }

                /**
                 * 加载系统全局配置
                 */
                SystemConfigRepository systemConfigRes = event.getApplicationContext().getBean(SystemConfigRepository.class);
                SystemConfig config = systemConfigRes.findByOrgi(MainContext.SYSTEM_ORGI);
                if (config != null) {
                    cache.putSystemByIdAndOrgi("systemConfig", MainContext.SYSTEM_ORGI, config);
                }
                logger.info("[StartedEventListener] setup Sysdicts in Redis done, strategy {}", cacheSetupStrategy);
            } else {
                logger.info("[onApplicationEvent] skip initialize sysdicts.");
            }

            MainUtils.initSystemArea();

            MainUtils.initSystemSecField(event.getApplicationContext().getBean(TablePropertiesRepository.class));
            // MainUtils.initAdv();//初始化广告位
        } else {
            logger.info("[onApplicationEvent] bypass, initialization has been done already.");
        }
    }
}