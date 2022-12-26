/*
 * Copyright (C) 2018-2023 北京华夏春松科技有限公司, <https://www.chatopera.com>
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
package com.cskefu.serving.api.service;

import com.cskefu.mod.plugin.IPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PluginList {
  private Map<String, IPlugin> plugins = new HashMap<>();

  public Set<String> registered() {
    return plugins.keySet();
  }

  public Optional<IPlugin> lookup(String name) {
    return Optional.ofNullable(plugins.get(name));
  }

  public void register(String name, IPlugin service) {
    plugins.put(name, service);
  }

  public void unregister(String name) {
    plugins.remove(name);
  }

}
