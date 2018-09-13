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

import com.chatopera.cc.app.persistence.hibernate.BaseService;
import com.chatopera.cc.app.persistence.repository.ChatbotRepository;
import com.chatopera.cc.exchange.UserEvent;

public class ChatbotEvent<S> implements UserEvent {

    private S data;
    private String eventype;

    public ChatbotEvent(S data, String eventype) {
        this.data = data;
        this.eventype = eventype;
    }

    public S getData() {
        return data;
    }

    public void setData(S data) {
        this.data = data;
    }

    public String getEventype() {
        return eventype;
    }

    public void setEventype(String eventype) {
        this.eventype = eventype;
    }
}