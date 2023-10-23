/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.cskefu.cc.controller.apps;

import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.acd.ACDAgentService;
import com.cskefu.cc.acd.basic.ACDMessageHelper;
import com.cskefu.cc.activemq.BrokerPublisher;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.exception.CSKefuException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.peer.PeerSyncIM;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.*;
import com.cskefu.cc.socketio.message.Message;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/apps/cca")
public class AgentAuditController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(AgentAuditController.class);

    @Autowired
    private AgentUserProxy agentUserProxy;

    @Autowired
    private ACDMessageHelper acdMessageHelper;

    @Autowired
    private AgentUserRepository agentUserRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    @Autowired
    private ServiceSummaryRepository serviceSummaryRes;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private PassportWebIMUserRepository onlineUserRes;

    @Autowired
    private TagRepository tagRes;

    @Autowired
    private Cache cache;

    @Autowired
    @Lazy
    private PeerSyncIM peerSyncIM;

    @Autowired
    private TagRelationRepository tagRelationRes;

    @Autowired
    private BlackEntityProxy blackEntityProxy;

    @Autowired
    private BrokerPublisher brokerPublisher;

    @Autowired
    private AgentServiceProxy agentServiceProxy;

    @Autowired
    private ACDAgentService acdAgentService;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private ChatbotRepository chatbotRes;

    @RequestMapping(value = "/index")
    @Menu(type = "cca", subtype = "cca", access = true)
    public ModelAndView index(
            ModelMap map,
            HttpServletRequest request,
            @Valid final String skill,
            @Valid final String agentno,
            @Valid String sort
    ) {
        final User logined = super.getUser(request);
        logger.info("[index] skill {}, agentno {}, logined {}", skill, agentno, logined.getId());

        Map<String, Organ> organs = organProxy.findAllOrganByParent(super.getOrgan(request));

        ModelAndView view = request(super.createView("/apps/cca/index"));
        Sort defaultSort = null;

        if (StringUtils.isNotBlank(sort)) {
            List<Sort.Order> criterias = new ArrayList<>();
            if (sort.equals("lastmessage")) {
                criterias.add(new Sort.Order(Sort.Direction.DESC, "status"));
                criterias.add(new Sort.Order(Sort.Direction.DESC, "lastmessage"));
            } else if (sort.equals("logintime")) {
                criterias.add(new Sort.Order(Sort.Direction.DESC, "status"));
                criterias.add(new Sort.Order(Sort.Direction.DESC, "createtime"));
            } else if (sort.equals("default")) {
                defaultSort = Sort.by(Sort.Direction.DESC, "status");
            }
            if (criterias.size() > 0) {
                defaultSort = Sort.by(criterias);
                map.addAttribute("sort", sort);
            }
        } else {
            defaultSort = Sort.by(Sort.Direction.DESC, "status");
        }

        // 坐席对话列表
        List<AgentUser> agentUsers = new ArrayList<>();

        if (StringUtils.isBlank(skill) && StringUtils.isBlank(agentno)) {
            if (organs.size() > 0) {
                agentUsers = agentUserRes.findByStatusAndSkillInAndAgentnoIsNotAndChatbotopsIsFalse(MainContext.AgentUserStatusEnum.INSERVICE.toString(), organs.keySet(), logined.getId(), defaultSort);
            }
        } else if (StringUtils.isNotBlank(skill) && StringUtils.isNotBlank(agentno)) {
            view.addObject("skill", skill);
            view.addObject("agentno", agentno);
            agentUsers = agentUserRes.findByStatusAndSkillAndAgentno(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else if (StringUtils.isNotBlank(skill)) {
            view.addObject("skill", skill);
            agentUsers = agentUserRes.findByStatusAndSkillAndAgentnoIsNot(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else {
            // agent is not Blank
            view.addObject("agentno", agentno);
            agentUsers = agentUserRes.findByStatusAndAgentno(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentno, defaultSort);
        }

        logger.info("[index] agent users size: {}", agentUsers.size());

        if (agentUsers.size() > 0) {
            view.addObject("agentUserList", agentUsers);

            /**
             * 当前对话
             */
            final AgentUser currentAgentUser = agentUsers.get(0);
            agentServiceProxy.bundleDialogRequiredDataInView(view, map, currentAgentUser, logined);
        }

        // 查询所有技能组
        List<Organ> skills = organRes.findBySkill(true);
        List<User> agents = userRes.findByAgentAndDatastatusAndIdIsNot(true, false, logined.getId());

        view.addObject("skillGroups", skills.stream().filter(s -> organs.containsKey(s.getId())).collect(Collectors.toList()));
        view.addObject("agentList", agents);

        return view;
    }

    @RequestMapping("/query")
    @Menu(type = "apps", subtype = "cca")
    public ModelAndView query(HttpServletRequest request, String skill, String agentno) {
        ModelAndView view = request(super.createView("/apps/cca/chatusers"));

        final User logined = super.getUser(request);

        Sort defaultSort = Sort.by(Sort.Direction.DESC, "status");

        // 坐席对话列表
        List<AgentUser> agentUsers;

        if (StringUtils.isBlank(skill) && StringUtils.isBlank(agentno)) {
            agentUsers = agentUserRes.findByStatusAndAgentnoIsNot(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), logined.getId(), defaultSort);
        } else if (StringUtils.isNotBlank(skill) && StringUtils.isNotBlank(agentno)) {
            agentUsers = agentUserRes.findByStatusAndSkillAndAgentno(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else if (StringUtils.isNotBlank(skill)) {
            agentUsers = agentUserRes.findByStatusAndSkillAndAgentnoIsNot(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), skill, agentno, defaultSort);
        } else {
            // agent is not Blank
            agentUsers = agentUserRes.findByStatusAndAgentno(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentno, defaultSort);
        }

        view.addObject("agentUserList", agentUsers);

        return view;
    }

    @RequestMapping("/agentusers")
    @Menu(type = "apps", subtype = "cca")
    public ModelAndView agentusers(HttpServletRequest request, String userid) {
        ModelAndView view = request(super.createView("/apps/cca/agentusers"));
        User logined = super.getUser(request);
        Sort defaultSort = Sort.by(Sort.Direction.DESC, "status");
        view.addObject(
                "agentUserList", agentUserRes.findByStatusAndAgentnoIsNot(
                        MainContext.AgentUserStatusEnum.INSERVICE.toString(), logined.getId(), defaultSort));
        List<AgentUser> agentUserList = agentUserRes.findByUserid(userid);
        view.addObject(
                "curagentuser", agentUserList != null && agentUserList.size() > 0 ? agentUserList.get(0) : null);

        return view;
    }

    @RequestMapping("/agentuser")
    @Menu(type = "apps", subtype = "cca")
    public ModelAndView agentuser(
            ModelMap map,
            HttpServletRequest request,
            String id,
            String channel
    ) throws IOException {
        String mainagentuser = "/apps/cca/mainagentuser";
        if (channel.equals("phone")) {
            mainagentuser = "/apps/cca/mainagentuser_callout";
        }
        ModelAndView view = request(super.createView(mainagentuser));
        final User logined = super.getUser(request);
        AgentUser agentUser = agentUserRes.findById(id).orElse(null);

        if (agentUser != null) {
            view.addObject("curagentuser", agentUser);

            Chatbot c = chatbotRes.findBySnsAccountIdentifier(agentUser.getAppid());
            if (c != null) {
                view.addObject("ccaAisuggest", c.isAisuggest());
            }

            view.addObject("inviteData", OnlineUserProxy.consult(agentUser.getAppid()));

            AgentUserTask agentUserTask = agentUserTaskRes.findById(id).orElse(null);
            agentUserTask.setTokenum(0);
            agentUserTaskRes.save(agentUserTask);

            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                List<AgentServiceSummary> summarizes = this.serviceSummaryRes.findByAgentserviceid(
                        agentUser.getAgentserviceid());
                if (summarizes.size() > 0) {
                    view.addObject("summary", summarizes.get(0));
                }
            }

            view.addObject(
                    "agentUserMessageList",
                    this.chatMessageRepository.findByUsession(agentUser.getUserid(),
                            PageRequest.of(0, 20, Sort.Direction.DESC,"updatetime")
                    )
            );
            AgentService agentService = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                agentService = this.agentServiceRes.findById(agentUser.getAgentserviceid()).orElse(null);
                view.addObject("curAgentService", agentService);
                if (agentService != null) {
                    /**
                     * 获取关联数据
                     */
                    agentServiceProxy.processRelaData(logined.getId(), agentService, map);
                }
            }
            if (MainContext.ChannelType.WEBIM.toString().equals(agentUser.getChanneltype())) {
                PassportWebIMUser passportWebIMUser = onlineUserRes.findById(agentUser.getUserid()).orElse(null);
                if (passportWebIMUser != null) {
                    if (passportWebIMUser.getLogintime() != null) {
                        if (MainContext.OnlineUserStatusEnum.OFFLINE.toString().equals(passportWebIMUser.getStatus())) {
                            passportWebIMUser.setBetweentime(
                                    (int) (passportWebIMUser.getUpdatetime().getTime() - passportWebIMUser.getLogintime().getTime()));
                        } else {
                            passportWebIMUser.setBetweentime(
                                    (int) (System.currentTimeMillis() - passportWebIMUser.getLogintime().getTime()));
                        }
                    }
                    view.addObject("onlineUser", passportWebIMUser);
                }
            }
            view.addObject("serviceCount", this.agentServiceRes
                    .countByUseridAndStatus(agentUser
                                    .getUserid(),
                            MainContext.AgentUserStatusEnum.END
                                    .toString()));
            view.addObject("tagRelationList", tagRelationRes.findByUserid(agentUser.getUserid()));

            AgentService service = agentServiceRes.findById(agentUser.getAgentserviceid()).orElse(null);
            if (service != null) {
                view.addObject("tags", tagRes.findByTagtypeAndSkill(MainContext.ModelType.USER.toString(), service.getSkill()));
            }
        }
        return view;
    }


    /**
     * 坐席转接窗口
     *
     * @param map
     * @param request
     * @param userid
     * @param agentserviceid
     * @param agentuserid
     * @return
     */
    @RequestMapping(value = "/transfer")
    @Menu(type = "apps", subtype = "transfer")
    public ModelAndView transfer(
            ModelMap map,
            final HttpServletRequest request,
            final @Valid String userid,
            final @Valid String agentserviceid,
            final @Valid String agentnoid,
            final @Valid String agentuserid
    ) {
        logger.info("[transfer] userId {}, agentUser {}", userid, agentuserid);
        Organ targetOrgan = super.getOrgan(request);
        Map<String, Organ> ownOrgans = organProxy.findAllOrganByParent(targetOrgan);
        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(agentuserid)) {
            // 列出所有技能组
            List<Organ> skillGroups = organRes.findByIdInAndSkill(ownOrgans.keySet(), true);

            // 选择当前用户的默认技能组
            AgentService agentService = agentServiceRes.findById(agentserviceid).orElse(null);

            String currentOrgan = agentService.getSkill();

            if (StringUtils.isBlank(currentOrgan)) {
                if (!skillGroups.isEmpty()) {
                    currentOrgan = skillGroups.get(0).getId();
                }
            }

            // 列出所有在线的坐席，排除本身
            List<String> userids = new ArrayList<>();
            final Map<String, AgentStatus> agentStatusMap = cache.findAllReadyAgentStatus();

            for (final String o : agentStatusMap.keySet()) {
                if (!StringUtils.equals(o, agentnoid)) {
                    userids.add(o);
                }
            }

            final List<User> userList = userRes.findAllById(userids);
            for (final User o : userList) {
                o.setAgentStatus(agentStatusMap.get(o.getId()));
                // find user's skills
                userProxy.attachOrgansPropertiesForUser(o);
            }

            map.addAttribute("userList", userList);
            map.addAttribute("userid", userid);
            map.addAttribute("agentserviceid", agentserviceid);
            map.addAttribute("agentuserid", agentuserid);
            map.addAttribute("agentno", agentnoid);
            map.addAttribute("skillGroups", skillGroups);
            map.addAttribute("agentservice", this.agentServiceRes.findById(agentserviceid).orElse(null));
            map.addAttribute("currentorgan", currentOrgan);
        }

        return request(super.createView("/apps/cca/transfer"));
    }


    /**
     * 查找一个组织机构中的在线坐席
     *
     * @param map
     * @param request
     * @param organ
     * @return
     */
    @RequestMapping(value = "/transfer/agent")
    @Menu(type = "apps", subtype = "transferagent")
    public ModelAndView transferagent(
            ModelMap map,
            HttpServletRequest request,
            @Valid String agentid,
            @Valid String organ
    ) {
        if (StringUtils.isNotBlank(organ)) {
            List<String> userids = new ArrayList<>();

            final Map<String, AgentStatus> agentStatusMap = cache.findAllReadyAgentStatus();

            for (final String o : agentStatusMap.keySet()) {
                if (!StringUtils.equals(o, agentid)) {
                    userids.add(o);
                }
            }

            final List<User> userList = userRes.findAllById(userids);
            for (final User o : userList) {
                o.setAgentStatus(agentStatusMap.get(o.getId()));
                // find user's skills
                userProxy.attachOrgansPropertiesForUser(o);
            }
            map.addAttribute("userList", userList);
            map.addAttribute("currentorgan", organ);
        }
        return request(super.createView("/apps/cca/transferagentlist"));
    }

    /**
     * 执行坐席转接
     *
     * @param map
     * @param request
     * @param userid
     * @param agentserviceid
     * @param agentuserid
     * @param agentno
     * @param memo
     * @return
     */
    @RequestMapping(value = "/transfer/save")
    @Menu(type = "apps", subtype = "transfersave")
    public ModelAndView transfersave(
            final ModelMap map, HttpServletRequest request,
            @Valid final String userid,         // 访客ID
            @Valid final String agentserviceid, // 服务记录ID
            @Valid final String agentuserid,    // 坐席访客ID
            @Valid final String currentAgentnoid,
            @Valid final String agentno,   // 会话转接给下一个坐席
            @Valid final String memo
    ) throws CSKefuException {
        final String currentAgentno = currentAgentnoid; // 当前会话坐席的agentno

        if (StringUtils.isNotBlank(userid) &&
                StringUtils.isNotBlank(agentuserid) &&
                StringUtils.isNotBlank(agentno)) {
            final User targetAgent = userRes.findById(agentno).orElse(null);
            final AgentService agentService = agentServiceRes.findById(agentserviceid).orElse(null);
            /**
             * 更新AgentUser
             */
            final AgentUser agentUser = agentUserProxy.resolveAgentUser(userid, agentuserid);
            agentUser.setAgentno(agentno);
            agentUser.setAgentname(targetAgent.getUname());
            agentUserRes.save(agentUser);

            /**
             * 坐席状态
             */
            // 转接目标坐席
            final AgentStatus transAgentStatus = cache.findOneAgentStatusByAgentno(agentno);

            // 转接源坐席
            final AgentStatus currentAgentStatus = cache.findOneAgentStatusByAgentno(currentAgentno);

            if (StringUtils.equals(
                    MainContext.AgentUserStatusEnum.INSERVICE.toString(), agentUser.getStatus())) { //转接 ， 发送消息给 目标坐席

                // 更新当前坐席的服务访客列表
                if (currentAgentStatus != null) {
                    cache.deleteOnlineUserIdFromAgentStatusByUseridAndAgentno(userid, currentAgentno);
                    agentUserProxy.updateAgentStatus(currentAgentStatus);
                }

                if (transAgentStatus != null) {
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

                    if (StringUtils.isNotBlank(agentUser.getUserid())) {
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
        return request(super.createView("redirect:/apps/cca/index.html"));

    }


    /**
     * 结束对话
     * 如果当前对话属于登录用户或登录用户为超级用户，则可以结束这个对话
     *
     * @param request
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping({"/end"})
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView end(HttpServletRequest request, @Valid String id) {
        final User logined = super.getUser(request);

        final AgentUser agentUser = agentUserRes.findById(id).orElse(null);

        if (agentUser != null) {
            if ((StringUtils.equals(
                    logined.getId(), agentUser.getAgentno()) || logined.isAdmin())) {
                // 删除访客-坐席关联关系，包括缓存
                try {
                    acdAgentService.finishAgentUser(agentUser);
                } catch (CSKefuException e) {
                    // 未能删除成功
                    logger.error("[end]", e);
                }
            } else {
                logger.info("[end] Permission not fulfill.");
            }
        }

        return request(super.createView("redirect:/apps/cca/index.html"));
    }

    @RequestMapping({"/blacklist/add"})
    @Menu(type = "apps", subtype = "blacklist")
    public ModelAndView blacklistadd(ModelMap map, HttpServletRequest request, @Valid String agentuserid, @Valid String agentserviceid, @Valid String userid)
            throws Exception {
        map.addAttribute("agentuserid", agentuserid);
        map.addAttribute("agentserviceid", agentserviceid);
        map.addAttribute("userid", userid);
        map.addAttribute("agentUser", agentUserRes.findById(userid).orElse(null));
        return request(super.createView("/apps/cca/blacklistadd"));
    }

    @RequestMapping({"/blacklist/save"})
    @Menu(type = "apps", subtype = "blacklist")
    public ModelAndView blacklist(
            HttpServletRequest request,
            @Valid String agentuserid,
            @Valid String agentserviceid,
            @Valid String userid,
            @Valid BlackEntity blackEntity)
            throws Exception {
        logger.info("[blacklist] userid {}", userid);
        final User logined = super.getUser(request);

        if (StringUtils.isBlank(userid)) {
            throw new CSKefuException("Invalid userid");
        }
        /**
         * 添加黑名单
         * 一定时间后触发函数
         */
        JSONObject payload = new JSONObject();

        int timeSeconds = blackEntity.getControltime() * 3600;
        payload.put("userId", userid);
        ModelAndView view = end(request, agentuserid);
        // 更新或创建黑名单
        blackEntityProxy.updateOrCreateBlackEntity(blackEntity, logined, userid, agentserviceid, agentuserid);

        // 创建定时任务 取消拉黑
        brokerPublisher.send(
                Constants.WEBIM_SOCKETIO_ONLINE_USER_BLACKLIST, payload.toJSONString(), false, timeSeconds);

        return view;
    }

}
