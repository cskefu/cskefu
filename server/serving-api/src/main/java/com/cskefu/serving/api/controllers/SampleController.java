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
package com.cskefu.serving.api.controllers;

import com.cskefu.mod.plugin.dto.ActionResponse;
import com.cskefu.mod.plugin.dto.NotificationResponse;
import com.cskefu.serving.api.service.PluginList;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plugins")
@RequiredArgsConstructor
public class SampleController {

  private final PluginList pluginList;

  @GetMapping
  @ResponseBody
  public ResponseEntity<Set<String>> getSamples() {
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
        .body(pluginList.registered());

  }

  @GetMapping(value = "/{name}")
  @ResponseBody
  public ResponseEntity<ActionResponse> doAction(@PathVariable(value = "name") String name) {
    return pluginList.lookup(name).map(pluginImpl -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
        .body(pluginImpl.doAction())).orElseGet(() -> ResponseEntity.badRequest().body(ActionResponse.builder().body("Plugin not found for " + name).build()));
  }

  @GetMapping(value = "/{name}/notification")
  @ResponseBody
  public ResponseEntity<NotificationResponse> doNotify(@PathVariable(value = "name") String name) {
    return pluginList.lookup(name).map(pluginImpl -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
        .body(pluginImpl.doNotification())).orElseGet(() -> ResponseEntity.badRequest().body(NotificationResponse.builder().body("Plugin not found for " + name).build()));
  }

}
