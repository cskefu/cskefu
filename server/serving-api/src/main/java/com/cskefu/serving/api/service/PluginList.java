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
