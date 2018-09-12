/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.concurrent.chatbot;

import com.chatopera.cc.concurrent.user.UserDataEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ChatbotEventHandler implements EventHandler<UserDataEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ChatbotEventHandler.class);

    @Override
    public void onEvent(UserDataEvent event, long arg1, boolean arg2)
            throws Exception {
        if (event.getEvent() != null) {
            ChatbotEvent x = (ChatbotEvent) event.getEvent();
            // TODO #75
            logger.info("onEvent");
        }
    }

}
