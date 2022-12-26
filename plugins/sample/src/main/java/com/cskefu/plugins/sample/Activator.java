package com.cskefu.plugins.sample;

import com.cskefu.mod.plugin.AbstractPluginActivator;
import com.cskefu.mod.plugin.IPlugin;
import com.cskefu.mod.plugin.PluginDescriptor;

import java.util.Hashtable;

public class Activator extends AbstractPluginActivator {

    @Override
    protected PluginDescriptor registerService() {
        Hashtable<String, Object> props = new Hashtable<>();
        props.put("Plugin-Name", "SamplePlugin");
        return PluginDescriptor.builder()
                .implementation(new Sample())
                .name(IPlugin.class.getName())
                .params(props)
                .build();
    }
}
