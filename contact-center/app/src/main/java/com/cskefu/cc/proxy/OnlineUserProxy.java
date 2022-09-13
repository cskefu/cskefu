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
package com.cskefu.cc.proxy;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.es.ContactsRepository;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.socketio.message.OtherMessageItem;
import com.cskefu.cc.util.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.text.SimpleDateFormat;
import java.util.*;


public class OnlineUserProxy {
    private final static Logger logger = LoggerFactory.getLogger(OnlineUserProxy.class);
    public static WebSseEmitterClient webIMClients = new WebSseEmitterClient();
    public static ObjectMapper objectMapper = new ObjectMapper();

    private static OnlineUserRepository onlineUserRes;
    private static UserRepository userRes;
    private static Cache cache;
    private static ConsultInviteRepository consultInviteRes;
    private static OnlineUserHisRepository onlineUserHisRes;
    private static UserTraceRepository userTraceRes;
    private static OrgiSkillRelRepository orgiSkillRelRes;
    private static AgentUserContactsRepository agentUserContactsRes;
    private static ContactsRepository contactsRes;
    private static UserProxy userProxy;
    private static OrganRepository organRes;

    /**
     * @param id
     * @return
     * @throws Exception
     */
    public static OnlineUser user(final String orgi, final String id) {
        return getOnlineUserRes().findOne(id);
    }

    /**
     * 更新cache
     *
     * @param consultInvite
     */
    public static void cacheConsult(final CousultInvite consultInvite) {
        logger.info("[cacheConsult] snsid {}, orgi {}", consultInvite.getSnsaccountid(), consultInvite.getOrgi());
        getCache().putConsultInviteByOrgi(consultInvite.getOrgi(), consultInvite);
    }

    /**
     * @param snsid
     * @param orgi
     * @return
     */
    public static CousultInvite consult(final String snsid, final String orgi) {
//        logger.info("[consult] snsid {}, orgi {}", snsid, orgi);
        CousultInvite consultInvite = MainContext.getCache().findOneConsultInviteBySnsidAndOrgi(snsid, orgi);
        if (consultInvite == null) {
            consultInvite = getConsultInviteRes().findBySnsaccountidAndOrgi(snsid, orgi);
            if (consultInvite != null) {
                getCache().putConsultInviteByOrgi(orgi, consultInvite);
            }
        }
        return consultInvite;
    }


    /**
     * 在Cache中查询OnlineUser，或者从数据库中根据UserId，Orgi和Invite查询
     *
     * @param userid
     * @param orgi
     * @return
     */
    public static OnlineUser onlineuser(String userid, String orgi) {
        // 从Cache中查找
        OnlineUser onlineUser = getCache().findOneOnlineUserByUserIdAndOrgi(userid, orgi);

        if (onlineUser == null) {
            logger.info(
                    "[onlineuser] !!! fail to resolve user {} with both cache and database, maybe this user is first presents.",
                    userid);
        }

        return onlineUser;
    }


