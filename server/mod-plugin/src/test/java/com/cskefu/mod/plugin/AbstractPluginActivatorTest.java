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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cskefu.mod.plugin.dto.ActionResponse;
import com.cskefu.mod.plugin.dto.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

@ExtendWith(MockitoExtension.class)
class AbstractPluginActivatorTest {

  private AbstractPluginActivator cut;
  @Mock
  private BundleContext bundleContext;


  @BeforeEach
  void setUp() {
    cut = new TestActivator();
  }

  @Test
  @DisplayName("When start method invoked, then the plugin service is registered into the bundle context")
  void testStart() throws Exception {
    cut.start(bundleContext);
    verify(bundleContext, times(1)).registerService(eq("com.mornati.sample.commons.plugins.IPlugin"), any(SampleService.class), any());
  }

  public class TestActivator extends AbstractPluginActivator {

    @Override
    protected PluginDescriptor registerService() {
      return PluginDescriptor.builder().implementation(new SampleService()).name(IPlugin.class.getName()).build();
    }
  }

  public class SampleService implements IPlugin {

    @Override
    public ActionResponse doAction() {
      return ActionResponse.builder().body("sample action").build();
    }

    @Override
    public NotificationResponse doNotification() {
      return NotificationResponse.builder().body("sample notification").build();
    }
  }
}
