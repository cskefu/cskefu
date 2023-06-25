/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.basic.plugins;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPluginConfigurer implements IPluginConfigurer {

    public abstract String getPluginId();

    public abstract String getPluginName();

    public abstract String getIOEventHandler();

    public Map<String, String> getEnvironmentVariables() {
        Map<String, String> env = new HashMap<>();
        return env;
    }

    public boolean isModule() {
        return false;
    }

    public abstract void setup();
}
