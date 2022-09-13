/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.cskefu.cc.plugins.messenger;

import com.cskefu.cc.basic.plugins.AbstractPluginConfigurer;
import com.cskefu.cc.basic.plugins.PluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * 定义Plugin存在
 */
@Configuration
public class MessengerPluginConfigurer extends AbstractPluginConfigurer {
    private final static Logger logger = LoggerFactory.getLogger(MessengerPluginConfigurer.class);
    private final static String pluginName = "Messenger 渠道";
    private final static String pluginId = "messenger";

    @Autowired
    private PluginRegistry pluginRegistry;

    @PostConstruct
    public void setup() {
        pluginRegistry.addPlugin(this);
    }

    @Override
    public String getPluginId() {
        return pluginId;
    }

    /**
     * 获取消息服务的Bean的名字
     * 当该方法存在时，加载到消息处理的调用栈 PeerSyncIM 中
     *
     * @return
     */
    @Override
    public String getPluginName() {
        return pluginName;
    }

    @Override
    public String getIOEventHandler() {
        return null;
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
