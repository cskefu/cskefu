/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.plugins.chatbot;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.plugins.PluginRegistry;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.chatopera.compose4j.Composer;
import com.chatopera.compose4j.Middleware;
import com.chatopera.compose4j.exception.Compose4jRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

@Component
public class ChatbotComposer implements ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(
            ChatbotComposer.class);

    private static ApplicationContext applicationContext;

    private Composer<ChatbotContext> composer;

    @Autowired
    private PluginRegistry pluginRegistry;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    @PostConstruct
    public void postConstruct() {
        applicationContext.getBeansWithAnnotation(Configuration.class);
        composer = new Composer<>();

        pluginRegistry.getPlugin(Constants.CSKEFU_MODULE_MESSENGER).ifPresent(p -> {
            composer.use((Middleware) applicationContext.getBean(
                    p.getPluginId() + PluginRegistry.PLUGIN_CHATBOT_MESSAGER_SUFFIX));
        });

    }

    public void handle(ChatMessage resp) {
        ChatbotContext ctx = new ChatbotContext();
        ctx.setResp(resp);

        try {
            composer.handle(ctx);
        } catch (Compose4jRuntimeException e) {
            logger.info("[chatbot send] error", e);
        }
    }
}
