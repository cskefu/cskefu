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

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAwareBundleListener implements BundleListener {

  private final PluginList pluginList;

  @Override
  public void bundleChanged(BundleEvent bundleEvent) {

    log.info(
        String.format(
            "Bundle %s:%s changed state to %s - Type: %s",
            bundleEvent.getBundle().getSymbolicName(),
            bundleEvent.getBundle().getVersion(),
            getBundleStateAsString(bundleEvent.getBundle().getState()),
            getBundleStateAsString(bundleEvent.getType())
        )
    );

    if (bundleEvent.getBundle().getState() == Bundle.ACTIVE) {
      try {
        Bundle bundle = bundleEvent.getBundle();
        BundleContext bundleContext = bundle.getBundleContext();
        ServiceReference<?>[] services = bundleContext.getAllServiceReferences(IPlugin.class.getName(), null);
        if (services != null && services.length > 0)
          Arrays.asList(services)
              .forEach(s -> pluginList.register(s.getBundle().getSymbolicName(), (IPlugin) bundleContext.getService(s)));

      } catch (InvalidSyntaxException e) {
        log.warn("Problem reading services from BundleContext");
      }

    } else if (bundleEvent.getBundle().getState() == Bundle.UNINSTALLED) {
      pluginList.unregister(bundleEvent.getBundle().getSymbolicName());
    }

  }

  private String getBundleStateAsString(int state) {
    return switch (state) {
      case Bundle.ACTIVE -> "Active";
      case Bundle.INSTALLED -> "Installed";
      case Bundle.RESOLVED -> "Resolved";
      case Bundle.STARTING -> "Starting";
      case Bundle.STOPPING -> "Stopping";
      case Bundle.UNINSTALLED -> "Uninstalled";
      default -> "Unknown";
    };

  }
}
