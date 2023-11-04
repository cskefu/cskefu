/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.controller.apps.service;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.model.AgentServiceSummary;
import com.cskefu.cc.model.PassportWebIMUser;
import com.cskefu.cc.model.PassportWechatUser;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.AgentUserProxy;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    private PassportWebIMUserRepository onlineUserRes;

    @Autowired
    private UserEventRepository userEventRes;

    @Autowired
    private ServiceSummaryRepository serviceSummaryRes;


    @Autowired
    private PassportWebIMUserHistRepository onlineUserHisRes;

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
        if (StringUtils.isNotBlank(userid)) {
            map.put(
                    "inviteResult",
                    MainUtils.getWebIMInviteResult(onlineUserRes.findByUserid(userid)));
            map.put("tagRelationList", tagRelationRes.findByUserid(userid));
            map.put("onlineUserHistList", onlineUserHisRes.findByUserid(userid));
            map.put(
                    "agentServicesAvg", onlineUserRes.countByUserForAvagTime(
                            MainContext.AgentUserStatusEnum.END.toString(),
                            userid));

            List<AgentService> agentServiceList = agentServiceRes.findByUseridOrderByLogindateDesc(
                    userid);

            map.put("agentServiceList", agentServiceList);
            if (agentServiceList.size() > 0) {
                map.put("serviceCount", this.agentServiceRes
                        .countByUseridAndStatus(userid,
                                MainContext.AgentUserStatusEnum.END.toString()));

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
                    List<AgentServiceSummary> summaries = serviceSummaryRes.findByAgentserviceid(
                            agentService.getId());
                    if (summaries.size() > 0) {
                        map.put("summary", summaries.get(0));
                    }

                }

                agentUserContactsRes.findOneByUserid(userid).ifPresent(p -> {
                    if (p.getContactsid() != null) {
                        map.put("contacts", contactsRes.findById(p.getContactsid()).orElse(null));
                    }
                });
                AgentService service = agentServiceRes.findById(agentservice).orElse(null);
                if (service != null) {
                    map.addAttribute(
                            "tags", tagRes.findByTagtypeAndSkill(MainContext.ModelType.USER.toString(), service.getSkill()));
                }
                map.put(
                        "summaryTags",
                        tagRes.findByTagtype(MainContext.ModelType.SUMMARY.toString()));
                map.put("curAgentService", agentService);


                map.put(
                        "agentUserMessageList",
                        chatMessageRepository.findByAgentserviceid(agentService.getId(),
                                PageRequest.of(
                                        0, 50, Direction.DESC,
                                        "updatetime")));
            }

            if (MainContext.ChannelType.WEIXIN.toString().equals(channel)) {
                List<PassportWechatUser> passportWechatUserList = weiXinUserRes.findByOpenid(userid);
                if (passportWechatUserList.size() > 0) {
                    PassportWechatUser passportWechatUser = passportWechatUserList.get(0);
                    map.put("weiXinUser", passportWechatUser);
                }
            } else if (MainContext.ChannelType.WEBIM.toString().equals(channel)) {
                PassportWebIMUser passportWebIMUser = onlineUserRes.findById(userid).orElse(null);
                if (passportWebIMUser != null) {
                    map.put("onlineUser", passportWebIMUser);
                }
            }

            cache.findOneAgentUserByUserId(userid).ifPresent(agentUser -> {
                map.put("agentUser", agentUser);
                map.put("curagentuser", agentUser);
            });


        }
        return request(super.createView("/apps/service/online/index"));
    }

    @RequestMapping("/online/chatmsg")
    @Menu(type = "service", subtype = "chatmsg", admin = true)
    public ModelAndView onlinechat(ModelMap map, HttpServletRequest request, String id, String title) {
        AgentService agentService = agentServiceRes.findById(id).orElse(null);
        map.put("curAgentService", agentService);
        cache.findOneAgentUserByUserId(agentService.getUserid()).ifPresent(p -> {
            map.put("curagentuser", p);
        });

        if (StringUtils.isNotBlank(title)) {
            map.put("title", title);
        }

        map.put(
                "summaryTags",
                tagRes.findByTagtype(MainContext.ModelType.SUMMARY.toString()));

        if (agentService != null) {
            List<AgentServiceSummary> summaries = serviceSummaryRes.findByAgentserviceid(
                    agentService.getId());
            if (summaries.size() > 0) {
                map.put("summary", summaries.get(0));
            }

        }

        map.put(
                "agentUserMessageList",
                chatMessageRepository.findByAgentserviceid(agentService.getId(),
                        PageRequest.of(0, 50, Direction.DESC,
                                "updatetime")));

        return request(super.createView("/apps/service/online/chatmsg"));
    }

    @RequestMapping("/trace")
    @Menu(type = "service", subtype = "trace")
    public ModelAndView trace(
            final ModelMap map, final HttpServletRequest request,
            final @Valid String sessionid,
            final @Valid String userid) {
        logger.info("[trace] online user {}, sessionid {}", userid, sessionid);
        if (StringUtils.isNotBlank(sessionid)) {
            map.addAttribute(
                    "traceHisList", userEventRes.findBySessionid(sessionid,
                            PageRequest.of(0, 100)));
        }
        return request(super.createView("/apps/service/online/trace"));
    }
}
