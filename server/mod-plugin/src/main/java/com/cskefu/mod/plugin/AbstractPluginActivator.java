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