    /**
     * @param orgi
     * @param ipdata
     * @param invite
     * @param isJudgeShare 是否判断是否共享租户
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Organ> organ(
            String orgi, final IP ipdata,
            final CousultInvite invite, boolean isJudgeShare) {
        String origOrig = orgi;
        boolean isShare = false;
        if (isJudgeShare) {
            SystemConfig systemConfig = MainUtils.getSystemConfig();
            if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
                orgi = Constants.SYSTEM_ORGI;
                isShare = true;
            }
        }
        List<Organ> skillGroups = getCache().findOneSystemByIdAndOrgi(Constants.CACHE_SKILL + origOrig, origOrig);
        if (skillGroups == null) {
            OrganRepository service = MainContext.getContext().getBean(OrganRepository.class);
            skillGroups = service.findByOrgiAndSkill(orgi, true);
            // 租户共享时 查出该租住要显的绑定的技能组
            if (isShare && !(StringUtils.equals(
                    Constants.SYSTEM_ORGI, (invite == null ? origOrig : invite.getOrgi())))) {
                OrgiSkillRelRepository orgiSkillRelService = MainContext.getContext().getBean(
                        OrgiSkillRelRepository.class);
                List<OrgiSkillRel> orgiSkillRelList = null;
                orgiSkillRelList = orgiSkillRelService.findByOrgi((invite == null ? origOrig : invite.getOrgi()));
                List<Organ> skillTempList = new ArrayList<>();
                if (!orgiSkillRelList.isEmpty()) {
                    for (Organ organ : skillGroups) {
                        for (OrgiSkillRel rel : orgiSkillRelList) {
                            if (organ.getId().equals(rel.getSkillid())) {
                                skillTempList.add(organ);
                            }
                        }
                    }
                }
                skillGroups = skillTempList;
            }

            if (skillGroups.size() > 0) {
                getCache().putSystemListByIdAndOrgi(Constants.CACHE_SKILL + origOrig, origOrig, skillGroups);
            }
        }

        if (ipdata == null && invite == null) {
            return skillGroups;
        }

        List<Organ> regOrganList = new ArrayList<Organ>();
        for (Organ organ : skillGroups) {
            if (StringUtils.isNotBlank(organ.getArea())) {
                if (organ.getArea().indexOf(ipdata.getProvince()) >= 0 || organ.getArea().indexOf(
                        ipdata.getCity()) >= 0) {
                    regOrganList.add(organ);
                }
            } else {
                regOrganList.add(organ);
            }
        }
        return regOrganList;
    }


    /**
     * @param orgi
     * @param isJudgeShare
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Organ> organ(String orgi, boolean isJudgeShare) {
        return organ(orgi, null, null, isJudgeShare);
    }

    private static List<AreaType> getAreaTypeList(String area, List<AreaType> areaTypeList) {
        List<AreaType> atList = new ArrayList<AreaType>();
        if (areaTypeList != null && areaTypeList.size() > 0) {
            for (AreaType areaType : areaTypeList) {
                if (StringUtils.isNotBlank(area) && area.indexOf(areaType.getId()) >= 0) {
                    atList.add(areaType);
                }
            }
        }
        return atList;
    }

    /**
     * @param orgi
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<User> agents(String orgi) {
        String origOrig = orgi;

        List<User> agentList = getUserRes().findByOrgiAndAgentAndDatastatus(orgi, true, false);
        List<User> agentTempList = new ArrayList<User>();
        List<Organ> skillOrgansByOrgi = getOrganRes().findByOrgiAndSkill(origOrig, true);

        if (!skillOrgansByOrgi.isEmpty()) {
            for (User user : agentList) {
                // 跳过超级管理员角色用户，不显示在技能组列表
                if (user.isSuperadmin()) continue;

                // 只显示在线的客服，跳过离线的客服
                if (getCache().findOneAgentStatusByAgentnoAndOrig(user.getId(), origOrig) == null) continue;

                // 一个用户可隶属于多个组织
                getUserProxy().attachOrgansPropertiesForUser(user);
                for (Organ organ : skillOrgansByOrgi) {
                    if (user.getOrgans().size() > 0 && user.inAffiliates(organ.getId())) {
                        agentTempList.add(user);
                    }
                }
            }
        }
        agentList = agentTempList;

        return agentList;
    }

    public static Contacts processContacts(
            final String orgi,
            Contacts contacts,
            final String appid,
            final String userid) {
        if (contacts != null) {
            if (contacts != null &&
                    (StringUtils.isNotBlank(contacts.getName()) ||
                            StringUtils.isNotBlank(contacts.getPhone()) ||
                            StringUtils.isNotBlank(contacts.getEmail()))) {
                StringBuffer query = new StringBuffer();
                query.append(contacts.getName());
                if (StringUtils.isNotBlank(contacts.getPhone())) {
                    query.append(" OR ").append(contacts.getPhone());
                }
                if (StringUtils.isNotBlank(contacts.getEmail())) {
                    query.append(" OR ").append(contacts.getEmail());
                }
                Page<Contacts> contactsList = contactsRes.findByOrgi(
                        orgi, false, query.toString(), new PageRequest(0, 1));
                if (contactsList.getContent().size() > 0) {
                    contacts = contactsList.getContent().get(0);
                } else {
//					contactsRes.save(contacts) ;	//需要增加签名验证，避免随便产生垃圾信息，也可以自行修改？
                    contacts.setId(null);
                }
            } else {
                contacts.setId(null);
            }

            if (contacts != null && StringUtils.isNotBlank(contacts.getId())) {
                if (!getAgentUserContactsRes().findOneByUseridAndOrgi(userid, orgi).isPresent()) {
                    AgentUserContacts agentUserContacts = new AgentUserContacts();
                    agentUserContacts.setAppid(appid);
                    agentUserContacts.setChannel(MainContext.ChannelType.WEBIM.toString());
                    agentUserContacts.setContactsid(contacts.getId());
                    agentUserContacts.setUserid(userid);
                    agentUserContacts.setOrgi(orgi);
                    agentUserContacts.setCreatetime(new Date());
                    agentUserContactsRes.save(agentUserContacts);
                }
            } else if (StringUtils.isNotBlank(userid)) {
                Optional<AgentUserContacts> agentUserContactOpt = agentUserContactsRes.findOneByUseridAndOrgi(
                        userid, orgi);
                if (agentUserContactOpt.isPresent()) {
                    contacts = getContactsRes().findOne(agentUserContactOpt.get().getContactsid());
                }
            }
        }
        return contacts;
    }

    /**
     * 创建OnlineUser并上线
     * 根据user判断追踪，在浏览器里，用fingerprint2生成的ID作为唯一标识
     *
     * @param user
     * @param orgi
     * @param sessionid
     * @param optype
     * @param request
     * @param channel
     * @param appid
     * @param contacts
     * @param invite
     * @return
     * @throws CharacterCodingException
     */
    public static OnlineUser online(
            final User user,
            final String orgi,
            final String sessionid,
            final String optype,
            final HttpServletRequest request,
            final String channel,
            final String appid,
            final Contacts contacts,
            final CousultInvite invite) {
//        logger.info(
//                "[online] user {}, orgi {}, sessionid {}, optype {}, channel {}", user.getId(), orgi, sessionid, optype,
//                channel);
        OnlineUser onlineUser = null;
        final Date now = new Date();
        if (invite != null) {
            // resolve user from cache or db.
            onlineUser = onlineuser(user.getId(), orgi);

            if (onlineUser == null) {
//                logger.info("[online] create new online user.");
                onlineUser = new OnlineUser();
                onlineUser.setId(user.getId());
                onlineUser.setCreater(user.getId());
                onlineUser.setUsername(user.getUsername());
                onlineUser.setCreatetime(now);
                onlineUser.setUpdatetime(now);
                onlineUser.setUpdateuser(user.getUsername());
                onlineUser.setSessionid(sessionid);

                if (contacts != null) {
                    onlineUser.setContactsid(contacts.getId());
                }

                onlineUser.setOrgi(orgi);
                onlineUser.setChannel(channel);

                // 从Server session信息中查找该用户相关的历史信息
                String cookie = getCookie(request, "R3GUESTUSEKEY");
                if ((StringUtils.isBlank(cookie))
                        || (StringUtils.equals(user.getSessionid(), cookie))) {
                    onlineUser.setOlduser("0");
                } else {
                    // 之前有session的访客
                    onlineUser.setOlduser("1");
                }
                onlineUser.setMobile(MobileDevice.isMobile(request
                        .getHeader("User-Agent")) ? "1" : "0");

                // onlineUser.setSource(user.getId());

                String url = request.getHeader("referer");
                onlineUser.setUrl(url);
                if (StringUtils.isNotBlank(url)) {
                    try {
                        URL referer = new URL(url);
                        onlineUser.setSource(referer.getHost());
                    } catch (MalformedURLException e) {
                        logger.info("[online] error when parsing URL", e);
                    }
                }
                onlineUser.setAppid(appid);
                onlineUser.setUserid(user.getId());
                onlineUser.setUsername(user.getUsername());

                if (StringUtils.isNotBlank(request.getParameter("title"))) {
                    String title = request.getParameter("title");
                    if (title.length() > 255) {
                        onlineUser.setTitle(title.substring(0, 255));
                    } else {
                        onlineUser.setTitle(title);
                    }
                }

                onlineUser.setLogintime(now);

                // 地理信息
                String ip = MainUtils.getIpAddr(request);
                onlineUser.setIp(ip);
                IP ipdata = IPTools.getInstance().findGeography(ip);
                onlineUser.setCountry(ipdata.getCountry());
                onlineUser.setProvince(ipdata.getProvince());
                onlineUser.setCity(ipdata.getCity());
                onlineUser.setIsp(ipdata.getIsp());
                onlineUser.setRegion(ipdata.toString() + "（"
                        + ip + "）");

                onlineUser.setDatestr(new SimpleDateFormat("yyyMMdd")
                        .format(now));

                onlineUser.setHostname(ip);
                onlineUser.setSessionid(sessionid);
                onlineUser.setOptype(optype);
                onlineUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString());
                final BrowserClient client = MainUtils.parseClient(request);

                // 浏览器信息
                onlineUser.setOpersystem(client.getOs());
                onlineUser.setBrowser(client.getBrowser());
                onlineUser.setUseragent(client.getUseragent());

                logger.info("[online] new online user is created but not persisted.");
            } else {
                // 从DB或缓存找到OnlineUser
                onlineUser.setCreatetime(now); // 刷新创建时间
                if ((StringUtils.isNotBlank(onlineUser.getSessionid()) && !StringUtils.equals(
                        onlineUser.getSessionid(), sessionid)) ||
                        !StringUtils.equals(
                                MainContext.OnlineUserStatusEnum.ONLINE.toString(), onlineUser.getStatus())) {
                    // 当新的session与从DB或缓存查找的session不一致时，或者当数据库或缓存的OnlineUser状态不是ONLINE时
                    // 代表该用户登录了新的Session或从离线变为上线！

                    onlineUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString()); // 设置用户到上线
                    onlineUser.setChannel(channel);          // 设置渠道
                    onlineUser.setAppid(appid);
                    onlineUser.setUpdatetime(now);           // 刷新更新时间
                    if (StringUtils.isNotBlank(onlineUser.getSessionid()) && !StringUtils.equals(
                            onlineUser.getSessionid(), sessionid)) {
                        onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                        onlineUser.setSessionid(sessionid);  // 设置新的session信息
                        onlineUser.setLogintime(now);        // 设置更新时间
                        onlineUser.setInvitetimes(0);        // 重置邀请次数
                    }
                }

                // 处理联系人关联信息
                if (contacts != null) {
                    // 当关联到联系人
                    if (StringUtils.isNotBlank(contacts.getId()) && StringUtils.isNotBlank(
                            contacts.getName()) && (StringUtils.isBlank(
                            onlineUser.getContactsid()) || !contacts.getName().equals(onlineUser.getUsername()))) {
                        if (StringUtils.isBlank(onlineUser.getContactsid())) {
                            onlineUser.setContactsid(contacts.getId());
                        }
                        if (!contacts.getName().equals(onlineUser.getUsername())) {
                            onlineUser.setUsername(contacts.getName());
                        }
                        onlineUser.setUpdatetime(now);
                    }
                }

                if (StringUtils.isBlank(onlineUser.getUsername()) && StringUtils.isNotBlank(user.getUsername())) {
                    onlineUser.setUseragent(user.getUsername());
                    onlineUser.setUpdatetime(now);
                }
            }

