/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2020 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.controller.apps;

import com.alibaba.fastjson.JSONObject;
import com.chatopera.cc.acd.ACDPolicyService;
import com.chatopera.cc.acd.ACDWorkMonitor;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.blob.JpaBlobHelper;
import com.chatopera.cc.persistence.es.ContactsRepository;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.socketio.util.RichMediaUtils;
import com.chatopera.cc.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping("/im")
@EnableAsync
public class IMController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(IMController.class);

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Value("${uk.im.server.host}")
    private String host;

    @Value("${uk.im.server.port}")
    private Integer port;

    @Value("${cs.im.server.ssl.port}")
    private Integer sslPort;

    @Value("${web.upload-path}")
    private String path;

    @Value("${cskefu.settings.webim.visitor-separate}")
    private Boolean channelWebIMVisitorSeparate;

    @Autowired
    private StreamingFileRepository streamingFileRepository;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Autowired
    private ConsultInviteRepository inviteRepository;

    @Autowired
    private ChatMessageRepository chatMessageRes;

    @Autowired
    private AgentServiceSatisRepository agentServiceSatisRes;

    @Autowired
    private AgentServiceRepository agentServiceRepository;

    @Autowired
    private InviteRecordRepository inviteRecordRes;

    @Autowired
    private LeaveMsgRepository leaveMsgRes;

    @Autowired
    private AgentUserRepository agentUserRepository;

    @Autowired
    private AttachmentRepository attachementRes;

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private AgentUserContactsRepository agentUserContactsRes;

    @Autowired
    private SNSAccountRepository snsAccountRepository;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private UserHistoryRepository userHistoryRes;

    @Autowired
    private ChatbotRepository chatbotRes;

    @Autowired
    private Cache cache;

    @PostConstruct
    private void init() {
    }

    @RequestMapping("/{id}")
    @Menu(type = "im", subtype = "point", access = true)
    public ModelAndView loader(
            HttpServletRequest request, HttpServletResponse response,
            @PathVariable String id,
            @Valid String userid,
            @Valid String title,
            @Valid String aiid) {
        ModelAndView view = request(super.createView("/apps/im/loader"));

        view.addObject("hostname", request.getServerName());
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnablessl()) {
            view.addObject("schema", "https");
            if (request.getServerPort() == 80) {
                view.addObject("port", 443);
            } else {
                view.addObject("port", request.getServerPort());
            }
        } else {
            view.addObject("schema", super.getSchema(request));
            view.addObject("port", request.getServerPort());
        }

        view.addObject("appid", id);
        view.addObject("userid", userid);
        view.addObject("title", title);
        view.addObject("aiid", aiid);

        return view;
    }

    /**
     * 在客户或第三方网页内，写入聊天控件
     *
     * @param request
     * @param response
     * @param id
     * @param userid
     * @param title
     * @param aiid
     * @return
     */
    @RequestMapping("/point/{id}")
    @Menu(type = "im", subtype = "point", access = true)
    public ModelAndView point(
            HttpServletRequest request, HttpServletResponse response,
            @PathVariable String id,
            @Valid String userid,
            @Valid String title,
            @Valid String aiid) {
        ModelAndView view = request(super.createView("/apps/im/point"));
        view.addObject("channelVisitorSeparate", channelWebIMVisitorSeparate);

        final String sessionid = MainUtils.getContextID(request.getSession().getId());
        logger.info("[point] session snsid {}, session {}", id, sessionid);

        if (StringUtils.isNotBlank(id)) {
            Boolean webimexist = false;
            view.addObject("hostname", request.getServerName());
            logger.info("[point] new website is : {}", request.getServerName());
            SNSAccount SnsAccountList = snsAccountRes.findBySnsidAndOrgi(id, super.getUser(request).getOrgi());
            if (SnsAccountList != null) {
                webimexist = true;
            }
            view.addObject("webimexist", webimexist);

            SystemConfig systemConfig = MainUtils.getSystemConfig();
            if (systemConfig != null && systemConfig.isEnablessl()) {
                view.addObject("schema", "https");
                if (request.getServerPort() == 80) {
                    view.addObject("port", 443);
                } else {
                    view.addObject("port", request.getServerPort());
                }
            } else {
                view.addObject("schema", super.getSchema(request));
                view.addObject("port", request.getServerPort());
            }

            view.addObject("appid", id);
            view.addObject("client", MainUtils.getUUID());
            view.addObject("sessionid", sessionid);
            view.addObject("ip", MainUtils.md5(request.getRemoteAddr()));
            view.addObject("mobile", MobileDevice.isMobile(request.getHeader("User-Agent")));

            CousultInvite invite = OnlineUserProxy.consult(id, Constants.SYSTEM_ORGI);
            if (invite != null) {
                logger.info("[point] find CousultInvite {}", invite.getId());
                view.addObject("inviteData", invite);
                view.addObject("orgi", invite.getOrgi());
                view.addObject("appid", id);

                if (StringUtils.isNotBlank(aiid)) {
                    view.addObject("aiid", aiid);
                } else if (StringUtils.isNotBlank(invite.getAiid())) {
                    view.addObject("aiid", invite.getAiid());
                }

                // 记录用户行为日志
                // 每次有一个新网页加载出聊天控件，都会生成一个userHistory
                UserHistory userHistory = new UserHistory();
                String url = request.getHeader("referer");
                if (StringUtils.isNotBlank(url)) {
                    if (url.length() > 255) {
                        userHistory.setUrl(url.substring(0, 255));
                    } else {
                        userHistory.setUrl(url);
                    }
                    userHistory.setReferer(userHistory.getUrl());
                }
                userHistory.setParam(MainUtils.getParameter(request));
                userHistory.setMaintype("send");
                userHistory.setSubtype("point");
                userHistory.setName("online");
                userHistory.setAdmin(false);
                userHistory.setAccessnum(true);
                userHistory.setModel(MainContext.ChannelType.WEBIM.toString());

                final User imUser = super.getIMUser(request, userid, null);
                if (imUser != null) {
                    userHistory.setCreater(imUser.getId());
                    userHistory.setUsername(imUser.getUsername());
                    userHistory.setOrgi(Constants.SYSTEM_ORGI);
                }

                if (StringUtils.isNotBlank(title)) {
                    if (title.length() > 255) {
                        userHistory.setTitle(title.substring(0, 255));
                    } else {
                        userHistory.setTitle(title);
                    }
                }

                userHistory.setOrgi(invite.getOrgi());
                userHistory.setAppid(id);
                userHistory.setSessionid(sessionid);

                String ip = MainUtils.getIpAddr(request);
                userHistory.setHostname(ip);
                userHistory.setIp(ip);
                IP ipdata = IPTools.getInstance().findGeography(ip);
                userHistory.setCountry(ipdata.getCountry());
                userHistory.setProvince(ipdata.getProvince());
                userHistory.setCity(ipdata.getCity());
                userHistory.setIsp(ipdata.getIsp());

                BrowserClient client = MainUtils.parseClient(request);
                userHistory.setOstype(client.getOs());
                userHistory.setBrowser(client.getBrowser());
                userHistory.setMobile(MobileDevice.isMobile(request.getHeader("User-Agent")) ? "1" : "0");

                if (invite.isSkill() && invite.isConsult_skill_fixed() == false) { // 展示所有技能组
                    /***
                     * 查询 技能组 ， 缓存？
                     */
                    view.addObject("skillGroups", OnlineUserProxy.organ(Constants.SYSTEM_ORGI, ipdata, invite, true));
                    /**
                     * 查询坐席 ， 缓存？
                     */
                    view.addObject("agentList", OnlineUserProxy.agents(Constants.SYSTEM_ORGI));
                }

                view.addObject("traceid", userHistory.getId());

                // 用户的浏览历史会有很大的数据量，目前强制开启
                userHistoryRes.save(userHistory);

                /**
                 * 广告信息
                 */
                List<AdType> ads = MainUtils.getPointAdvs(MainContext.AdPosEnum.POINT.toString(),
                        invite.getConsult_skill_fixed_id(), Constants.SYSTEM_ORGI);

                if (ads.size() > 0) {
                    view.addObject(
                            "pointAds",
                            ads);
                    view.addObject(
                            "pointAd",
                            MainUtils.weitht(ads));
                } else {
                    view.addObject(
                            "pointAds",
                            null);
                    view.addObject(
                            "pointAd",
                            null);
                }

                view.addObject(
                        "inviteAd",
                        MainUtils.getPointAdv(MainContext.AdPosEnum.INVITE.toString(),
                                invite.getConsult_skill_fixed_id(), Constants.SYSTEM_ORGI));
            } else {
                logger.info("[point] invite id {}, orgi {} not found", id, Constants.SYSTEM_ORGI);
            }
        }

        return view;
    }

    private void createContacts(
            final String userid,
            final HttpServletRequest request,
            final String gid,
            final String uid,
            final String cid,
            final String sid,
            final String username,
            final String company_name,
            final String system_name) {
        if (StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(cid)) {
            Contacts data = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(uid, sid, cid, false);
            if (data == null) {
                data = new Contacts();
                data.setCreater(gid);
                data.setOrgi(Constants.SYSTEM_ORGI);
                data.setWluid(uid);
                data.setWlusername(username);
                data.setWlcid(cid);
                data.setWlcompany_name(company_name);
                data.setWlsid(sid);
                data.setWlsystem_name(system_name);
                data.setName(username + '@' + company_name);
                data.setShares("all");

                data.setPinyin(PinYinTools.getInstance().getFirstPinYin(username));
                contactsRes.save(data);
            }
        }
    }

    @ResponseBody
    @RequestMapping("/chatoperainit")
    @Menu(type = "im", subtype = "chatoperainit")
    public String chatoperaInit(
            ModelMap map,
            HttpServletRequest request,
            HttpServletResponse response,
            String userid,
            String uid,
            String username,
            String cid,
            String company_name,
            String sid,
            String system_name,
            Boolean whitelist_mode,
            @RequestParam String sessionid) throws IOException {
        ModelAndView view = request(super.createView("/apps/im/point"));
        final User logined = super.getUser(request);

        request.getSession().setAttribute("Sessionuid", uid);

        Map<String, String> sessionMessage = new HashMap<String, String>();
        sessionMessage.put("username", username);
        sessionMessage.put("cid", cid);
        sessionMessage.put("company_name", company_name);
        sessionMessage.put("sid", sid);
        sessionMessage.put("Sessionsystem_name", system_name);
        sessionMessage.put("sessionid", sessionid);
        sessionMessage.put("uid", uid);
        cache.putSystemMapByIdAndOrgi(sessionid, Constants.SYSTEM_ORGI, sessionMessage);

        OnlineUser onlineUser = onlineUserRes.findOne(userid);
        String updateusername;
        if (onlineUser != null) {
            updateusername = username + "@" + company_name;
            onlineUser.setUsername(updateusername);
            onlineUser.setUpdateuser(updateusername);
            onlineUser.setUpdatetime(new Date());
            onlineUserRes.save(onlineUser);
        }

        Contacts usc = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(uid, sid, cid, false);
        if (usc != null) {
            return "usc";
        } else {
            if (!whitelist_mode) {
                createContacts(userid,
                        request,
                        logined.getId(),
                        uid, cid, sid, username, company_name, system_name);
            }
        }

        return "ok";
    }

    @RequestMapping("/{id}/userlist")
    @Menu(type = "im", subtype = "inlist", access = true)
    public void inlist(HttpServletRequest request, HttpServletResponse response, @PathVariable String id,
                       @Valid String userid) throws IOException {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        if (StringUtils.isNotBlank(userid)) {
            BlackEntity black = cache.findOneSystemByIdAndOrgi(userid, Constants.SYSTEM_ORGI);
            if ((black != null && (black.getEndtime() == null || black.getEndtime().after(new Date())))) {
                response.getWriter().write("in");
            }
        }
    }

    /**
     * 延时获取用户端浏览器的跟踪ID
     *
     * @param request
     * @param response
     * @param orgi
     * @param appid
     * @param userid
     * @param sign
     * @return
     */
    @RequestMapping("/online")
    @Menu(type = "im", subtype = "online", access = true)
    public SseEmitter callable(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid Contacts contacts,
            final @Valid String orgi,
            final @Valid String sessionid,
            @Valid String appid,
            final @Valid String userid,
            @Valid String sign,
            final @Valid String client,
            final @Valid String title,
            final @Valid String traceid) throws InterruptedException {
        // logger.info(
        // "[online] user {}, orgi {}, traceid {}, appid {}, session {}", userid, orgi,
        // traceid, appid, sessionid);
        Optional<BlackEntity> blackOpt = cache.findOneBlackEntityByUserIdAndOrgi(userid, orgi);
        if (blackOpt.isPresent() && (blackOpt.get().getEndtime() == null || blackOpt.get().getEndtime().after(
                new Date()))) {
            logger.info("[online] online user {} is in black list.", userid);
            // 该访客被拉黑
            return null;
        }

        final SseEmitter emitter = new SseEmitter(30000L);
        if (StringUtils.isNotBlank(userid)) {
            emitter.onCompletion(new Runnable() {
                @Override
                public void run() {
                    try {
                        OnlineUserProxy.webIMClients.removeClient(userid, client, false); // 执行了 邀请/再次邀请后终端的
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            emitter.onTimeout(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (emitter != null) {
                            emitter.complete();
                        }
                        OnlineUserProxy.webIMClients.removeClient(userid, client, true); // 正常的超时断开
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            CousultInvite invite = OnlineUserProxy.consult(appid, orgi);

            // TODO 该contacts的识别并不准确，因为不能关联
            // if (invite != null && invite.isTraceuser()) {
            // contacts = OnlineUserProxy.OnlineUserProxy.processContacts(orgi, contacts,
            // appid, userid);
            // }
            //
            // if (StringUtils.isNotBlank(sign)) {
            // OnlineUserProxy.online(
            // super.getIMUser(request, sign, contacts != null ? contacts.getName() : null,
            // sessionid),
            // orgi,
            // sessionid,
            // MainContext.OnlineUserType.WEBIM.toString(),
            // request,
            // MainContext.ChannelType.WEBIM.toString(),
            // appid,
            // contacts,
            // invite);
            // END 取消关联contacts

            if (StringUtils.isNotBlank(sign)) {
                OnlineUserProxy.online(
                        super.getIMUser(request, sign, null, sessionid),
                        orgi,
                        sessionid,
                        MainContext.OnlineUserType.WEBIM.toString(),
                        request,
                        MainContext.ChannelType.WEBIM.toString(),
                        appid,
                        null,
                        invite);
            }
            OnlineUserProxy.webIMClients.putClient(userid, new WebIMClient(userid, client, emitter, traceid));
            Thread.sleep(500);
        }

        return emitter;
    }

    /**
     * 访客与客服聊天小窗口
     * <p>
     * 此处返回给访客新的页面：根据访客/坐席/机器人的情况进行判断
     * 如果此处返回的是人工服务，那么此处并不寻找服务的坐席信息，而是在返回的页面中查找
     *
     * @param map
     * @param request
     * @param response
     * @param orgi
     * @param aiid
     * @param traceid
     * @param exchange
     * @param title
     * @param url
     * @param mobile
     * @param phone
     * @param ai
     * @param client
     * @param type
     * @param appid
     * @param userid
     * @param sessionid
     * @param skill
     * @param agent
     * @param contacts
     * @param product
     * @param description
     * @param imgurl
     * @param pid
     * @param purl
     * @return
     * @throws Exception
     */
    @RequestMapping("/index")
    @Menu(type = "im", subtype = "index", access = true)
    public ModelAndView index(
            ModelMap map,
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid final String orgi,
            @Valid final String aiid,
            // @Valid String uid,
            @Valid final String traceid,
            @Valid final String exchange,
            @Valid final String title,
            @Valid final String url,
            @Valid final String mobile,
            @Valid final String phone,
            @Valid final String ai,
            @Valid final String client,
            @Valid final String type,
            @Valid final String appid,
            @Valid final String userid,
            @Valid final String sessionid,
            @Valid final String skill,
            @Valid final String agent,
            @Valid Contacts contacts,
            @Valid final String product,
            @Valid final String description,
            @Valid final String imgurl,
            @Valid final String pid,
            @Valid final String purl,
            @Valid final boolean isInvite) throws Exception {
        logger.info(
                "[index] orgi {}, skill {}, agent {}, traceid {}, isInvite {}, exchange {}", orgi, skill, agent,
                traceid, isInvite, exchange);
        Map<String, String> sessionMessageObj = cache.findOneSystemMapByIdAndOrgi(sessionid, orgi);

        map.put("pugHelper", new PugHelper());

        if (sessionMessageObj != null) {
            request.getSession().setAttribute("Sessionusername", sessionMessageObj.get("username"));
            request.getSession().setAttribute("Sessioncid", sessionMessageObj.get("cid"));
            request.getSession().setAttribute("Sessioncompany_name", sessionMessageObj.get("company_name"));
            request.getSession().setAttribute("Sessionsid", sessionMessageObj.get("sid"));
            request.getSession().setAttribute("Sessionsystem_name", sessionMessageObj.get("system_name"));
            request.getSession().setAttribute("sessionid", sessionMessageObj.get("sessionid"));
            request.getSession().setAttribute("Sessionuid", sessionMessageObj.get("uid"));
        }

        ModelAndView view = request(super.createView("/apps/im/index"));
        view.addObject("systemConfig", MainUtils.getSystemConfig());
        Optional<BlackEntity> blackOpt = cache.findOneBlackEntityByUserIdAndOrgi(userid, Constants.SYSTEM_ORGI);
        CousultInvite invite = OnlineUserProxy.consult(appid, orgi);
        if (StringUtils.isNotBlank(
                appid)
                && ((!blackOpt.isPresent())
                || (blackOpt.get().getEndtime() != null && blackOpt.get().getEndtime().before(
                new Date())))) {

            String randomUserId; // 随机生成OnlineUser的用户名，使用了浏览器指纹做唯一性KEY
            if (StringUtils.isNotBlank(userid)) {
                randomUserId = MainUtils.genIDByKey(userid);
            } else {
                randomUserId = MainUtils.genIDByKey(sessionid);
            }
            String nickname;

            if (sessionMessageObj != null) {
                nickname = ((Map) sessionMessageObj).get("username") + "@" + ((Map) sessionMessageObj).get(
                        "company_name");
            } else if (request.getSession().getAttribute("Sessionusername") != null) {
                String struname = (String) request.getSession().getAttribute("Sessionusername");
                String strcname = (String) request.getSession().getAttribute("Sessioncompany_name");
                nickname = struname + "@" + strcname;
            } else {
                nickname = "Guest_" + "@" + randomUserId;
            }

            view.addObject("nickname", nickname);

            boolean consult = true; // 是否已收集用户信息
            SessionConfig sessionConfig = acdPolicyService.initSessionConfig(skill, orgi);

            // 强制开启满意调查问卷
            sessionConfig.setSatisfaction(true);

            map.addAttribute("sessionConfig", sessionConfig);
            map.addAttribute("hostname", request.getServerName());

            String schema = super.getSchema(request);

            if (StringUtils.equals(schema, "https")) {
                map.addAttribute("port", 443);
            } else if (sslPort != null) {
                map.addAttribute("port", sslPort);
            } else {
                map.addAttribute("port", port);
            }

            map.addAttribute("appid", appid);
            map.addAttribute("userid", userid);
            map.addAttribute("schema", schema);
            map.addAttribute("sessionid", sessionid);
            map.addAttribute("isInvite", isInvite);

            view.addObject("product", product);
            view.addObject("description", description);
            view.addObject("imgurl", imgurl);
            view.addObject("pid", pid);
            view.addObject("purl", purl);

            map.addAttribute("ip", MainUtils.md5(request.getRemoteAddr()));

            if (StringUtils.isNotBlank(traceid)) {
                map.addAttribute("traceid", traceid);
            }
            if (StringUtils.isNotBlank(exchange)) {
                map.addAttribute("exchange", exchange);
            }
            if (StringUtils.isNotBlank(title)) {
                map.addAttribute("title", title);
            }
            if (StringUtils.isNotBlank(traceid)) {
                map.addAttribute("url", URLEncoder.encode(url, "utf-8"));
            }

            map.addAttribute("cskefuport", request.getServerPort());

            /**
             * 先检查 invite不为空
             */
            if (invite != null) {
                logger.info("[index] invite id {}, orgi {}", invite.getId(), invite.getOrgi());
                map.addAttribute("orgi", invite.getOrgi());
                map.addAttribute("inviteData", invite);

                if (StringUtils.isNotBlank(aiid)) {
                    map.addAttribute("aiid", aiid);
                } else if (StringUtils.isNotBlank(invite.getAiid())) {
                    map.addAttribute("aiid", invite.getAiid());
                }

                AgentReport report;
                if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 绑定技能组
                    report = acdWorkMonitor.getAgentReport(invite.getConsult_skill_fixed_id(), invite.getOrgi());
                } else {
                    report = acdWorkMonitor.getAgentReport(invite.getOrgi());
                }

                boolean isLeavemsg = false;
                if (report.getAgents() == 0 ||
                        (sessionConfig.isHourcheck() &&
                                !MainUtils.isInWorkingHours(sessionConfig.getWorkinghours()) &&
                                invite.isLeavemessage())) {
                    // 没有坐席在线，进入留言
                    isLeavemsg = true;
                    boolean isInWorkingHours = MainUtils.isInWorkingHours(sessionConfig.getWorkinghours());
                    map.addAttribute("isInWorkingHours", isInWorkingHours);
                    if (invite.isLeavemessage()) {
                        view = request(super.createView("/apps/im/leavemsg"));
                    } else {
                        view = request(super.createView("/apps/im/noagent"));
                    }
                } else if (invite.isConsult_info()) { // 启用了信息收集，从Request获取， 或从 Cookies 里去
                    // 验证 OnlineUser 信息
                    if (contacts != null && StringUtils.isNotBlank(
                            contacts.getName())) { // contacts用于传递信息，并不和 联系人表发生 关联，contacts信息传递给 Socket.IO，然后赋值给
                        // AgentUser，最终赋值给 AgentService永久存储
                        consult = true;
                        // 存入 Cookies
                        if (invite.isConsult_info_cookies()) {
                            Cookie name = new Cookie(
                                    "name", MainUtils.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
                            response.addCookie(name);
                            name.setMaxAge(3600);
                            if (StringUtils.isNotBlank(contacts.getPhone())) {
                                Cookie phonecookie = new Cookie(
                                        "phone", MainUtils.encryption(URLEncoder.encode(contacts.getPhone(), "UTF-8")));
                                phonecookie.setMaxAge(3600);
                                response.addCookie(phonecookie);
                            }
                            if (StringUtils.isNotBlank(contacts.getEmail())) {
                                Cookie email = new Cookie(
                                        "email", MainUtils.encryption(URLEncoder.encode(contacts.getEmail(), "UTF-8")));
                                email.setMaxAge(3600);
                                response.addCookie(email);
                            }

                            if (StringUtils.isNotBlank(contacts.getSkypeid())) {
                                Cookie skypeid = new Cookie(
                                        "skypeid", MainUtils.encryption(
                                        URLEncoder.encode(contacts.getSkypeid(), "UTF-8")));
                                skypeid.setMaxAge(3600);
                                response.addCookie(skypeid);
                            }

                            if (StringUtils.isNotBlank(contacts.getMemo())) {
                                Cookie memo = new Cookie(
                                        "memo", MainUtils.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
                                memo.setMaxAge(3600);
                                response.addCookie(memo);
                            }
                        }
                    } else {
                        // 从 Cookies里尝试读取
                        if (invite.isConsult_info_cookies()) {
                            Cookie[] cookies = request.getCookies();// 这样便可以获取一个cookie数组
                            contacts = new Contacts();
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    if (cookie != null && StringUtils.isNotBlank(
                                            cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
                                        if (cookie.getName().equals("name")) {
                                            contacts.setName(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("phone")) {
                                            contacts.setPhone(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("email")) {
                                            contacts.setEmail(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("memo")) {
                                            contacts.setMemo(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("skypeid")) {
                                            contacts.setSkypeid(
                                                    URLDecoder.decode(
                                                            MainUtils.decryption(cookie.getValue()),
                                                            "UTF-8"));
                                        }
                                    }
                                }
                            }
                        }
                        if (StringUtils.isBlank(contacts.getName())) {
                            consult = false;
                            view = request(super.createView("/apps/im/collecting"));
                        }
                    }
                } else {
                    // TODO 该contacts的识别并不准确，因为不能关联
                    // contacts = OnlineUserProxy.processContacts(invite.getOrgi(), contacts, appid,
                    // userid);
                    String uid = (String) request.getSession().getAttribute("Sessionuid");
                    String sid = (String) request.getSession().getAttribute("Sessionsid");
                    String cid = (String) request.getSession().getAttribute("Sessioncid");

                    if (StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(cid)) {
                        Contacts contacts1 = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(
                                uid, sid, cid, false);
                        if (contacts1 != null) {
                            agentUserRepository.findOneByUseridAndOrgi(userid, orgi).ifPresent(p -> {
                                // 关联AgentService的联系人
                                if (StringUtils.isNotBlank(p.getAgentserviceid())) {
                                    AgentService agentService = agentServiceRepository.findOne(
                                            p.getAgentserviceid());
                                    agentService.setContactsid(contacts1.getId());
                                }

                                // 关联AgentUserContact的联系人
                                // NOTE: 如果该userid已经有了关联的Contact则忽略，继续使用之前的
                                Optional<AgentUserContacts> agentUserContactsOpt = agentUserContactsRes
                                        .findOneByUseridAndOrgi(
                                                userid, orgi);
                                if (!agentUserContactsOpt.isPresent()) {
                                    AgentUserContacts agentUserContacts = new AgentUserContacts();
                                    agentUserContacts.setOrgi(orgi);
                                    agentUserContacts.setAppid(appid);
                                    agentUserContacts.setChannel(p.getChannel());
                                    agentUserContacts.setContactsid(contacts1.getId());
                                    agentUserContacts.setUserid(userid);
                                    agentUserContacts.setUsername(
                                            (String) request.getSession().getAttribute("Sessionusername"));
                                    agentUserContacts.setCreater(super.getUser(request).getId());
                                    agentUserContacts.setCreatetime(new Date());
                                    agentUserContactsRes.save(agentUserContacts);
                                }
                            });
                        }
                    }
                }

                if (StringUtils.isNotBlank(client)) {
                    map.addAttribute("client", client);
                }

                if (StringUtils.isNotBlank(skill)) {
                    map.addAttribute("skill", skill);
                }

                if (StringUtils.isNotBlank(agent)) {
                    map.addAttribute("agent", agent);
                }

                map.addAttribute("contacts", contacts);

                if (StringUtils.isNotBlank(type)) {
                    map.addAttribute("type", type);
                }
                IP ipdata = IPTools.getInstance().findGeography(MainUtils.getIpAddr(request));
                map.addAttribute("skillGroups", OnlineUserProxy.organ(invite.getOrgi(), ipdata, invite, true));

                if (invite != null && consult) {
                    if (contacts != null && StringUtils.isNotBlank(contacts.getName())) {
                        nickname = contacts.getName();
                    }

                    map.addAttribute("username", nickname);
                    boolean isChatbotAgentFirst = false;
                    boolean isEnableExchangeAgentType = false;
                    Chatbot bot = null;

                    // 是否使用机器人客服
                    if (invite.isAi() && MainContext.hasModule(Constants.CSKEFU_MODULE_CHATBOT)) {
                        // 查找机器人
                        bot = chatbotRes.findOne(invite.getAiid());
                        if (bot != null) {
                            // 判断是否接受访客切换坐席类型
                            isEnableExchangeAgentType = !StringUtils.equals(
                                    bot.getWorkmode(), Constants.CHATBOT_CHATBOT_ONLY);

                            // 判断是否机器人客服优先
                            if (((StringUtils.equals(
                                    ai, "true")) || (invite.isAifirst() && ai == null))) {
                                isChatbotAgentFirst = true;
                            }
                        }
                    }

                    map.addAttribute(
                            "exchange", isEnableExchangeAgentType);

                    if (isChatbotAgentFirst) {
                        // 机器人坐席
                        HashMap<String, String> chatbotConfig = new HashMap<String, String>();
                        chatbotConfig.put("botname", invite.getAiname());
                        chatbotConfig.put("botid", invite.getAiid());
                        chatbotConfig.put("botwelcome", invite.getAimsg());
                        chatbotConfig.put("botfirst", Boolean.toString(invite.isAifirst()));
                        chatbotConfig.put("isai", Boolean.toString(invite.isAi()));

                        if (chatbotConfig != null) {
                            map.addAttribute("chatbotConfig", chatbotConfig);
                        }
                        view = request(super.createView("/apps/im/chatbot/index"));
                        if (MobileDevice.isMobile(request.getHeader("User-Agent")) || StringUtils.isNotBlank(
                                mobile)) {
                            view = request(super.createView(
                                    "/apps/im/chatbot/mobile")); // 智能机器人 移动端
                        }
                    } else {
                        // 维持人工坐席的设定，检查是否进入留言
                        if (!isLeavemsg && (MobileDevice.isMobile(
                                request.getHeader("User-Agent")) || StringUtils.isNotBlank(mobile))) {
                            view = request(
                                    super.createView("/apps/im/mobile")); // WebIM移动端。再次点选技能组？
                        }
                    }

                    map.addAttribute(
                            "chatMessageList", chatMessageRes.findByUsessionAndOrgi(userid, orgi, new PageRequest(0, 20,
                                    Direction.DESC,
                                    "updatetime")));
                }
                view.addObject("commentList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_DIC));
                view.addObject("commentItemList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_ITEM_DIC));

                /**
                 * 绑定广告位信息，确定对应的组织机构；广告是在该组织机构下，管理的【客服设置】
                 * 1）查找对应的网站渠道，如果找到对应的网站渠道，则设置为该网站渠道所属的组织机构
                 * 2）该渠道不属于网站渠道，那么是来自于其他渠道，查看其是否绑定技能组，如果绑定技能组，则使用绑定技能组的 ID
                 */
                String adsAttachedOrgan = null;
                if (StringUtils.isNotEmpty(invite.getSnsaccountid())) {
                    SNSAccount snsAccount = snsAccountRes.findBySnsidAndOrgi(invite.getSnsaccountid(), orgi);
                    if (snsAccount != null) {
                        adsAttachedOrgan = snsAccount.getOrgan();
                    }
                } else if (StringUtils.isNotEmpty(skill)) {
                    adsAttachedOrgan = skill;
                }

                if (StringUtils.isNotEmpty(adsAttachedOrgan)) {
                    view.addObject("welcomeAd",
                            MainUtils.getPointAdv(MainContext.AdPosEnum.WELCOME.toString(), adsAttachedOrgan, orgi));
                    view.addObject("figureAds",
                            MainUtils.getPointAdvs(MainContext.AdPosEnum.IMAGE.toString(), adsAttachedOrgan, orgi));
                }

                // 确定"接受邀请"被处理后，通知浏览器关闭弹出窗口
                OnlineUserProxy.sendWebIMClients(userid, "accept");

                // 更新 InviteRecord
                logger.info("[index] update inviteRecord for user {}", userid);
                final Date threshold = new Date(System.currentTimeMillis() - Constants.WEBIM_AGENT_INVITE_TIMEOUT);
                Page<InviteRecord> inviteRecords = inviteRecordRes.findByUseridAndOrgiAndResultAndCreatetimeGreaterThan(
                        userid, orgi,
                        MainContext.OnlineUserInviteStatus.DEFAULT.toString(),
                        threshold, new PageRequest(0, 1, Direction.DESC, "createtime"));
                if (inviteRecords.getContent() != null && inviteRecords.getContent().size() > 0) {
                    final InviteRecord record = inviteRecords.getContent().get(0);
                    record.setUpdatetime(new Date());
                    record.setTraceid(traceid);
                    record.setTitle(title);
                    record.setUrl(url);
                    record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
                    record.setResult(MainContext.OnlineUserInviteStatus.ACCEPT.toString());
                    logger.info("[index] re-save inviteRecord id {}", record.getId());
                    inviteRecordRes.save(record);
                }

            } else {
                logger.info("[index] can not invite for appid {}, orgi {}", appid, orgi);
            }
        } else {
            view.addObject("inviteData", invite);
        }

        logger.info("[index] return view");
        return view;
    }

    @GetMapping("/text/{appid}")
    @Menu(type = "im", subtype = "index", access = true)
    public ModelAndView text(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String appid,
            @Valid String traceid,
            @Valid String aiid,
            @Valid String exchange,
            @Valid String title,
            @Valid String url,
            @Valid String skill,
            @Valid String id,
            @Valid String userid,
            @Valid String agent,
            @Valid String name,
            @Valid String email,
            @Valid String phone,
            @Valid String ai,
            @Valid String orgi,
            @Valid String product,
            @Valid String description,
            @Valid String imgurl,
            @Valid String pid,
            @Valid String purl) throws Exception {
        ModelAndView view = request(super.createView("/apps/im/text"));
        CousultInvite invite = OnlineUserProxy.consult(
                appid, StringUtils.isBlank(orgi) ? Constants.SYSTEM_ORGI : orgi);

        view.addObject("hostname", request.getServerName());
        view.addObject("port", request.getServerPort());
        view.addObject("schema", super.getSchema(request));
        view.addObject("appid", appid);
        view.addObject("channelVisitorSeparate", channelWebIMVisitorSeparate);
        view.addObject("ip", MainUtils.md5(request.getRemoteAddr()));

        if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 添加技能组ID
            // 忽略前端传入的技能组ID
            view.addObject("skill", invite.getConsult_skill_fixed_id());
        } else if (StringUtils.isNotBlank(skill)) {
            view.addObject("skill", skill);
        }

        if (StringUtils.isNotBlank(agent)) {
            view.addObject("agent", agent);
        }

        view.addObject("client", MainUtils.getUUID());
        view.addObject("sessionid", request.getSession().getId());

        view.addObject("id", id);
        if (StringUtils.isNotBlank(ai)) {
            view.addObject("ai", ai);
        }
        if (StringUtils.isNotBlank(exchange)) {
            view.addObject("exchange", exchange);
        }

        view.addObject("name", name);
        view.addObject("email", email);
        view.addObject("phone", phone);
        view.addObject("userid", userid);

        view.addObject("product", product);
        view.addObject("description", description);
        view.addObject("imgurl", imgurl);
        view.addObject("pid", pid);
        view.addObject("purl", purl);

        if (StringUtils.isNotBlank(traceid)) {
            view.addObject("traceid", traceid);
        }
        if (StringUtils.isNotBlank(title)) {
            view.addObject("title", title);
        }
        if (StringUtils.isNotBlank(traceid)) {
            view.addObject("url", url);
        }

        if (invite != null) {
            view.addObject("inviteData", invite);
            view.addObject("orgi", invite.getOrgi());
            view.addObject("appid", appid);

            if (StringUtils.isNotBlank(aiid)) {
                view.addObject("aiid", aiid);
            } else if (StringUtils.isNotBlank(invite.getAiid())) {
                view.addObject("aiid", invite.getAiid());
            }
        }

        return view;
    }

    /**
     * 适配移动端连接请求
     *
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/text/{appid}")
    @Menu(type = "im", subtype = "index", access = true)
    public Map<String, Object> miniapp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String appid,
            @Valid String traceid,
            @Valid String aiid,
            @Valid String exchange,
            @Valid String title,
            @Valid String url,
            @Valid String skill,
            @Valid String id,
            @Valid String userid,
            @Valid String agent,
            @Valid String name,
            @Valid String email,
            @Valid String phone,
            @Valid String ai,
            @Valid String orgi,
            @Valid String product,
            @Valid String description,
            @Valid String imgurl,
            @Valid String pid,
            @Valid String purl) throws Exception {
        Map<String, Object> params = new HashMap<>();
        CousultInvite invite = OnlineUserProxy.consult(appid, StringUtils.isBlank(orgi) ? Constants.SYSTEM_ORGI : orgi);

        params.put("hostname", request.getServerName());
        params.put("port", request.getServerPort());
        params.put("schema", super.getSchema(request));
        params.put("appid", appid);
        params.put("channelVisitorSeparate", channelWebIMVisitorSeparate);
        params.put("ip", MainUtils.md5(request.getRemoteAddr()));

        if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 添加技能组ID
            // 忽略前端传入的技能组ID
            params.put("skill", invite.getConsult_skill_fixed_id());
        } else if (StringUtils.isNotBlank(skill)) {
            params.put("skill", skill);
        }

        if (StringUtils.isNotBlank(agent)) {
            params.put("agent", agent);
        }

        params.put("client", MainUtils.getUUID());
        params.put("sessionid", request.getSession().getId());

        params.put("id", id);
        if (StringUtils.isNotBlank(ai)) {
            params.put("ai", ai);
        }
        if (StringUtils.isNotBlank(exchange)) {
            params.put("exchange", exchange);
        }

        params.put("name", name);
        params.put("email", email);
        params.put("phone", phone);
        params.put("userid", userid);

        params.put("product", product);
        params.put("description", description);
        params.put("imgurl", imgurl);
        params.put("pid", pid);
        params.put("purl", purl);

        if (StringUtils.isNotBlank(traceid)) {
            params.put("traceid", traceid);
        }
        if (StringUtils.isNotBlank(title)) {
            params.put("title", title);
        }
        if (StringUtils.isNotBlank(traceid)) {
            params.put("url", url);
        }

        if (invite != null) {
            params.put("inviteData", invite);
            params.put("orgi", invite.getOrgi());
            params.put("appid", appid);

            if (StringUtils.isNotBlank(aiid)) {
                params.put("aiid", aiid);
            } else if (StringUtils.isNotBlank(invite.getAiid())) {
                params.put("aiid", invite.getAiid());
            }
        }

        return params;
    }

    @RequestMapping("/leavemsg/save")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView leavemsgsave(HttpServletRequest request,
                                     @Valid String appid,
                                     @Valid LeaveMsg msg,
                                     @Valid String skillId) {
        if (StringUtils.isNotBlank(appid)) {
            snsAccountRepository.findBySnsid(appid).ifPresent(p -> {
                CousultInvite invite = inviteRepository.findBySnsaccountidAndOrgi(appid, Constants.SYSTEM_ORGI);
                // TODO 增加策略防止恶意刷消息
                // List<LeaveMsg> msgList = leaveMsgRes.findByOrgiAndUserid(invite.getOrgi(),
                // msg.getUserid());
                // if(msg!=null && msgList.size() == 0){
                if (msg != null) {
                    msg.setOrgi(invite.getOrgi());
                    msg.setSkill(skillId);
                    msg.setChannel(p);
                    msg.setSnsId(appid);
                    leaveMsgRes.save(msg);
                }
            });
        }
        return request(super.createView("/apps/im/leavemsgsave"));
    }

    @RequestMapping("/refuse")
    @Menu(type = "im", subtype = "refuse", access = true)
    public void refuse(HttpServletRequest request, HttpServletResponse response, @Valid String orgi,
                       @Valid String appid, @Valid String userid, @Valid String sessionid, @Valid String client) throws Exception {
        OnlineUserProxy.refuseInvite(userid, orgi);
        final Date threshold = new Date(System.currentTimeMillis() - Constants.WEBIM_AGENT_INVITE_TIMEOUT);
        Page<InviteRecord> inviteRecords = inviteRecordRes.findByUseridAndOrgiAndResultAndCreatetimeGreaterThan(
                userid,
                orgi,
                MainContext.OnlineUserInviteStatus.DEFAULT.toString(),
                threshold,
                new PageRequest(
                        0,
                        1,
                        Direction.DESC,
                        "createtime"));
        if (inviteRecords.getContent() != null && inviteRecords.getContent().size() > 0) {
            InviteRecord record = inviteRecords.getContent().get(0);
            record.setUpdatetime(new Date());
            record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
            record.setResult(MainContext.OnlineUserInviteStatus.REFUSE.toString());
            inviteRecordRes.save(record);
        }
        return;
    }

    @RequestMapping("/satis")
    @Menu(type = "im", subtype = "satis", access = true)
    public void satis(HttpServletRequest request, HttpServletResponse response, @Valid AgentServiceSatis satis)
            throws Exception {
        if (satis != null && StringUtils.isNotBlank(satis.getId())) {
            int count = agentServiceSatisRes.countById(satis.getId());
            if (count == 1) {
                if (StringUtils.isNotBlank(satis.getSatiscomment()) && satis.getSatiscomment().length() > 255) {
                    satis.setSatiscomment(satis.getSatiscomment().substring(0, 255));
                }
                satis.setSatisfaction(true);
                satis.setSatistime(new Date());
                agentServiceSatisRes.save(satis);
            }
        }
        return;
    }

    @RequestMapping("/image/upload")
    @Menu(type = "im", subtype = "image", access = true)
    public ResponseEntity<String> upload(
            ModelMap map, HttpServletRequest request,
            @RequestParam(value = "imgFile", required = false) MultipartFile multipart,
            @Valid String channel,
            @Valid String userid,
            @Valid String username,
            @Valid String appid,
            @Valid String orgi,
            @Valid String paste) throws IOException {
        final User logined = super.getUser(request);

        String fileName = null;
        JSONObject result = new JSONObject();
        HttpHeaders headers = RestUtils.header();
        // String multipartLast = null;
        // if ( multipart != null && multipart.getOriginalFilename() != null ){
        // Number multipartLenght = multipart.getOriginalFilename().split("\\.").length
        // - 1;
        // multipartLast = multipart.getOriginalFilename().split("\\.")[
        // multipartLenght.intValue()];
        // }

        // if (multipart != null &&
        // multipartLast != null
        // && multipart.getOriginalFilename().lastIndexOf(".") > 0
        // && StringUtils.isNotBlank(userid)) {
        // if( multipartLast.equals("jpeg") || multipartLast.equals("jpg") ||
        // multipartLast.equals("bmp")
        // || multipartLast.equals("png") ){
        if (multipart != null
                && multipart.getOriginalFilename().lastIndexOf(".") > 0
                && StringUtils.isNotBlank(userid)) {
            File uploadDir = new File(path, "upload");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileid = MainUtils.getUUID();
            StreamingFile sf = new StreamingFile();
            sf.setId(fileid);
            sf.setName(multipart.getOriginalFilename());
            sf.setMime(multipart.getContentType());
            if (multipart.getContentType() != null
                    && multipart.getContentType().indexOf(Constants.ATTACHMENT_TYPE_IMAGE) >= 0) {
                // 检查文件格式
                String invalid = StreamingFileUtil.getInstance().validate(
                        Constants.ATTACHMENT_TYPE_IMAGE, multipart.getOriginalFilename());
                if (invalid == null) {
                    fileName = "upload/" + fileid + "_original";
                    File imageFile = new File(path, fileName);
                    FileCopyUtils.copy(multipart.getBytes(), imageFile);
                    String thumbnailsFileName = "upload/" + fileid;
                    File thumbnail = new File(path, thumbnailsFileName);
                    MainUtils.processImage(thumbnail, imageFile);

                    // 存储数据库
                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    sf.setThumbnail(jpaBlobHelper.createBlobWithFile(thumbnail));
                    streamingFileRepository.save(sf);
                    String fileUrl = "/res/image.html?id=" + fileid;
                    result.put("error", 0);
                    result.put("url", fileUrl);

                    if (paste == null) {
                        if (StringUtils.isNotBlank(channel)) {
                            RichMediaUtils.uploadImageWithChannel(
                                    fileUrl, fileid, (int) multipart.getSize(), multipart.getName(), channel, userid,
                                    username, appid, orgi);
                        } else {
                            RichMediaUtils.uploadImage(
                                    fileUrl, fileid, (int) multipart.getSize(), multipart.getName(), userid);
                        }
                    }
                } else {
                    result.put("error", 1);
                    result.put("message", invalid);
                }
            } else {
                String invalid = StreamingFileUtil.getInstance().validate(
                        Constants.ATTACHMENT_TYPE_FILE, multipart.getOriginalFilename());
                if (invalid == null) {
                    // 存储数据库
                    sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
                    streamingFileRepository.save(sf);

                    // 存储到本地硬盘
                    String id = processAttachmentFile(multipart,
                            fileid, logined.getOrgi(), logined.getId());
                    result.put("error", 0);
                    result.put("url", "/res/file.html?id=" + id);
                    String file = "/res/file.html?id=" + id;

                    File tempFile = new File(multipart.getOriginalFilename());
                    if (StringUtils.isNotBlank(channel)) {
                        RichMediaUtils.uploadFileWithChannel(
                                file, (int) multipart.getSize(), tempFile.getName(), channel, userid, username, appid,
                                orgi, id);
                    } else {
                        RichMediaUtils.uploadFile(file, (int) multipart.getSize(), tempFile.getName(), userid, id);
                    }
                } else {
                    result.put("error", 1);
                    result.put("message", invalid);
                }
            }
        } else {
            result.put("error", 1);
            result.put("message", "请选择文件");
        }
        // }else {
        // upload = new UploadStatus("请上传格式为jpg，png，jpeg，bmp类型图片");
        // }
        // } else {
        // upload = new UploadStatus("请上传格式为jpg，png，jpeg，bmp类型图片");
        // }

        return new ResponseEntity<>(result.toString(), headers, HttpStatus.OK);
    }

    private String processAttachmentFile(
            final MultipartFile file,
            final String fileid,
            final String orgi,
            final String creator) throws IOException {
        String id = null;

        if (file.getSize() > 0) { // 文件尺寸 限制 ？在 启动 配置中 设置 的最大值，其他地方不做限制
            AttachmentFile attachmentFile = new AttachmentFile();
            attachmentFile.setCreater(creator);
            attachmentFile.setOrgi(orgi);
            attachmentFile.setModel(MainContext.ModelType.WEBIM.toString());
            attachmentFile.setFilelength((int) file.getSize());
            if (file.getContentType() != null && file.getContentType().length() > 255) {
                attachmentFile.setFiletype(file.getContentType().substring(0, 255));
            } else {
                attachmentFile.setFiletype(file.getContentType());
            }
            String originalFilename = URLDecoder.decode(file.getOriginalFilename(), "utf-8");
            File uploadFile = new File(originalFilename);
            if (uploadFile.getName() != null && uploadFile.getName().length() > 255) {
                attachmentFile.setTitle(uploadFile.getName().substring(0, 255));
            } else {
                attachmentFile.setTitle(uploadFile.getName());
            }
            if (StringUtils.isNotBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().indexOf(
                    "image") >= 0) {
                attachmentFile.setImage(true);
            }
            attachmentFile.setFileid(fileid);
            attachementRes.save(attachmentFile);
            FileUtils.writeByteArrayToFile(new File(path, "upload/" + fileid), file.getBytes());
            id = attachmentFile.getId();
        }
        return id;
    }
}