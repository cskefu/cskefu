/*
 * Copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.plugins.chatbot;

import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.compose4j.AbstractContext;

public class ChatbotContext extends AbstractContext {
    private ChatMessage resp;

    public ChatMessage getResp() {
        return resp;
    }

    public void setResp(ChatMessage resp) {
        this.resp = resp;
    }
}
