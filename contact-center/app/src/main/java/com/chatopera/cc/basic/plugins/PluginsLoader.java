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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 插架装载器
 * TODO Placeholder
 */
public class PluginsLoader {
    private final static Logger logger = LoggerFactory.getLogger(PluginsLoader.class);


    /**
     * 通过插件entry获得PluginName
     *
     * @param pluginEntry
     * @return
     */
    public static String getPluginName(final String pluginEntry) {
        Class<?> clazz;
        try {
            clazz = Class.forName(pluginEntry);
            IPluginDescriptor clazzInst = (IPluginDescriptor) clazz.newInstance();
            Method method = clazz.getMethod("getPluginName");
            return (String) method.invoke(clazzInst);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.info("[postConstruct] error", e);
        }
        return null;
    }



    /**
     * 通过插件entry获得Plugin SocketIO Event Handler
     *
     * @param pluginEntry
     * @return
     */
    public static String getIOEventHandler(final String pluginEntry) {
        Class<?> clazz;
        try {
            clazz = Class.forName(pluginEntry);
            IPluginDescriptor clazzInst = (IPluginDescriptor) clazz.newInstance();
            Method method = clazz.getMethod("getIOEventHandler");
            return (String) method.invoke(clazzInst);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.info("[postConstruct] error", e);
        }
        return null;
    }

}
