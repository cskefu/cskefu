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
package com.chatopera.cc.basic.plugins;

import com.chatopera.cc.basic.MainContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 插件注册表
 */
@Component
public class PluginRegistry {
    /**
     * Plugins Entry
     */
    public final static String PLUGIN_CHANNEL_MESSAGER_SUFFIX = "ChannelMessager";

    // 插件列表
    private final List<IPluginConfigurer> plugins = new ArrayList<>();

    /**
     * 添加插件
     *
     * @param plugin
     * @return
     */
    public boolean addPlugin(final IPluginConfigurer plugin) {
        for (final IPluginConfigurer x : plugins) {
            if (StringUtils.equalsIgnoreCase(x.getPluginId(), plugin.getPluginId())) {
                return false;
            }
        }

        if (StringUtils.isNotBlank(plugin.getPluginId())) {
            MainContext.enableModule(plugin.getPluginId());
        }

        plugins.add(plugin);
        return true;
    }

    /**
     * 获得插件列表
     *
     * @return
     */
    public List<IPluginConfigurer> getPlugins() {
        return plugins;
    }

    /**
     * 获得一个插件
     *
     * @param pluginId
     * @return
     */
    public Optional<IPluginConfigurer> getPlugin(final String pluginId) {
        IPluginConfigurer p = null;
        for (final IPluginConfigurer plugin : plugins) {
            if (StringUtils.equalsIgnoreCase(plugin.getPluginId(), pluginId)) {
                p = plugin;
                break;
            }
        }
        return Optional.ofNullable(p);
    }

    /**
     * 删除插件
     *
     * @param pluginId
     */
    public void removePlugin(final String pluginId) {
        for (final IPluginConfigurer plugin : plugins) {
            if (StringUtils.equalsIgnoreCase(plugin.getPluginId(), pluginId)) {
                plugins.remove(plugin);
                break;
            }
        }
    }
}
