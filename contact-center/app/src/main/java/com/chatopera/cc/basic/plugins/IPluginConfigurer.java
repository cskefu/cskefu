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

import java.util.Map;

public interface IPluginConfigurer {
    // 插件的ID:插件的标识，用于区别其它插件，由[a-z]组成，最大32位长度
    String getPluginId();

    // 插件的名字:最少的概述插件
    String getPluginName();

    // 即时通信接口
    String getIOEventHandler();

    // 获得环境变量及默认值
    Map<String, String> getEnvironmentVariables();

    // 是否是Module(在一级菜单有入口的插件)
    boolean isModule();

    // 安装插件
    public void setup();
}
