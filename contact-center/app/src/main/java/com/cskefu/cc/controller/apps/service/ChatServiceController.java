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

import com.cskefu.cc.acd.ACDAgentService;
import com.cskefu.cc.acd.ACDVisitorDispatcher;
import com.cskefu.cc.acd.basic.ACDComposeContext;
import com.cskefu.cc.acd.basic.ACDMessageHelper;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.*;
import com.cskefu.cc.peer.PeerSyncIM;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.*;
import com.cskefu.cc.socketio.message.Message;
import com.cskefu.cc.util.IP;
import com.cskefu.cc.util.IPTools;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    private LeaveMsgRepository leaveMsgRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private Cache cache;

    @Autowired
    @Lazy
    private PeerSyncIM peerSyncIM;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @Autowired
    private LeaveMsgProxy leaveMsgProxy;

    @RequestMapping("/history/index")
    @Menu(type = "service", subtype = "history", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, final String username, final String channel, final String servicetype, final String allocation, final String servicetimetype, final String begin, final String end) {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        Page<AgentService> page = agentServiceRes.findAll((root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            Expression<String> exp = root.<String>get("skill");
            list.add(exp.in(organs.keySet()));
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
        }, PageRequest.of(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        map.put("agentServiceList", page);
        map.put("username", username);
        map.put("channel", channel);
        map.put("servicetype", servicetype);
        map.put("servicetimetype", servicetimetype);
        map.put("allocation", allocation);
        map.put("begin", begin);
        map.put("end", end);
        map.put("deptlist", organs.values());
        map.put("userlist", userProxy.findUserInOrgans(organs.keySet()));

        return request(super.createView("/apps/service/history/index"));
    }

    @RequestMapping("/current/index")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView current(ModelMap map, HttpServletRequest request) {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        map.put(
                "agentServiceList", agentServiceRes.findByStatusAndAgentskillIn(
                        MainContext.AgentUserStatusEnum.INSERVICE.toString(),
                        organs.keySet(),
                        PageRequest.of(
                                super.getP(request),
                                super.getPs(request), Direction.DESC,
                                "createtime")));

        return request(super.createView("/apps/service/current/index"));
    }

    @RequestMapping("/current/trans")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView trans(ModelMap map, HttpServletRequest request, @Valid String id) {
        Organ targetOrgan = super.getOrgan(request);
        Map<String, Organ> ownOrgans = organProxy.findAllOrganByParent(targetOrgan);

        if (StringUtils.isNotBlank(id)) {
            AgentService agentService = agentServiceRes.findById(id).orElse(null);
            List<Organ> skillGroups = organRes.findByIdInAndSkill(ownOrgans.keySet(), true);
            Set<String> organs = ownOrgans.keySet();
            String currentOrgan = agentService.getSkill();

            if (StringUtils.isBlank(currentOrgan)) {
                if (!skillGroups.isEmpty()) {
                    currentOrgan = skillGroups.get(0).getId();
                }
            }
            final Map<String, AgentStatus> agentStatusMap = cache.findAllReadyAgentStatus();
            List<String> usersids = new ArrayList<>();
            for (final String o : agentStatusMap.keySet()) {
                if (!StringUtils.equals(o, agentService.getAgentno())) {
                    usersids.add(o);
                }
            }
            List<User> userList = userRes.findAllById(usersids);
            for (User user : userList) {
                user.setAgentStatus(cache.findOneAgentStatusByAgentno(user.getId()));
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

        return request(super.createView("/apps/service/current/transfer"));
    }

    @RequestMapping(value = "/transfer/save")
    @Menu(type = "apps", subtype = "transfersave")
    public ModelAndView transfersave(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String agentno, @Valid String memo) throws CharacterCodingException {
        if (StringUtils.isNotBlank(id)) {
            AgentService agentService = agentServiceRes.findById(id).orElse(null);
            final User targetAgent = userRes.findById(agentno).orElse(null);
            AgentUser agentUser = null;
            Optional<AgentUser> agentUserOpt = cache.findOneAgentUserByUserId(
                    agentService.getUserid());
            if (agentUserOpt.isPresent()) {
                agentUser = agentUserOpt.get();
            }

            if (agentUser != null) {
                agentUser.setAgentno(agentno);
                agentUser.setAgentname(targetAgent.getUname());
                agentUserRes.save(agentUser);
                if (MainContext.AgentUserStatusEnum.INSERVICE.toString().equals(
                        agentUser.getStatus())) {
                    // 转接 ， 发送消息给 目标坐席
                    AgentStatus agentStatus = cache.findOneAgentStatusByAgentno(
                            super.getUser(request).getId());

                    if (agentStatus != null) {
                        agentUserProxy.updateAgentStatus(agentStatus);
                    }

                    AgentStatus transAgentStatus = cache.findOneAgentStatusByAgentno(
                            agentno);
                    if (transAgentStatus != null) {
                        agentUserProxy.updateAgentStatus(transAgentStatus);
                        agentService.setAgentno(agentno);
                        agentService.setAgentusername(transAgentStatus.getUsername());
                    }
                    // 转接坐席提示消息
                    try {
                        Message outMessage = new Message();
                        outMessage.setMessage(
                                acdMessageHelper.getSuccessMessage(agentService, agentUser.getChanneltype()));
                        outMessage.setMessageType(MainContext.MediaType.TEXT.toString());
                        outMessage.setCalltype(MainContext.CallType.IN.toString());
                        outMessage.setCreatetime(MainUtils.dateFormate.format(new Date()));
                        outMessage.setAgentUser(agentUser);
                        outMessage.setAgentService(agentService);

                        if (org.apache.commons.lang3.StringUtils.isNotBlank(agentUser.getUserid())) {
                            peerSyncIM.send(
                                    MainContext.ReceiverType.VISITOR,
                                    MainContext.ChannelType.toValue(agentUser.getChanneltype()),
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
                agentUser = agentUserRes.findById(agentService.getAgentuserid()).orElse(null);
                if (agentUser != null) {
                    agentUser.setAgentno(agentno);
                    agentUser.setAgentname(targetAgent.getUname());
                    agentUserRes.save(agentUser);
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

        return request(super.createView("redirect:/service/current/index.html"));
    }

    @RequestMapping("/current/end")
    @Menu(type = "service", subtype = "current", admin = true)
    public ModelAndView end(ModelMap map, HttpServletRequest request, @Valid String id) throws Exception {
        if (StringUtils.isNotBlank(id)) {
            AgentService agentService = agentServiceRes.findById(id).orElse(null);
            if (agentService != null) {
                User user = super.getUser(request);
                AgentUser agentUser = agentUserRes.findById(agentService.getAgentuserid()).orElse(null);
                if (agentUser != null) {
                    acdAgentService.finishAgentUser(agentUser);
                }
                agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                agentServiceRes.save(agentService);
            }
        }
        return request(super.createView("redirect:/service/current/index.html"));
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
            AgentService agentService = agentServiceRes.findById(id).orElse(null);
            if (agentService != null) {
                final User user = super.getUser(request);
                if (StringUtils.isBlank(agentService.getAgentno())) {

                    // 将AiUser替换为OnlineUser
                    // TODO #153 https://gitlab.chatopera.com/chatopera/cosinee/issues/153
                    //  此处可能会有逻辑问题，从而导致BUG
                    // AiUser 定义参考
                    // https://gitlab.chatopera.com/chatopera/cosinee.w4l/blob/2ea2ad5cad92d2d9f4ceb88e9608c7019495ccf5/contact-center/app/src/main/java/com/chatopera/cc/app/model/AiUser.java
                    // 需要做更多测试
                    PassportWebIMUser passportWebIMUser = cache.findOneOnlineUserByUserId(
                            agentService.getUserid());

                    if (passportWebIMUser != null) {
                        IP ipdata = IPTools.getInstance().findGeography(passportWebIMUser.getIp());
                        acdVisitorDispatcher.enqueue(ACDMessageHelper.getWebIMComposeContext(
                                passportWebIMUser.getUserid(),
                                passportWebIMUser.getUsername(),
                                agentService.getSessionid(),
                                agentService.getAppid(),
                                agentService.getIpaddr(),
                                agentService.getOsname(),
                                agentService.getBrowser(),
                                "",
                                ipdata,
                                agentService.getChanneltype(),
                                null, // 此处绑定坐席，不指定技能组
                                user.getId(),
                                null,
                                null,
                                agentService.getContactsid(),
                                passportWebIMUser.getOwner(),
                                true,
                                MainContext.ChatInitiatorType.AGENT.toString()));
                    }
                }
            }
        }
        return request(super.createView("redirect:/service/current/index.html"));
    }


    @RequestMapping("/quene/index")
    @Menu(type = "service", subtype = "filter", admin = true)
    public ModelAndView quene(ModelMap map, HttpServletRequest request) {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        Page<AgentUser> agentUserList = agentUserRes.findByStatusAndSkillIn(MainContext.AgentUserStatusEnum.INQUENE.toString(), organs.keySet(),
                PageRequest.of(super.getP(request), super.getPs(request), Direction.DESC, "createtime"));
        List<String> skillGroups = new ArrayList<>();
        for (AgentUser agentUser : agentUserList.getContent()) {
            agentUser.setWaittingtime((int) (System.currentTimeMillis() - agentUser.getCreatetime().getTime()));
            if (StringUtils.isNotBlank(agentUser.getSkill())) {
                skillGroups.add(agentUser.getSkill());
            }
        }
        if (skillGroups.size() > 0) {
            List<Organ> organList = organRes.findAllById(skillGroups);
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

        return request(super.createView("/apps/service/quene/index"));
    }

    @RequestMapping("/quene/transfer")
    @Menu(type = "service", subtype = "quenetransfer", admin = true)
    public ModelAndView transfer(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String skillid) {

        Organ targetOrgan = super.getOrgan(request);
        Map<String, Organ> ownOrgans = organProxy.findAllOrganByParent(targetOrgan);

        if (StringUtils.isNotBlank(id)) {
            List<Organ> skillGroups = organRes.findByIdInAndSkill(ownOrgans.keySet(), true);
            Set<String> organs = ownOrgans.keySet();
            String currentOrgan = organs.size() > 0 ? (new ArrayList<>(organs)).get(0) : null;

            if (StringUtils.isBlank(currentOrgan)) {
                if (!skillGroups.isEmpty()) {
                    currentOrgan = skillGroups.get(0).getId();
                }
            }
            List<AgentStatus> agentStatusList = cache.getAgentStatusBySkill(null);
            List<String> usersids = new ArrayList<>();
            if (!agentStatusList.isEmpty()) {
                for (AgentStatus agentStatus : agentStatusList) {
                    if (agentStatus != null) {
                        usersids.add(agentStatus.getAgentno());
                    }
                }
            }
            List<User> userList = userRes.findAllById(usersids);
            for (User user : userList) {
                user.setAgentStatus(cache.findOneAgentStatusByAgentno(user.getId()));
                userProxy.attachOrgansPropertiesForUser(user);
            }
            map.put("id", id);
            map.put("skillid", skillid);
            map.addAttribute("userList", userList);
            map.addAttribute("skillGroups", skillGroups);
            map.addAttribute("currentorgan", currentOrgan);
        }
        return request(super.createView("/apps/service/quene/transfer"));
    }

    @RequestMapping("/quene/transfer/save")
    @Menu(type = "service", subtype = "quenetransfer", admin = true)
    public ModelAndView transferSave(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String skillid) {
        AgentUser agentUser = agentUserRes.findById(id).orElse(null);
        if (agentUser != null && agentUser.getStatus().equals(MainContext.AgentUserStatusEnum.INQUENE.toString())) {
            agentUser.setAgentno(null);
            agentUser.setSkill(skillid);
            agentUserRes.save(agentUser);
            ACDComposeContext ctx = acdMessageHelper.getComposeContextWithAgentUser(
                    agentUser, false, MainContext.ChatInitiatorType.USER.toString());
            acdVisitorDispatcher.enqueue(ctx);
        }
        return request(super.createView("redirect:/service/quene/index.html"));
    }

    @RequestMapping("/quene/invite")
    @Menu(type = "service", subtype = "invite", admin = true)
    public ModelAndView invite(ModelMap map, HttpServletRequest request, @Valid String id) throws Exception {
        final User logined = super.getUser(request);
        AgentUser agentUser = agentUserRes.findById(id).orElse(null);
        if (agentUser != null && agentUser.getStatus().equals(MainContext.AgentUserStatusEnum.INQUENE.toString())) {
            acdAgentService.assignVisitorAsInvite(logined.getId(), agentUser);
        }
        return request(super.createView("redirect:/service/quene/index.html"));
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
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        final Map<String, AgentStatus> ass = cache.findAllAgentStatus();
        List<AgentStatus> lis = new ArrayList<>();
        List<User> users = userProxy.findUserInOrgans(organs.keySet());
        if (users != null) {
            for (User us : users) {
                if (ass.containsKey(us.getId())) {
                    lis.add(ass.get(us.getId()));
                }
            }
        }
        map.put("agentStatusList", lis);
        return request(super.createView("/apps/service/agent/index"));
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

        AgentStatus agentStatus = agentStatusRepository.findById(id).orElse(null);
        if (agentStatus != null) {
            agentStatusRepository.delete(agentStatus);
        }
        cache.deleteAgentStatusByAgentno(agentStatus.getAgentno());

        agentStatusProxy.broadcastAgentsStatus("agent", "offline", super.getUser(request).getId());

        return request(super.createView("redirect:/service/agent/index.html"));
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
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        Page<User> userList = userProxy.findUserInOrgans(organs.keySet(), PageRequest.of(super.getP(request), super.getPs(request),
                Direction.DESC, "createtime"));
        Map<String, Boolean> onlines = new HashMap<>();
        if (userList != null) {
            for (User user : userList.getContent()) {
                if (cache.findOneAgentStatusByAgentno(user.getId()) != null) {
                    onlines.put(user.getId(), true);
                } else {
                    onlines.put(user.getId(), false);
                }
            }
        }

        map.put("userList", userList);
        map.put("onlines", onlines);
        return request(super.createView("/apps/service/user/index"));
    }

    @RequestMapping("/leavemsg/index")
    @Menu(type = "service", subtype = "leavemsg", admin = true)
    public ModelAndView leavemsg(ModelMap map, HttpServletRequest request) {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);

        Page<LeaveMsg> leaveMsgs = leaveMsgRes.findBySkill(organs.keySet(), PageRequest.of(super.getP(request), super.getPs(request),
                Direction.DESC, "createtime"));
        logger.info("[leavemsg] current organ {}, find message size {}", currentOrgan.getId(), leaveMsgs.getSize());
        for (final LeaveMsg l : leaveMsgs) {
            leaveMsgProxy.resolveChannelBySnsid(l);
        }

        map.put("leaveMsgList", leaveMsgs);
        return request(super.createView("/apps/service/leavemsg/index"));
    }

    @RequestMapping("/leavemsg/delete")
    @Menu(type = "service", subtype = "leavemsg", admin = true)
    public ModelAndView leavemsg(ModelMap map, HttpServletRequest request, @Valid String id) {
        if (StringUtils.isNotBlank(id)) {
            leaveMsgRes.deleteById(id);
        }
        return request(super.createView("redirect:/service/leavemsg/index"));
    }
}
