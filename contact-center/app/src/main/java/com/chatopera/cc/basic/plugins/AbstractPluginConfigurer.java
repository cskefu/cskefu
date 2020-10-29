package com.chatopera.cc.basic.plugins;

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
