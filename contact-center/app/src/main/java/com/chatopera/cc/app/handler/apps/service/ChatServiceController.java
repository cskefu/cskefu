/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app.handler.apps.service;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.persistence.repository.*;
import com.chatopera.cc.util.IP;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.OnlineUserUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/service")
public class ChatServiceController extends Handler {

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
    private OrganRepository organRes;

    @Autowired
    private OrganRepository organ;

    @Autowired
    private UserRepository user;

    @Autowired
    private UserRepository userRes;
    @Autowired
    private OrgiSkillRelRepository orgiSkillRelService;

    @RequestMapping("/history/index")
    @Menu(type = "service", subtype = "history", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, final String username, final String channel, final String servicetype, final String allocation, final String servicetimetype, final String begin, final String end) {
        Page<AgentService> page = agentServiceRes.findAll(new Specification<AgentService>() {
            @Override
            public Predicate toPredicate(Root<AgentService> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (!StringUtils.isBlank(username)) {
                    list.add(cb.equal(root.get("username").as(String.class), username));
                }
                if (!StringUtils.isBlank(channel)) {
                    list.add(cb.equal(root.get("channel").as(String.class), channel));
                }
                if (!StringUtils.isBlank(servicetype) && !StringUtils.isBlank(allocation)) {
                    list.add(cb.equal(root.get(servicetype).as(String.class), allocation));
                }
                if (!StringUtils.isBlank(servicetimetype)) {
                    try {
                        if (!StringUtils.isBlank(begin) && begin.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                            list.add(cb.greaterThanOrEqualTo(root.get(servicetimetype).as(Date.class), MainUtils.dateFormate.parse(begin)));
                        }
                        if (!StringUtils.isBlank(end) && end.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                            list.add(cb.lessThanOrEqualTo(root.get(servicetimetype).as(Date.class), MainUtils.dateFormate.parse(end)));
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
        map.put("agentServiceList", agentServiceRes.findByOrgiAndStatus(super.getOrgi(request), MainContext.AgentUserStatusEnum.INSERVICE.toString(), new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime")));
        return request(super.createAppsTempletResponse("/apps/service/current/index"));
    }

    @RequestMapping("/current/trans")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView trans(ModelMap map, HttpServletRequest request, @Valid String id) {
        if (!StringUtils.isBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            List<Organ> skillList = OnlineUserUtils.organ(super.getOrgi(request), true);
            String currentOrgan = super.getUser(request).getOrgan();
            if (StringUtils.isBlank(currentOrgan)) {
                if (!skillList.isEmpty()) {
                    currentOrgan = skillList.get(0).getId();
                }
            }
            List<AgentStatus> agentStatusList = AutomaticServiceDist.getAgentStatus(null, super.getOrgi(request));
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
                user.setAgentStatus((AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), super.getOrgi(request)));
            }
            map.addAttribute("userList", userList);
            map.addAttribute("userid", agentService.getUserid());
            map.addAttribute("agentserviceid", agentService.getId());
            map.addAttribute("agentuserid", agentService.getAgentuserid());
            map.addAttribute("agentservice", agentService);
            map.addAttribute("skillList", skillList);
            map.addAttribute("currentorgan", currentOrgan);
        }

        return request(super.createRequestPageTempletResponse("/apps/service/current/transfer"));
    }

    @RequestMapping(value = "/transfer/save")
    @Menu(type = "apps", subtype = "transfersave")
    public ModelAndView transfersave(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String agentno, @Valid String memo) {
        if (!StringUtils.isBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(agentService.getUserid(), super.getOrgi(request));
            if (agentUser != null) {
                agentUser.setAgentno(agentno);
                CacheHelper.getAgentUserCacheBean().put(agentService.getUserid(), agentUser, super.getOrgi(request));
                agentUserRepository.save(agentUser);
                if (MainContext.AgentUserStatusEnum.INSERVICE.toString().equals(agentUser.getStatus())) {        //转接 ， 发送消息给 目标坐席
                    AgentStatus agentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(super.getUser(request).getId(), super.getOrgi(request));

                    if (agentStatus != null) {
                        AutomaticServiceDist.updateAgentStatus(agentStatus, agentUser, super.getOrgi(request), false);
                    }

                    AgentStatus transAgentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(agentno, super.getOrgi(request));
                    if (transAgentStatus != null) {
                        AutomaticServiceDist.updateAgentStatus(transAgentStatus, agentUser, super.getOrgi(request), true);
                        agentService.setAgentno(agentno);
                        agentService.setAgentusername(transAgentStatus.getUsername());
                    }
					// TODO #111 通知转接消息
                    NettyClients.getInstance().publishAgentEventMessage(agentno, MainContext.MessageTypeEnum.NEW.toString(), agentUser);
                }
            } else {
                agentUser = agentUserRepository.findByIdAndOrgi(agentService.getAgentuserid(), super.getOrgi(request));
                if (agentUser != null) {
                    agentUser.setAgentno(agentno);
                    agentUserRepository.save(agentUser);
                }
            }

            if (agentService != null) {
                agentService.setAgentno(agentno);
                if (!StringUtils.isBlank(memo)) {
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
        if (!StringUtils.isBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (agentService != null) {
                User user = super.getUser(request);
                AgentUser agentUser = agentUserRepository.findByIdAndOrgi(agentService.getAgentuserid(), super.getOrgi(request));
                if (agentUser != null) {
                    AutomaticServiceDist.deleteAgentUser(agentUser, user.getOrgi());
                }
                agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                agentServiceRes.save(agentService);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/current/index.html"));
    }

    @RequestMapping("/current/invite")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView currentinvite(ModelMap map, HttpServletRequest request, @Valid String id) throws Exception {
        if (!StringUtils.isBlank(id)) {
            AgentService agentService = agentServiceRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (agentService != null) {
                User user = super.getUser(request);
                if (StringUtils.isBlank(agentService.getAgentno())) {
                    AiUser aiUser = (AiUser) CacheHelper.getOnlineUserCacheBean().getCacheObject(agentService.getSessionid(), agentService.getOrgi());
                    IP ipdata = null;
                    if (aiUser != null) {
                        ipdata = aiUser.getIpdata();
                        OnlineUserUtils.newRequestMessage(aiUser.getUserid(), aiUser.getUsername(), user.getOrgi(), agentService.getSessionid(), agentService.getAppid(), agentService.getIpaddr(), agentService.getOsname(), agentService.getBrowser(), "", ipdata != null ? ipdata : null, agentService.getChannel(), user.getOrgan(), user.getId(), null, null, agentService.getContactsid(), MainContext.ChatInitiatorType.AGENT.toString(), aiUser.getContextid());
                    }
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/current/index.html"));
    }


    @RequestMapping("/quene/index")
    @Menu(type = "service", subtype = "filter", admin = true)
    public ModelAndView quene(ModelMap map, HttpServletRequest request) {
        Page<AgentUser> agentUserList = agentUserRes.findByOrgiAndStatus(super.getOrgi(request), MainContext.AgentUserStatusEnum.INQUENE.toString(), new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        List<String> skillList = new ArrayList<String>();
        for (AgentUser agentUser : agentUserList.getContent()) {
            agentUser.setWaittingtime((int) (System.currentTimeMillis() - agentUser.getCreatetime().getTime()));
            if (!StringUtils.isBlank(agentUser.getSkill())) {
                skillList.add(agentUser.getSkill());
            }
        }
        if (skillList.size() > 0) {
            List<Organ> organList = organRes.findAll(skillList);
            for (AgentUser agentUser : agentUserList.getContent()) {
                if (!StringUtils.isBlank(agentUser.getSkill())) {
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
            agentUser.setAgent(null);
            agentUser.setSkill(null);
            agentUserRes.save(agentUser);
            CacheHelper.getAgentUserCacheBean().put(agentUser.getUserid(), agentUser, super.getOrgi(request));
            AutomaticServiceDist.allotAgent(agentUser, super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/quene/index.html"));
    }

    @RequestMapping("/quene/invite")
    @Menu(type = "service", subtype = "invite", admin = true)
    public ModelAndView invite(ModelMap map, HttpServletRequest request, @Valid String id) throws Exception {
        AgentUser agentUser = agentUserRes.findByIdAndOrgi(id, super.getOrgi(request));
        if (agentUser != null && agentUser.getStatus().equals(MainContext.AgentUserStatusEnum.INQUENE.toString())) {
            AutomaticServiceDist.allotAgentForInvite(super.getUser(request).getId(), agentUser, super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/quene/index.html"));
    }

    @RequestMapping("/agent/index")
    @Menu(type = "service", subtype = "onlineagent", admin = true)
    public ModelAndView agent(ModelMap map, HttpServletRequest request) {
        List<AgentStatus> agentStatusList = agentStatusRepository.findByOrgi(super.getOrgi(request));
        for (int i = 0; i < agentStatusList.size(); ) {
            AgentStatus agentStatus = agentStatusList.get(i);
            if (CacheHelper.getAgentStatusCacheBean().getCacheObject(agentStatus.getAgentno(), super.getOrgi(request)) == null) {
                agentStatusRepository.delete(agentStatus);
                agentStatusList.remove(i);
                continue;
            } else {
                AgentStatus temp = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(agentStatus.getAgentno(), super.getOrgi(request));
                agentStatusList.set(i, temp);
            }
            i++;
        }
        List<String> skillList = new ArrayList<String>();
        for (AgentStatus agentStatus : agentStatusList) {
            if (!StringUtils.isBlank(agentStatus.getSkill())) {
                skillList.add(agentStatus.getSkill());
            }
        }
        if (skillList.size() > 0) {
            List<Organ> organList = organRes.findAll(skillList);
            for (AgentStatus agentStatus : agentStatusList) {
                if (!StringUtils.isBlank(agentStatus.getSkill())) {
                    for (Organ organ : organList) {
                        if (agentStatus.getSkill().equals(organ.getId())) {
                            agentStatus.setSkillname(organ.getName());
                            break;
                        }
                    }
                }
            }
        }
        map.put("agentStatusList", agentStatusList);
        return request(super.createAppsTempletResponse("/apps/service/agent/index"));
    }

    @RequestMapping("/agent/offline")
    @Menu(type = "service", subtype = "offline", admin = true)
    public ModelAndView offline(ModelMap map, HttpServletRequest request, @Valid String id) {

        AgentStatus agentStatus = agentStatusRepository.findByIdAndOrgi(id, super.getOrgi(request));
        if (agentStatus != null) {
            agentStatusRepository.delete(agentStatus);
        }
        CacheHelper.getAgentStatusCacheBean().delete(agentStatus.getAgentno(), super.getOrgi(request));
        ;
        AutomaticServiceDist.publishMessage(super.getOrgi(request), "agent", "offline", super.getUser(request).getId());


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
        Page<User> userList = null;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            userList = userRes.findByOrganInAndAgentAndDatastatus(organIdList, true, false, new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        } else {
            userList = userRes.findByOrgiAndAgentAndDatastatus(super.getOrgi(request), true, false, new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        }
        for (User user : userList.getContent()) {
            if (CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), super.getOrgi(request)) != null) {
                user.setOnline(true);
            }
        }
        map.put("userList", userList);
        return request(super.createAppsTempletResponse("/apps/service/user/index"));
    }

    /**
     * 管理员坐席
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping("/adminagent/index")
    @Menu(type = "service", subtype = "adminagentlist", admin = true)
    public ModelAndView adminagent(ModelMap map, HttpServletRequest request) {
        Page<User> userList = userRes.findByOrgidAndAgentAndDatastatusAndUsertype(super.getOrgid(request), true, false, "0", new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        for (User user : userList.getContent()) {
            if (CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), super.getOrgi(request)) != null) {
                user.setOnline(true);
            }
        }
        map.put("userList", userList);
        return request(super.createAppsTempletResponse("/apps/service/adminagent/index"));
    }

    @RequestMapping("/leavemsg/index")
    @Menu(type = "service", subtype = "leavemsg", admin = true)
    public ModelAndView leavemsg(ModelMap map, HttpServletRequest request) {
        Page<LeaveMsg> leaveMsgList = leaveMsgRes.findByOrgi(super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        map.put("leaveMsgList", leaveMsgList);
        return request(super.createAppsTempletResponse("/apps/service/leavemsg/index"));
    }

    @RequestMapping("/leavemsg/delete")
    @Menu(type = "service", subtype = "leavemsg", admin = true)
    public ModelAndView leavemsg(ModelMap map, HttpServletRequest request, @Valid String id) {
        if (!StringUtils.isBlank(id)) {
            leaveMsgRes.delete(id);
        }
        return request(super.createRequestPageTempletResponse("redirect:/service/leavemsg/index.html"));
    }
}
