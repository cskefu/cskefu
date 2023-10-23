/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.config;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.basic.plugins.IPluginConfigurer;
import com.cskefu.cc.basic.plugins.PluginRegistry;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.BlackEntity;
import com.cskefu.cc.model.SysDic;
import com.cskefu.cc.model.SystemConfig;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.LicenseProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.*;

public class AppCtxRefreshEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AppCtxRefreshEventListener.class);

    private void setupSysdicCacheAndExtras(final ContextRefreshedEvent event, final String cacheSetupStrategy, final Cache cache, final SysDicRepository sysDicRes, final BlackListRepository blackListRes) {
        if (!StringUtils.equalsIgnoreCase(cacheSetupStrategy, Constants.cache_setup_strategy_skip)) {

            /**************************
             * 加载系统到缓存
             * 加载系统词典大约只需要5s左右
             **************************/

            // 首先将之前缓存清空，此处使用系统的默认租户信息
            cache.eraseSysDic();

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
                        rootDictItems.put(dic.getDicid(), new ArrayList<>());
                    }
                    rootDictItems.get(dic.getDicid()).add(dic);
                }
            }

            // 更新缓存
            // TODO 集群时注意!!!
            // 此处为长时间的操作，如果在一个集群中，会操作共享内容，非常不可靠
            // 所以，当前代码不支持集群，需要解决启动上的这个问题！

            // 存储根词典 TODO 此处只考虑了系统默认租户
            cache.putSysDic(new ArrayList<>(rootDics.values()));

            for (final Map.Entry<String, List<SysDic>> entry : rootDictItems.entrySet()) {
                SysDic rootDic = rootDics.get(entry.getKey());
                // 打印根词典信息
                logger.debug("[onApplicationEvent] root dict: {}, code {}, name {}, item size {}", entry.getKey(), rootDics.get(entry.getKey()).getCode(), rootDics.get(entry.getKey()).getName(), entry.getValue().size());
                // 存储子项列表
                cache.putSysDic(rootDic.getCode(), entry.getValue());
                // 存储子项成员
                cache.putSysDic(entry.getValue());
            }

            List<BlackEntity> blackList = blackListRes.findAll();
            for (final BlackEntity black : blackList) {
                if (StringUtils.isNotBlank(black.getUserid())) {
                    if (black.getEndtime() == null || black.getEndtime().after(new Date())) {
                        cache.putSystemById(black.getUserid(), black);
                    }
                }
            }

            /**
             * 加载系统全局配置
             */
            SystemConfigRepository systemConfigRes = event.getApplicationContext().getBean(SystemConfigRepository.class);
            List<SystemConfig> configs = systemConfigRes.findAll();
            SystemConfig config = configs.size() > 0 ? configs.get(0) : null;
            if (config != null) {
                cache.putSystemById("systemConfig", config);
            }
            logger.warn("[StartedEventListener] setup Sysdicts in Redis done, strategy {}", cacheSetupStrategy);
        } else {
            logger.warn("[onApplicationEvent] skip initialize sysdicts.");
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (MainContext.getContext() == null) {
            logger.info("[onApplicationEvent] set main context and initialize the Cache System.");
            MainContext.setApplicationContext(event.getApplicationContext());
            SysDicRepository sysDicRes = event.getApplicationContext().getBean(SysDicRepository.class);
            BlackListRepository blackListRes = event.getApplicationContext().getBean(BlackListRepository.class);
            Cache cache = event.getApplicationContext().getBean(Cache.class);
            String cacheSetupStrategy = event.getApplicationContext().getEnvironment().getProperty("cache.setup.strategy");

            setupSysdicCacheAndExtras(event, cacheSetupStrategy, cache, sysDicRes, blackListRes);

            MainUtils.initSystemArea();

            MainUtils.initSystemSecField(event.getApplicationContext().getBean(TablePropertiesRepository.class));
            // MainUtils.initAdv();//初始化广告位

            // 初始化插件
            PluginRegistry pluginRegistry = MainContext.getContext().getBean(PluginRegistry.class);
            for (final IPluginConfigurer p : pluginRegistry.getPlugins()) {
                logger.info("[Plugins] registered plugin id {}, class {}", p.getPluginId(), p.getClass().getName());
            }

            // 初始化 ServerInstId
            LicenseProxy licenseProxy = event.getApplicationContext().getBean(LicenseProxy.class);
            licenseProxy.checkOnStartup();
        } else {
            logger.info("[onApplicationEvent] bypass, initialization has been done already.");
        }

        // Fix SQL init lazy load delay
        if (MainContext.getContext() != null) {
            UserRepository userRes = MainContext.getContext().getBean(UserRepository.class);
            userRes.findByUsername("admin").ifPresent((p) -> {
                logger.warn("[onApplicationEvent] inited JPA sql.");
            });
        }
    }
}