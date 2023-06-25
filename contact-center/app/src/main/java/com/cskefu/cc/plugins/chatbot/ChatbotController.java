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

import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.exception.CSKefuException;
import com.cskefu.cc.model.Chatbot;
import com.cskefu.cc.model.Channel;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.ChatbotRepository;
import com.cskefu.cc.persistence.repository.ChannelRepository;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.SystemEnvHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/admin/system/chatbot")
public class ChatbotController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private ChannelRepository snsAccountRes;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private ChannelRepository channelRepository;

    private final static String botServiceProvider = SystemEnvHelper.getenv(
            ChatbotConstants.BOT_PROVIDER, ChatbotConstants.DEFAULT_BOT_PROVIDER);


    @RequestMapping(value = "/index")
    @Menu(type = "chatbot", subtype = "index", access = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid final String chatbotid) throws CSKefuException {
        logger.info("[index] chatbot id {}", chatbotid);

        ModelAndView view = request(super.createView("/admin/system/chatbot/index"));
        List<Chatbot> chatbots = chatbotRes.findAll();
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
                channelRepository.findOneBySnsTypeAndSnsId(
                        currentbot.getChannel(),
                        currentbot.getSnsAccountIdentifier()).ifPresent(p -> {
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
            Chatbot c = chatbotRes.getReferenceById(id);
            Optional<Channel> snsAccountOpt = snsAccountRes.findBySnsid(c.getSnsAccountIdentifier());
            view.addObject("snsurl", snsAccountOpt.get().getType() == "webim" ? snsAccountOpt.get().getBaseURL() : snsAccountOpt.get().getName());
            view.addObject("bot", c);
            view.addObject("type", snsAccountOpt.get().getType());

        }

        view.addObject("id", id);
        view.addObject("botServiceProvider", botServiceProvider);

        return view;
    }
}


