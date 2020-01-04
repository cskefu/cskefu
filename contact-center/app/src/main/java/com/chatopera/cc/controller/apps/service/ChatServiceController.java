/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.controller.apps.service;

import com.chatopera.cc.acd.ACDAgentService;
import com.chatopera.cc.acd.ACDVisitorDispatcher;
import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.acd.basic.ACDMessageHelper;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.*;
import com.chatopera.cc.peer.PeerSyncIM;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.*;
import com.chatopera.cc.socketio.message.Message;
import com.chatopera.cc.util.IP;
import com.chatopera.cc.util.IPTools;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.CharacterCodingException;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping("/service")
public class ChatServiceController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ChatServiceController.class);

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Autowired
    private AgentStatusProxy agentStatusProxy;

    @Autowired
    private ACDAgentService acdAgentService;

    @Autowired
    private ACDVisitorDispatcher acdVisitorDispatcher;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private AgentStatusRepository agentStatusRepository;

    @Autowired
    private AgentUserRepository agentUserRepository;

    @Autowired
    private LeaveMsgRepository leaveMsgRes;

    @Autowired
    private LeaveMsgProxy leaveMsgProxy;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private OrganRepository organ;

    @Autowired
    private UserRepository user;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private OrgiSkillRelRepository orgiSkillRelService;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private Cache cache;

    @Autowired
    private PeerSyncIM peerSyncIM;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @RequestMapping("/history/index")
    @Menu(type = "service", subtype = "history", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, final String username, final String channel, final String servicetype, final String allocation, final String servicetimetype, final String begin, final String end) {
        Page<AgentService> page = agentServiceRes.findAll(new Specification<AgentService>() {
            @Override
            public Predicate toPredicate(Root<AgentService> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(username)) {
                    list.add(cb.equal(root.get("username").as(String.class), username));
                }
                if (StringUtils.isNotBlank(channel)) {
                    list.add(cb.equal(root.get("channel").as(String.class), channel));
                }
                if (StringUtils.isNotBlank(servicetype) && StringUtils.isNotBlank(allocation)) {
                    list.add(cb.equal(root.get(servicetype).as(String.class), allocation));
                }
                if (StringUtils.isNotBlank(servicetimetype)) {
                    try {
                        if (StringUtils.isNotBlank(begin) && begin.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                            list.add(cb.greaterThanOrEqualTo(
                                    root.get(servicetimetype).as(Date.class),
                                    MainUtils.simpleDateFormat.parse(begin)));
                        }
                        if (StringUtils.isNotBlank(end) && end.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
                            list.add(cb.lessThanOrEqualTo(
                                    root.get(servicetimetype).as(Date.class),
                                    MainUtils.dateFormate.parse(end + " 23:59:59")));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        }, new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        map.put("agentServiceList", page);
        map.put("username", username);
        map.put("channel", channel);
        map.put("servicetype", servicetype);
        map.put("servicetimetype", servicetimetype);
        map.put("allocation", allocation);
        map.put("begin", begin);
        map.put("end", end);
        map.put("deptlist", organ.findByOrgi(super.getOrgi(request)));
        map.put("userlist", user.findByOrgiAndDatastatus(super.getOrgi(request), false));

        return request(super.createAppsTempletResponse("/apps/service/history/index"));
    }

    @RequestMapping("/current/index")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView current(ModelMap map, HttpServletRequest request) {
        map.put(
                "agentServiceList", agentServiceRes.findByOrgiAndStatus(
                        super.getOrgi(request),
                        MainContext.AgentUserStatusEnum.INSERVICE.toString(),
                        new PageRequest(
                                super.getP(request),
                                super.getPs(request), Direction.DESC,
                                "createtime")));
        return request(super.createAppsTempletResponse("/apps/service/current/index"));
    }

    @RequestMapping("/current/trans")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView trans(ModelMap map, HttpServletRequest request, @Valid String id) {
        final User logined = super.getUser(request);

        if (StringUtils.isNotBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            List<Organ> skillGroups = OnlineUserProxy.organ(super.getOrgi(request), true);
            Set<String> organs = logined.getOrgans().keySet();
            String currentOrgan = organs.size() > 0 ? (new ArrayList<>(organs)).get(0) : null;

            if (StringUtils.isBlank(currentOrgan)) {
                if (!skillGroups.isEmpty()) {
                    currentOrgan = skillGroups.get(0).getId();
                }
            }
            List<AgentStatus> agentStatusList = cache.getAgentStatusBySkillAndOrgi(null, super.getOrgi(request));
            List<String> usersids = new ArrayList<String>();
            if (!agentStatusList.isEmpty()) {
                for (AgentStatus agentStatus : agentStatusList) {
                    if (agentStatus != null) {
                        usersids.add(agentStatus.getAgentno());
                    }
                }
            }
            List<User> userList = userRes.findAll(usersids);
            for (User user : userList) {
                user.setAgentStatus(cache.findOneAgentStatusByAgentnoAndOrig(user.getId(), super.getOrgi(request)));
                userProxy.attachOrgansPropertiesForUser(user);
            }
            map.addAttribute("userList", userList);
            map.addAttribute("userid", agentService.getUserid());
            map.addAttribute("agentserviceid", agentService.getId());
            map.addAttribute("agentuserid", agentService.getAgentuserid());
            map.addAttribute("agentservice", agentService);
            map.addAttribute("skillGroups", skillGroups);
            map.addAttribute("currentorgan", currentOrgan);
        }

        return request(super.createRequestPageTempletResponse("/apps/service/current/transfer"));
    }

    @RequestMapping(value = "/transfer/save")
    @Menu(type = "apps", subtype = "transfersave")
    public ModelAndView transfersave(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String agentno, @Valid String memo) throws CharacterCodingException {
        if (StringUtils.isNotBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            final User targetAgent = userRes.findOne(agentno);
            AgentUser agentUser = null;
            Optional<AgentUser> agentUserOpt = cache.findOneAgentUserByUserIdAndOrgi(
                    agentService.getUserid(), super.getOrgi(request));
            if (agentUserOpt.isPresent()) {
                agentUser = agentUserOpt.get();
            }

            if (agentUser != null) {
                agentUser.setAgentno(agentno);
                agentUser.setAgentname(targetAgent.getUname());
                agentUserRepository.save(agentUser);
                if (MainContext.AgentUserStatusEnum.INSERVICE.toString().equals(
                        agentUser.getStatus())) {
                    // 转接 ， 发送消息给 目标坐席
                    AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                            super.getUser(request).getId(), super.getOrgi(request));

                    if (agentStatus != null) {
                        agentUserProxy.updateAgentStatus(agentStatus, super.getOrgi(request));
                    }

                    AgentStatus transAgentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                            agentno, super.getOrgi(request));
                    if (transAgentStatus != null) {
                        agentUserProxy.updateAgentStatus(transAgentStatus, super.getOrgi(request));
                        agentService.setAgentno(agentno);
                        agentService.setAgentusername(transAgentStatus.getUsername());
                    }
                    // 转接坐席提示消息
                    try {
                        Message outMessage = new Message();
                        outMessage.setMessage(
                                acdMessageHelper.getSuccessMessage(agentService, agentUser.getChannel(), super.getOrgi(request)));
                        outMessage.setMessageType(MainContext.MediaType.TEXT.toString());
                        outMessage.setCalltype(MainContext.CallType.IN.toString());
                        outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));
                        outMessage.setAgentUser(agentUser);
                        outMessage.setAgentService(agentService);

                        if (org.apache.commons.lang.StringUtils.isNotBlank(agentUser.getUserid())) {
                            peerSyncIM.send(
                                    MainContext.ReceiverType.VISITOR,
                                    MainContext.ChannelType.toValue(agentUser.getChannel()),
                                    agentUser.getAppid(),
                                    MainContext.MessageType.STATUS,
                                    agentUser.getUserid(),
                                    outMessage,
                                    true
                            );
                        }

                        // 通知转接消息给新坐席
                        outMessage.setChannelMessage(agentUser);
                        outMessage.setAgentUser(agentUser);
                        peerSyncIM.send(
                                MainContext.ReceiverType.AGENT, MainContext.ChannelType.WEBIM,
                                agentUser.getAppid(), MainContext.MessageType.NEW, agentService.getAgentno(),
                                outMessage, true
                        );

                    } catch (Exception ex) {
                        logger.error("[transfersave]", ex);
                    }
                }
            } else {
                agentUser = agentUserRepository.findByIdAndOrgi(agentService.getAgentuserid(), super.getOrgi(request));
                if (agentUser != null) {
                    agentUser.setAgentno(agentno);
                    agentUser.setAgentname(targetAgent.getUname());
                    agentUserRepository.save(agentUser);
                }
            }

            if (agentService != null) {
                agentService.setAgentno(agentno);
                if (StringUtils.isNotBlank(memo)) {
                    agentService.setTransmemo(memo);
                }
                agentService.setTrans(true);
                agentService.setTranstime(new Date());
                agentServiceRes.save(agentService);
            }
        }

        return request(super.createRequestPageTempletResponse("redirect:/service/current/index.html"));
    }

    @RequestMapping("/current/end")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView end(ModelMap map, HttpServletRequest request, @Valid String id) throws Exception {
        if (StringUtils.isNotBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (agentService != null) {
                User user = super.getUser(request);
                AgentUser agentUser = agentUserRepository.findByIdAndOrgi(
                        agentService.getAgentuserid(), super.getOrgi(request));
                if (agentUser != null) {
                    acdAgentService.finishAgentUser(agentUser, user.getOrgi());
                }
                agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                agentServiceRes.save(agentService);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/current/index.html"));
    }

    /**
     * 邀请
     *
     * @param map
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/current/invite")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView currentinvite(
            ModelMap map,
            final HttpServletRequest request,
            final @Valid String id) throws Exception {
        if (StringUtils.isNotBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (agentService != null) {
                final User user = super.getUser(request);
                if (StringUtils.isBlank(agentService.getAgentno())) {

                    // 将AiUser替换为OnlineUser
                    // TODO #153 https://gitlab.chatopera.com/chatopera/cosinee/issues/153
                    //  此处可能会有逻辑问题，从而导致BUG
                    // AiUser 定义参考
                    // https://gitlab.chatopera.com/chatopera/cosinee.w4l/blob/2ea2ad5cad92d2d9f4ceb88e9608c7019495ccf5/contact-center/app/src/main/java/com/chatopera/cc/app/model/AiUser.java
                    // 需要做更多测试
                    OnlineUser onlineUser = cache.findOneOnlineUserByUserIdAndOrgi(
                            agentService.getUserid(), agentService.getOrgi());

                    if (onlineUser != null) {
                        IP ipdata = IPTools.getInstance().findGeography(onlineUser.getIp());
                        acdVisitorDispatcher.enqueue(ACDMessageHelper.getWebIMComposeContext(
                                onlineUser.getUserid(),
                                onlineUser.getUsername(),
                                user.getOrgi(),
                                agentService.getSessionid(),
                                agentService.getAppid(),
                                agentService.getIpaddr(),
                                agentService.getOsname(),
                                agentService.getBrowser(),
                                "",
                                ipdata,
                                agentService.getChannel(),
                                null, // 此处绑定坐席，不指定技能组
                                user.getId(),
                                null,
                                null,
                                agentService.getContactsid(),
                                onlineUser.getOwner(),
                                true,
                                MainContext.ChatInitiatorType.AGENT.toString()));
                    }
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/current/index.html"));
    }


    @RequestMapping("/quene/index")
    @Menu(type = "service", subtype = "filter", admin = true)
    public ModelAndView quene(ModelMap map, HttpServletRequest request) {
        Page<AgentUser> agentUserList = agentUserRes.findByOrgiAndStatus(
                super.getOrgi(request), MainContext.AgentUserStatusEnum.INQUENE.toString(),
                new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        List<String> skillGroups = new ArrayList<String>();
        for (AgentUser agentUser : agentUserList.getContent()) {
            agentUser.setWaittingtime((int) (System.currentTimeMillis() - agentUser.getCreatetime().getTime()));
            if (StringUtils.isNotBlank(agentUser.getSkill())) {
                skillGroups.add(agentUser.getSkill());
            }
        }
        if (skillGroups.size() > 0) {
            List<Organ> organList = organRes.findAll(skillGroups);
            for (AgentUser agentUser : agentUserList.getContent()) {
                if (StringUtils.isNotBlank(agentUser.getSkill())) {
                    for (Organ organ : organList) {
                        if (agentUser.getSkill().equals(organ.getId())) {
                            agentUser.setSkillname(organ.getName());
                            break;
                        }
                    }
                }
            }
        }
        map.put("agentUserList", agentUserList);
        return request(super.createAppsTempletResponse("/apps/service/quene/index"));
    }

    @RequestMapping("/quene/clean")
    @Menu(type = "service", subtype = "queneclean", admin = true)
    public ModelAndView clean(ModelMap map, HttpServletRequest request, @Valid String id) {
        AgentUser agentUser = agentUserRes.findByIdAndOrgi(id, super.getOrgi(request));
        if (agentUser != null && agentUser.getStatus().equals(MainContext.AgentUserStatusEnum.INQUENE.toString())) {
            agentUser.setAgentno(null);
            agentUser.setSkill(null);
            agentUserRes.save(agentUser);
            ACDComposeContext ctx = acdMessageHelper.getComposeContextWithAgentUser(
                    agentUser, false, MainContext.ChatInitiatorType.USER.toString());
            acdVisitorDispatcher.enqueue(ctx);
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/quene/index.html"));
    }

    @RequestMapping("/quene/invite")
    @Menu(type = "service", subtype = "invite", admin = true)
    public ModelAndView invite(ModelMap map, HttpServletRequest request, @Valid String id) throws Exception {
        final User logined = super.getUser(request);
        final String orgi = logined.getOrgi();
        AgentUser agentUser = agentUserRes.findByIdAndOrgi(id, super.getOrgi(request));
        if (agentUser != null && agentUser.getStatus().equals(MainContext.AgentUserStatusEnum.INQUENE.toString())) {
            acdAgentService.assignVisitorAsInvite(logined.getId(), agentUser, orgi);
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/quene/index.html"));
    }

    /**
     * 管理员查看在线坐席
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping("/agent/index")
    @Menu(type = "service", subtype = "onlineagent", admin = true)
    public ModelAndView agent(ModelMap map, HttpServletRequest request) {
        final Map<String, AgentStatus> ass = cache.findAllAgentStatusByOrgi(super.getOrgi(request));

        List<AgentStatus> lis = new ArrayList<>();
        for (final Map.Entry<String, AgentStatus> as : ass.entrySet()) {
            lis.add(as.getValue());
        }

        map.put("agentStatusList", lis);
        return request(super.createAppsTempletResponse("/apps/service/agent/index"));
    }

    /**
     * 查看离线坐席
     *
     * @param map
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("/agent/offline")
    @Menu(type = "service", subtype = "offline", admin = true)
    public ModelAndView offline(ModelMap map, HttpServletRequest request, @Valid String id) {

        AgentStatus agentStatus = agentStatusRepository.findByIdAndOrgi(id, super.getOrgi(request));
        if (agentStatus != null) {
            agentStatusRepository.delete(agentStatus);
        }
        cache.deleteAgentStatusByAgentnoAndOrgi(agentStatus.getAgentno(), super.getOrgi(request));

        agentStatusProxy.broadcastAgentsStatus(
                super.getOrgi(request), "agent", "offline", super.getUser(request).getId());

        return request(super.createRequestPageTempletResponse("redirect:/service/agent/index.html"));
    }

    /**
     * 非管理员坐席
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping("/user/index")
    @Menu(type = "service", subtype = "userlist", admin = true)
    public ModelAndView user(ModelMap map, HttpServletRequest request) {
        Page<User> userList;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            userList = userProxy.findByOrganInAndAgentAndDatastatus(
                    organIdList, true, false, new PageRequest(super.getP(request), super.getPs(request), Direction.DESC,
                                                              "createtime"));
        } else {
            userList = userRes.findByOrgiAndAgentAndDatastatus(
                    super.getOrgi(request), true, false, new PageRequest(super.getP(request), super.getPs(request),
                                                                         Direction.DESC, "createtime"));
        }

        Map<String, Boolean> onlines = new HashMap<>();

        for (User user : userList.getContent()) {
            if (cache.findOneAgentStatusByAgentnoAndOrig(user.getId(), super.getOrgi(request)) != null) {
                onlines.put(user.getId(), true);
            } else {
                onlines.put(user.getId(), false);
            }
        }
        map.put("userList", userList);
        map.put("onlines", onlines);
        return request(super.createAppsTempletResponse("/apps/service/user/index"));
    }

    @RequestMapping("/leavemsg/index")
    @Menu(type = "service", subtype = "leavemsg", admin = true)
    public ModelAndView leavemsg(ModelMap map, HttpServletRequest request) {
        Page<LeaveMsg> leaveMsgs = leaveMsgRes.findByOrgi(
                super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request), Direction.DESC,
                                                        "createtime"));
        for (final LeaveMsg l : leaveMsgs) {
            leaveMsgProxy.resolveChannelBySnsid(l);
        }

        map.put("leaveMsgList", leaveMsgs);
        return request(super.createAppsTempletResponse("/apps/service/leavemsg/index"));
    }

    @RequestMapping("/leavemsg/delete")
    @Menu(type = "service", subtype = "leavemsg", admin = true)
    public ModelAndView leavemsg(ModelMap map, HttpServletRequest request, @Valid String id) {
        if (StringUtils.isNotBlank(id)) {
            leaveMsgRes.delete(id);
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/leavemsg/index.html"));
    }
}
