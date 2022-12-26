package com.cskefu.serving.api.controllers;


import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SampleControllerTest {

  @Autowired
  private SampleController cut;
  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("When the SpringBoot application is initialized, then the controller is created")
  void testControllerInit() {
    assertNotNull(cut);
  }

  @Test
  @DisplayName("When samples list api is called, then the list of installed bundles is returned")
  void samplesList() throws Exception {
    mockMvc.perform(get("/v1/samples")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("[]")));
  }

  @Test
  @DisplayName("When missing sample bundle is called, then the httpStatus is 400 with a message explaining the problem")
  void missingPluginCall() throws Exception {
    mockMvc.perform(get("/v1/samples/missing-bundle")).andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Plugin not found for missing-bundle")));
  }

  @Test
  @DisplayName("When missing sample bundle notification is called, then the httpStatus is 400 with a message explaining the problem")
  void missingPluginNotificationCall() throws Exception {
    mockMvc.perform(get("/v1/samples/missing-bundle/notification")).andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Plugin not found for missing-bundle")));
  }
}
