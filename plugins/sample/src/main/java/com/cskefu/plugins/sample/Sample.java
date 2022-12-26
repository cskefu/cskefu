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
