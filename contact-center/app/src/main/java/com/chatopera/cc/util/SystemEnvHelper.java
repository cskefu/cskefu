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
package com.chatopera.cc.util;

import org.apache.commons.lang.StringUtils;

public class SystemEnvHelper {

    /**
     * 分析是否加载模块，在变量为不存在或变量值为true的情况下加载
     * 也就是说，该变量值不为空或为true时时加载
     * @param environmentVariable
     * @return
     */
    public static boolean parseModuleFlag(final String environmentVariable){
        String val = System.getenv(environmentVariable);
        return StringUtils.isBlank(val) || StringUtils.equalsIgnoreCase(val, "true");
    }
}
