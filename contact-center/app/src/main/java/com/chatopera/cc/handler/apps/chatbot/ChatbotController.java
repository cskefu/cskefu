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

package com.chatopera.cc.handler.apps.chatbot;

import com.chatopera.cc.handler.Handler;
import com.chatopera.cc.model.Chatbot;
import com.chatopera.cc.model.SNSAccount;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.ChatbotRepository;
import com.chatopera.cc.persistence.repository.SNSAccountRepository;
import com.chatopera.cc.util.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/apps/chatbot")
public class ChatbotController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @RequestMapping(value = "/index")
    @Menu(type = "chatbot", subtype = "index", access = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        ModelAndView view = request(super.createAppsTempletResponse("/apps/chatbot/index"));
        List<Chatbot> chatbots = chatbotRes.findByOrgi(super.getOrgi(request));

        logger.info("[index] chatbot size {}", chatbots.size());
        view.addObject("chatbots", chatbots);

        return view;
    }

    @RequestMapping(value = "/edit")
    @Menu(type = "chatbot", subtype = "index", access = true)
    public ModelAndView eidt(ModelMap map, HttpServletRequest request, @Valid String id) {
        User curruser = super.getUser(request);

        ModelAndView view = request(super.createAppsTempletResponse("/apps/chatbot/edit"));
        if (id != null) {
            Chatbot c = chatbotRes.findOne(id);
            SNSAccount snsAccount = snsAccountRes.findBySnsidAndOrgi(c.getSnsAccountIdentifier(), curruser.getOrgi());
            view.addObject("snsurl", snsAccount.getBaseURL());
            view.addObject("bot", c);
        }

        view.addObject("id", id);

        return view;
    }
}


