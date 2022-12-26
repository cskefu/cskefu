package com.cskefu.mod.plugin;

import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

@Slf4j
public abstract class AbstractPluginActivator implements BundleActivator {

  public static BundleContext bundleContext;

  @Override
  public final void start(BundleContext context) throws Exception {
    bundleContext = context;
    PluginDescriptor descriptor = registerService();
    bundleContext.registerService(descriptor.getName(), descriptor.getImplementation(), descriptor.getParams());
    log.info("Service registered: " + descriptor.getImplementation().getClass().getName());
  }

  @Override
  public final void stop(BundleContext context) throws Exception {
    bundleContext = null;
    log.info("Stopping plugin " + this.getClass().getName());
  }

  protected abstract PluginDescriptor registerService();
}
