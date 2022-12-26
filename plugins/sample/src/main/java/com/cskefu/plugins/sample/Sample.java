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
package com.cskefu.plugins.sample;

import com.cskefu.mod.plugin.IPlugin;
import com.cskefu.mod.plugin.dto.ActionResponse;
import com.cskefu.mod.plugin.dto.NotificationResponse;

public class Sample implements IPlugin {
    @Override
    public ActionResponse doAction() {
        return ActionResponse.builder().body("Action Worked !!").build();
    }

    @Override
    public NotificationResponse doNotification() {
        return NotificationResponse.builder().body("Notification Processed...").build();
    }

}