            if (invite.isRecordhis() && StringUtils.isNotBlank(request.getParameter("traceid"))) {
                UserTraceHistory trace = new UserTraceHistory();
                trace.setId(request.getParameter("traceid"));
                trace.setTitle(request.getParameter("title"));
                trace.setUrl(request.getParameter("url"));
                trace.setOrgi(invite.getOrgi());
                trace.setUpdatetime(new Date());
                trace.setUsername(onlineUser.getUsername());
                getUserTraceRes().save(trace);
            }

            // 完成获取及更新OnlineUser, 将信息加入缓存
            if (onlineUser != null && StringUtils.isNotBlank(onlineUser.getUserid())) {
//                logger.info(
//                        "[online] onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
//                        onlineUser.getStatus(), onlineUser.getInvitestatus());
                // 存储到缓存及数据库
                getOnlineUserRes().save(onlineUser);
            }
        }
        return onlineUser;
    }

    /**
     * @param request
     * @param key
     * @return
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie data = null;
        if (request != null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(key)) {
                    data = cookie;
                    break;
                }
            }
        }
        return data != null ? data.getValue() : null;
    }

    /**
     * @param user
     * @param orgi
     * @throws Exception
     */
    public static void offline(String user, String orgi) {
        if (MainContext.getContext() != null) {
            OnlineUser onlineUser = getCache().findOneOnlineUserByUserIdAndOrgi(user, orgi);
            if (onlineUser != null) {
                onlineUser.setStatus(MainContext.OnlineUserStatusEnum.OFFLINE.toString());
                onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                onlineUser.setBetweentime((int) (new Date().getTime() - onlineUser.getLogintime().getTime()));
                onlineUser.setUpdatetime(new Date());
                getOnlineUserRes().save(onlineUser);

                final OnlineUserHis his = getOnlineUserHisRes().findOneBySessionidAndOrgi(
                        onlineUser.getSessionid(), onlineUser.getOrgi()).orElseGet(OnlineUserHis::new);
                MainUtils.copyProperties(onlineUser, his);
                his.setDataid(onlineUser.getId());
                getOnlineUserHisRes().save(his);
            }
            getCache().deleteOnlineUserByIdAndOrgi(user, orgi);
        }
    }

    /**
     * 设置onlineUser为离线
     *
     * @param onlineUser
     * @throws Exception
     */
    public static void offline(OnlineUser onlineUser) {
        if (onlineUser != null) {
            offline(onlineUser.getId(), onlineUser.getOrgi());
        }
    }

    /**
     * @param user
     * @param orgi
     * @throws Exception
     */
    public static void refuseInvite(final String user, final String orgi) {
        OnlineUser onlineUser = getOnlineUserRes().findOne(user);
        if (onlineUser != null) {
            onlineUser.setInvitestatus(MainContext.OnlineUserInviteStatus.REFUSE.toString());
            onlineUser.setRefusetimes(onlineUser.getRefusetimes() + 1);
            getOnlineUserRes().save(onlineUser);
        }
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        try {
            tmp.append(java.net.URLDecoder.decode(src, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return tmp.toString();
    }

    public static String getKeyword(String url) {
        Map<String, String[]> values = new HashMap<String, String[]>();
        try {
            OnlineUserUtils.parseParameters(values, url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer strb = new StringBuffer();
        String[] data = values.get("q");
        if (data != null) {
            for (String v : data) {
                strb.append(v);
            }
        }
        return strb.toString();
    }

    public static String getSource(String url) {
        String source = "0";
        try {
            URL addr = new URL(url);
            source = addr.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * 发送邀请
     *
     * @param userid
     * @throws Exception
     */
    public static void sendWebIMClients(String userid, String msg) throws Exception {
//        logger.info("[sendWebIMClients] userId {}, msg {}", userid, msg);
        List<WebIMClient> clients = OnlineUserProxy.webIMClients.getClients(userid);

        if (clients != null && clients.size() > 0) {
            for (WebIMClient client : clients) {
                try {
                    client.getSse().send(SseEmitter.event().reconnectTime(0).data(msg));
//                    logger.info("[sendWebIMClients] sent done with client {}", client.getClient());
                } catch (Exception ex) {
                    // 一些连接断开在服务器端没有清除
//                    logger.info("[sendWebIMClients] lost connection", ex);
                    // cleanup connections hold in server side
                    OnlineUserProxy.webIMClients.removeClient(userid, client.getClient(), false);
                } finally {
                    client.getSse().complete();
                }
            }
        }
    }

    public static OtherMessageItem suggestdetail(AiConfig aiCofig, String id, String orgi, User user) throws IOException {
        OtherMessageItem otherMessageItem = null;
        String param = "";
        if (StringUtils.isNotBlank(aiCofig.getOqrdetailinput())) {
            Template templet = MainUtils.getTemplate(aiCofig.getOqrdetailinput());
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("id", id);
            values.put("user", user);
            param = MainUtils.getTemplet(templet.getTemplettext(), values);
        }
        if (StringUtils.isNotBlank(aiCofig.getOqrdetailurl())) {
            String result = HttpClientUtil.doPost(aiCofig.getOqrdetailurl(), param), text = null;
            if (StringUtils.isNotBlank(aiCofig.getOqrdetailoutput()) && !result.equals("error")) {
                Template templet = MainUtils.getTemplate(aiCofig.getOqrdetailoutput());
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonData = objectMapper.readValue(result, Map.class);
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("id", id);
                values.put("user", user);
                values.put("data", jsonData);
                text = MainUtils.getTemplet(templet.getTemplettext(), values);
            }
            if (StringUtils.isNotBlank(text)) {
                otherMessageItem = objectMapper.readValue(text, OtherMessageItem.class);
            }
        }
        return otherMessageItem;
    }

//    public static OtherMessageItem detail(String id, String orgi, User user) throws IOException, TemplateException {
//        OtherMessageItem otherMessageItem = null;
//        String param = "";
//        SessionConfig sessionConfig = ACDServiceRouter.getAcdPolicyService().initSessionConfig(
//                orgi);
//        if (StringUtils.isNotBlank(sessionConfig.getOqrdetailinput())) {
//            Template templet = MainUtils.getTemplate(sessionConfig.getOqrdetailinput());
//            Map<String, Object> values = new HashMap<String, Object>();
//            values.put("id", id);
//            values.put("user", user);
//            param = MainUtils.getTemplet(templet.getTemplettext(), values);
//        }
//        if (StringUtils.isNotBlank(sessionConfig.getOqrdetailurl())) {
//            String result = HttpClientUtil.doPost(sessionConfig.getOqrdetailurl(), param), text = null;
//            if (StringUtils.isNotBlank(sessionConfig.getOqrdetailoutput()) && !result.equals("error")) {
//                Template templet = MainUtils.getTemplate(sessionConfig.getOqrdetailoutput());
//                @SuppressWarnings("unchecked")
//                Map<String, Object> jsonData = objectMapper.readValue(result, Map.class);
//                Map<String, Object> values = new HashMap<String, Object>();
//                values.put("id", id);
//                values.put("user", user);
//                values.put("data", jsonData);
//                text = MainUtils.getTemplet(templet.getTemplettext(), values);
//            }
//            if (StringUtils.isNotBlank(text)) {
//                otherMessageItem = objectMapper.readValue(text, OtherMessageItem.class);
//            }
//        }
//        return otherMessageItem;
//    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


    /**
     * 创建Skype联系人的onlineUser记录
     *
     * @param contact
     * @param logined
     * @return
     */
    public static OnlineUser createNewOnlineUserWithContactAndChannel(final Contacts contact, final User logined, final String channel) {
        final Date now = new Date();
        OnlineUser onlineUser = new OnlineUser();
        onlineUser.setId(MainUtils.getUUID());
        onlineUser.setUserid(onlineUser.getId());
        onlineUser.setLogintime(now);
        onlineUser.setUpdateuser(logined.getId());
        onlineUser.setContactsid(contact.getId());
        onlineUser.setUsername(contact.getName());
        onlineUser.setChannel(channel);
        onlineUser.setCity(contact.getCity());
        onlineUser.setOrgi(logined.getOrgi());
        onlineUser.setCreater(logined.getId());

        logger.info(
                "[createNewOnlineUserWithContactAndChannel] onlineUser id {}, userId {}", onlineUser.getId(),
                onlineUser.getUserid());
        // TODO 此处没有创建 onlineUser 的 appid
        getOnlineUserRes().save(onlineUser);
        return onlineUser;

    }

    private static AgentUserContactsRepository getAgentUserContactsRes() {
        if (agentUserContactsRes == null) {
            agentUserContactsRes = MainContext.getContext().getBean(AgentUserContactsRepository.class);
        }
        return agentUserContactsRes;
    }


    private static ContactsRepository getContactsRes() {
        if (contactsRes == null) {
            contactsRes = MainContext.getContext().getBean(ContactsRepository.class);
        }
        return contactsRes;
    }


    private static OnlineUserRepository getOnlineUserRes() {
        if (onlineUserRes == null) {
            onlineUserRes = MainContext.getContext().getBean(OnlineUserRepository.class);
        }
        return onlineUserRes;

    }

    private static Cache getCache() {
        if (cache == null) {
            cache = MainContext.getCache();
        }

        return cache;
    }

    private static ConsultInviteRepository getConsultInviteRes() {
        if (consultInviteRes == null) {
            consultInviteRes = MainContext.getContext().getBean(ConsultInviteRepository.class);
        }
        return consultInviteRes;
    }

    private static OnlineUserHisRepository getOnlineUserHisRes() {
        if (onlineUserHisRes == null) {
            onlineUserHisRes = MainContext.getContext().getBean(OnlineUserHisRepository.class);
        }
        return onlineUserHisRes;
    }

    private static UserTraceRepository getUserTraceRes() {
        if (userTraceRes == null) {
            userTraceRes = MainContext.getContext().getBean(UserTraceRepository.class);
        }
        return userTraceRes;
    }

    private static UserRepository getUserRes() {
        if (userRes == null) {
            userRes = MainContext.getContext().getBean(UserRepository.class);
        }
        return userRes;
    }

    private static OrganRepository getOrganRes() {
        if (organRes == null) {
            organRes = MainContext.getContext().getBean(OrganRepository.class);
        }
        return organRes;
    }

    private static OrgiSkillRelRepository getOrgiSkillRelRes() {
        if (orgiSkillRelRes == null) {
            orgiSkillRelRes = MainContext.getContext().getBean(OrgiSkillRelRepository.class);
        }
        return orgiSkillRelRes;
    }

    public static UserProxy getUserProxy() {
        if (userProxy == null) {
            userProxy = MainContext.getContext().getBean(UserProxy.class);
        }
        return userProxy;
    }
}
