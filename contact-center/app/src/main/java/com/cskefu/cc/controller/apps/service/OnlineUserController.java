/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.controller.apps.service;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.model.AgentServiceSummary;
import com.cskefu.cc.model.OnlineUser;
import com.cskefu.cc.model.WeiXinUser;
import com.cskefu.cc.persistence.es.ContactsRepository;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.AgentUserProxy;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/service")
public class OnlineUserController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(OnlineUserController.class);

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private UserEventRepository userEventRes;

    @Autowired
    private ServiceSummaryRepository serviceSummaryRes;


    @Autowired
    private OnlineUserHisRepository onlineUserHisRes;

    @Autowired
    private WeiXinUserRepository weiXinUserRes;

    @Autowired
    private TagRepository tagRes;

    @Autowired
    private TagRelationRepository tagRelationRes;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private AgentUserContactsRepository agentUserContactsRes;

    @Autowired
    private Cache cache;

    @Autowired
    private AgentUserRepository agentUserRes;

    @RequestMapping("/online/index")
    @Menu(type = "service", subtype = "online", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, String userid, String agentservice, @Valid String channel) {
        final String orgi = super.getOrgi(request);
        if (StringUtils.isNotBlank(userid)) {
            map.put(
                    "inviteResult",
                    MainUtils.getWebIMInviteResult(onlineUserRes.findByOrgiAndUserid(orgi, userid)));
            map.put("tagRelationList", tagRelationRes.findByUserid(userid));
            map.put("onlineUserHistList", onlineUserHisRes.findByUseridAndOrgi(userid, orgi));
            map.put(
                    "agentServicesAvg", onlineUserRes.countByUserForAvagTime(
                            orgi,
                            MainContext.AgentUserStatusEnum.END.toString(),
                            userid));

            List<AgentService> agentServiceList = agentServiceRes.findByUseridAndOrgiOrderByLogindateDesc(
                    userid, orgi);

            map.put("agentServiceList", agentServiceList);
            if (agentServiceList.size() > 0) {
                map.put("serviceCount", Integer
                        .valueOf(this.agentServiceRes
                                .countByUseridAndOrgiAndStatus(userid, orgi,
                                        MainContext.AgentUserStatusEnum.END.toString())));

                AgentService agentService = agentServiceList.get(0);
                if (StringUtils.isNotBlank(agentservice)) {
                    for (AgentService as : agentServiceList) {
                        if (as.getId().equals(agentservice)) {
                            agentService = as;
                            break;
                        }
                    }
                }

                if (agentService != null) {
                    List<AgentServiceSummary> summaries = serviceSummaryRes.findByAgentserviceidAndOrgi(
                            agentService.getId(), orgi);
                    if (summaries.size() > 0) {
                        map.put("summary", summaries.get(0));
                    }

                }

                agentUserContactsRes.findOneByUseridAndOrgi(
                        userid, orgi).ifPresent(p -> {
                    map.put("contacts", contactsRes.findOne(p.getContactsid()));
                });
                AgentService service = agentServiceRes.findByIdAndOrgi(agentservice, orgi);
                if (service != null) {
                    map.addAttribute(
                            "tags", tagRes.findByOrgiAndTagtypeAndSkill(orgi, MainContext.ModelType.USER.toString(), service.getSkill()));
                }
                map.put(
                        "summaryTags",
                        tagRes.findByOrgiAndTagtype(orgi, MainContext.ModelType.SUMMARY.toString()));
                map.put("curAgentService", agentService);


                map.put(
                        "agentUserMessageList",
                        chatMessageRepository.findByAgentserviceidAndOrgi(agentService.getId(), orgi,
                                new PageRequest(
                                        0, 50, Direction.DESC,
                                        "updatetime")));
            }

            if (MainContext.ChannelType.WEIXIN.toString().equals(channel)) {
                List<WeiXinUser> weiXinUserList = weiXinUserRes.findByOpenidAndOrgi(userid, orgi);
                if (weiXinUserList.size() > 0) {
                    WeiXinUser weiXinUser = weiXinUserList.get(0);
                    map.put("weiXinUser", weiXinUser);
                }
            } else if (MainContext.ChannelType.WEBIM.toString().equals(channel)) {
                OnlineUser onlineUser = onlineUserRes.findOne(userid);
                if (onlineUser != null) {
                    map.put("onlineUser", onlineUser);
                }
            }

            cache.findOneAgentUserByUserIdAndOrgi(userid, orgi).ifPresent(agentUser -> {
                map.put("agentUser", agentUser);
                map.put("curagentuser", agentUser);
            });


        }
        return request(super.createView("/apps/service/online/index"));
    }

    @RequestMapping("/online/chatmsg")
    @Menu(type = "service", subtype = "chatmsg", admin = true)
    public ModelAndView onlinechat(ModelMap map, HttpServletRequest request, String id, String title) {
        AgentService agentService = agentServiceRes.getOne(id);
        map.put("curAgentService", agentService);
        cache.findOneAgentUserByUserIdAndOrgi(agentService.getUserid(), super.getOrgi(request)).ifPresent(p -> {
            map.put("curagentuser", p);
        });

        if (StringUtils.isNotBlank(title)) {
            map.put("title", title);
        }

        map.put(
                "summaryTags",
                tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.ModelType.SUMMARY.toString()));

        if (agentService != null) {
            List<AgentServiceSummary> summaries = serviceSummaryRes.findByAgentserviceidAndOrgi(
                    agentService.getId(), super.getOrgi(request));
            if (summaries.size() > 0) {
                map.put("summary", summaries.get(0));
            }

        }

        map.put(
                "agentUserMessageList",
                chatMessageRepository.findByAgentserviceidAndOrgi(agentService.getId(), super.getOrgi(request),
                        new PageRequest(0, 50, Direction.DESC,
                                "updatetime")));

        return request(super.createView("/apps/service/online/chatmsg"));
    }

    @RequestMapping("/trace")
    @Menu(type = "service", subtype = "trace", admin = false)
    public ModelAndView trace(
            final ModelMap map, final HttpServletRequest request,
            final @Valid String sessionid,
            final @Valid String userid) {
        logger.info("[trace] online user {}, sessionid {}", userid, sessionid);
        if (StringUtils.isNotBlank(sessionid)) {
            map.addAttribute(
                    "traceHisList", userEventRes.findBySessionidAndOrgi(sessionid, super.getOrgi(request),
                            new PageRequest(0, 100)));
        }
        return request(super.createView("/apps/service/online/trace"));
    }
}
