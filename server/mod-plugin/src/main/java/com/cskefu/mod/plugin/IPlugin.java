package com.cskefu.mod.plugin;

import com.cskefu.mod.plugin.dto.ActionResponse;
import com.cskefu.mod.plugin.dto.NotificationResponse;

public interface IPlugin {

  ActionResponse doAction();

  NotificationResponse doNotification();
}
