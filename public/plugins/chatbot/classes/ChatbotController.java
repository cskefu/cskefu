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

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.model.Chatbot;
import com.chatopera.cc.model.SNSAccount;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.ChatbotRepository;
import com.chatopera.cc.persistence.repository.SNSAccountRepository;
import com.chatopera.cc.proxy.UserProxy;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.SystemEnvHelper;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping(value = "/admin/system/chatbot")
public class ChatbotController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private SNSAccountRepository snsAccountRepository;

    private final static String botServiceProvider = SystemEnvHelper.getenv(
            ChatbotConstants.BOT_PROVIDER, ChatbotConstants.DEFAULT_BOT_PROVIDER);


    @RequestMapping(value = "/index")
    @Menu(type = "chatbot", subtype = "index", access = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid final String chatbotid) throws CSKefuException {
        logger.info("[index] chatbot id {}", chatbotid);

        ModelAndView view = request(super.createView("/admin/system/chatbot/index"));
        List<Chatbot> chatbots = chatbotRes.findByOrgi(super.getOrgi(request));
        Chatbot currentbot = null;


        logger.info("[index] chatbot size {}", chatbots.size());
        if (chatbots.size() > 0) {
            view.addObject("chatbots", chatbots);
            if (StringUtils.isNotBlank(chatbotid)) {
                view.addObject("currentbotid", chatbotid);
                boolean resolved = false;
                for (final Chatbot b : chatbots) {
                    if (StringUtils.equals(b.getId(), chatbotid)) {
                        view.addObject("currentbot", b);
                        currentbot = b;
                        resolved = true;
                        break;
                    }
                }

                if (!resolved) {
                    // TODO 优化查到不到Bot的提示
                    throw new CSKefuException("Can not find target chatbot by id [" + chatbotid + "]");
                }

            } else {
                currentbot = chatbots.get(0);
                view.addObject("currentbotid", currentbot.getId());
                view.addObject("currentbot", currentbot);
            }
        }

        view.addObject("botServiceProvider", botServiceProvider);

        // 增加当前bot的更多信息
        if (currentbot != null) {
            // 创建人
            final User creator = userProxy.findOne(currentbot.getCreater());
            if (creator != null) {
                view.addObject("creatorname", creator.getUname());
            }
            // 隶属渠道
            if (StringUtils.isNotBlank(currentbot.getSnsAccountIdentifier())) {
                snsAccountRepository.findOneBySnsTypeAndSnsIdAndOrgi(
                        currentbot.getChannel(),
                        currentbot.getSnsAccountIdentifier(),
                        currentbot.getOrgi()).ifPresent(p -> {
                    view.addObject("snsAccountName", p.getName());
                    view.addObject("snsAccountId", p.getId());
                });

            }
        }


        return view;
    }

    @RequestMapping(value = "/edit")
    @Menu(type = "chatbot", subtype = "index", access = true)
    public ModelAndView eidt(ModelMap map, HttpServletRequest request, @Valid String id) {
        User curruser = super.getUser(request);

        ModelAndView view = request(super.createView("/admin/system/chatbot/edit"));
        if (id != null) {
            Chatbot c = chatbotRes.findOne(id);
            SNSAccount snsAccount = snsAccountRes.findBySnsidAndOrgi(c.getSnsAccountIdentifier(), curruser.getOrgi());
            view.addObject("snsurl", snsAccount.getSnstype() == "webim" ? snsAccount.getBaseURL() : snsAccount.getName());
            view.addObject("bot", c);
            view.addObject("snstype", snsAccount.getSnstype());

        }

        view.addObject("id", id);
        view.addObject("botServiceProvider", botServiceProvider);

        return view;
    }
}


