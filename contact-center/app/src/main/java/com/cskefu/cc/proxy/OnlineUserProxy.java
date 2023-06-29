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
package com.cskefu.cc.proxy;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.util.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.text.SimpleDateFormat;
import java.util.*;


public class OnlineUserProxy {
    private final static Logger logger = LoggerFactory.getLogger(OnlineUserProxy.class);
    public static final WebSseEmitterClient webIMClients = new WebSseEmitterClient();
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private static PassportWebIMUserRepository onlineUserRes;
    private static UserRepository userRes;
    private static Cache cache;
    private static ConsultInviteRepository consultInviteRes;
    private static PassportWebIMUserHistRepository onlineUserHisRes;
    private static UserTraceRepository userTraceRes;
    private static AgentUserContactsRepository agentUserContactsRes;
    private static ContactsRepository contactsRes;
    private static UserProxy userProxy;
    private static OrganRepository organRes;

    /**
     * @param id
     * @return
     * @throws Exception
     */
    public static PassportWebIMUser user(final String id) {
        return getOnlineUserRes().findById(id).orElse(null);
    }

    /**
     * 更新cache
     *
     * @param consultInvite
     */
    public static void cacheConsult(final CousultInvite consultInvite) {
        logger.info("[cacheConsult] snsid {}", consultInvite.getSnsaccountid());
        getCache().putConsultInvite(consultInvite);
    }

    /**
     * @param snsid
     * @return
     */
    public static CousultInvite consult(final String snsid) {
        CousultInvite consultInvite = MainContext.getCache().findOneConsultInviteBySnsid(snsid);
        if (consultInvite == null) {
            consultInvite = getConsultInviteRes().findBySnsaccountid(snsid);
            if (consultInvite != null) {
                getCache().putConsultInvite(consultInvite);
            }
        }
        return consultInvite;
    }


    /**
     * 在Cache中查询OnlineUser，或者从数据库中根据UserId，Orgi和Invite查询
     *
     * @param userid
     * @return
     */
    public static PassportWebIMUser onlineuser(String userid) {
        // 从Cache中查找
        PassportWebIMUser passportWebIMUser = getCache().findOneOnlineUserByUserId(userid);

        if (passportWebIMUser == null) {
            logger.info(
                    "[onlineuser] !!! fail to resolve user {} with both cache and database, maybe this user is first presents.",
                    userid);
        }

        return passportWebIMUser;
    }


    /**
     * @param ipdata
     * @param invite
     * @param isJudgeShare 是否判断是否共享租户
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Organ> organ(final IP ipdata,
                                    final CousultInvite invite, boolean isJudgeShare) {
        boolean isShare = false;
        if (isJudgeShare) {
            SystemConfig systemConfig = MainUtils.getSystemConfig();
            if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
                isShare = true;
            }
        }
        List<Organ> skillGroups = getCache().findOneSystemById(Constants.CACHE_SKILL);
        if (skillGroups == null) {
            OrganRepository service = MainContext.getContext().getBean(OrganRepository.class);
            skillGroups = service.findBySkill(true);
            // 租户共享时 查出该租住要显的绑定的技能组
            if (isShare) {
                OrgiSkillRelRepository skillRelService = MainContext.getContext().getBean(
                        OrgiSkillRelRepository.class);
                List<OrgiSkillRel> skillRelList = null;
                skillRelList = skillRelService.findAll();
                List<Organ> skillTempList = new ArrayList<>();
                if (!skillRelList.isEmpty()) {
                    for (Organ organ : skillGroups) {
                        for (OrgiSkillRel rel : skillRelList) {
                            if (organ.getId().equals(rel.getSkillid())) {
                                skillTempList.add(organ);
                            }
                        }
                    }
                }
                skillGroups = skillTempList;
            }

            if (skillGroups.size() > 0) {
                getCache().putSystemListById(Constants.CACHE_SKILL, skillGroups);
            }
        }

        if (ipdata == null && invite == null) {
            return skillGroups;
        }

        List<Organ> regOrganList = new ArrayList<>();
        for (Organ organ : skillGroups) {
            if (StringUtils.isNotBlank(organ.getArea())) {
                if (organ.getArea().contains(ipdata.getProvince()) || organ.getArea().contains(ipdata.getCity())) {
                    regOrganList.add(organ);
                }
            } else {
                regOrganList.add(organ);
            }
        }
        return regOrganList;
    }


    /**
     * @param isJudgeShare
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Organ> organ(boolean isJudgeShare) {
        return organ(null, null, isJudgeShare);
    }

    private static List<AreaType> getAreaTypeList(String area, List<AreaType> areaTypeList) {
        List<AreaType> atList = new ArrayList<>();
        if (areaTypeList != null && areaTypeList.size() > 0) {
            for (AreaType areaType : areaTypeList) {
                if (StringUtils.isNotBlank(area) && area.contains(areaType.getId())) {
                    atList.add(areaType);
                }
            }
        }
        return atList;
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<User> agents() {
        List<User> agentList = getUserRes().findByAgentAndDatastatus(true, false);
        List<User> agentTempList = new ArrayList<>();
        List<Organ> skillOrgansByOrgi = getOrganRes().findBySkill(true);

        if (!skillOrgansByOrgi.isEmpty()) {
            for (User user : agentList) {
                // 跳过超级管理员角色用户，不显示在技能组列表
                if (user.isSuperadmin()) continue;

                // 只显示在线的客服，跳过离线的客服
                if (getCache().findOneAgentStatusByAgentno(user.getId()) == null) continue;

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
                Page<Contacts> contactsList = contactsRes.findByDatastatus(
                        false, PageRequest.of(0, 1));
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
                if (!getAgentUserContactsRes().findOneByUserid(userid).isPresent()) {
                    AgentUserContacts agentUserContacts = new AgentUserContacts();
                    agentUserContacts.setAppid(appid);
                    agentUserContacts.setChanneltype(MainContext.ChannelType.WEBIM.toString());
                    agentUserContacts.setContactsid(contacts.getId());
                    agentUserContacts.setUserid(userid);
                    agentUserContacts.setCreatetime(new Date());
                    agentUserContactsRes.save(agentUserContacts);
                }
            } else if (StringUtils.isNotBlank(userid)) {
                Optional<AgentUserContacts> agentUserContactOpt = agentUserContactsRes.findOneByUserid(
                        userid);
                if (agentUserContactOpt.isPresent()) {
                    contacts = getContactsRes().findById(agentUserContactOpt.get().getContactsid()).orElse(null);
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
     * @param sessionid
     * @param optype
     * @param request
     * @param channel
     * @param appid
     * @param contacts
     * @param invite
     * @throws CharacterCodingException
     */
    public static void online(
            final User user,
            final String sessionid,
            final String optype,
            final HttpServletRequest request,
            final String channel,
            final String appid,
            final Contacts contacts,
            final CousultInvite invite) {
//        logger.info(
//                "[online] user {}, sessionid {}, optype {}, channel {}", user.getId(), sessionid, optype,
//                channel);
        PassportWebIMUser passportWebIMUser = null;
        final Date now = new Date();
        if (invite != null) {
            // resolve user from cache or db.
            passportWebIMUser = onlineuser(user.getId());

            if (passportWebIMUser == null) {
//                logger.info("[online] create new online user.");
                passportWebIMUser = new PassportWebIMUser();
                passportWebIMUser.setId(user.getId());
                passportWebIMUser.setCreater(user.getId());
                passportWebIMUser.setUsername(user.getUsername());
                passportWebIMUser.setCreatetime(now);
                passportWebIMUser.setUpdatetime(now);
                passportWebIMUser.setUpdateuser(user.getUsername());
                passportWebIMUser.setSessionid(sessionid);

                if (contacts != null) {
                    passportWebIMUser.setContactsid(contacts.getId());
                }

                passportWebIMUser.setChannel(channel);

                // 从Server session信息中查找该用户相关的历史信息
                String cookie = getCookie(request, "R3GUESTUSEKEY");
                if ((StringUtils.isBlank(cookie))
                        || (StringUtils.equals(user.getSessionid(), cookie))) {
                    passportWebIMUser.setOlduser("0");
                } else {
                    // 之前有session的访客
                    passportWebIMUser.setOlduser("1");
                }
                passportWebIMUser.setMobile(MobileDevice.isMobile(request
                        .getHeader("User-Agent")) ? "1" : "0");

                // onlineUser.setSource(user.getId());

                String url = request.getHeader("referer");
                passportWebIMUser.setUrl(url);
                if (StringUtils.isNotBlank(url)) {
                    try {
                        URL referer = new URL(url);
                        passportWebIMUser.setSource(referer.getHost());
                    } catch (MalformedURLException e) {
                        logger.info("[online] error when parsing URL", e);
                    }
                }
                passportWebIMUser.setAppid(appid);
                passportWebIMUser.setUserid(user.getId());
                passportWebIMUser.setUsername(user.getUsername());

                if (StringUtils.isNotBlank(request.getParameter("title"))) {
                    String title = request.getParameter("title");
                    if (title.length() > 255) {
                        passportWebIMUser.setTitle(title.substring(0, 255));
                    } else {
                        passportWebIMUser.setTitle(title);
                    }
                }

                passportWebIMUser.setLogintime(now);

                // 地理信息
                String ip = MainUtils.getIpAddr(request);
                passportWebIMUser.setIp(ip);
                IP ipdata = IPTools.getInstance().findGeography(ip);
                passportWebIMUser.setCountry(ipdata.getCountry());
                passportWebIMUser.setProvince(ipdata.getProvince());
                passportWebIMUser.setCity(ipdata.getCity());
                passportWebIMUser.setIsp(ipdata.getIsp());
                passportWebIMUser.setRegion(ipdata.toString() + "（"
                        + ip + "）");

                passportWebIMUser.setDatestr(new SimpleDateFormat("yyyMMdd")
                        .format(now));

                passportWebIMUser.setHostname(ip);
                passportWebIMUser.setSessionid(sessionid);
                passportWebIMUser.setOptype(optype);
                passportWebIMUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString());
                final BrowserClient client = MainUtils.parseClient(request);

                // 浏览器信息
                passportWebIMUser.setOpersystem(client.getOs());
                passportWebIMUser.setBrowser(client.getBrowser());
                passportWebIMUser.setUseragent(client.getUseragent());

                logger.info("[online] new online user is created but not persisted.");
            } else {
                // 从DB或缓存找到OnlineUser
                passportWebIMUser.setCreatetime(now); // 刷新创建时间
                if ((StringUtils.isNotBlank(passportWebIMUser.getSessionid()) && !StringUtils.equals(
                        passportWebIMUser.getSessionid(), sessionid)) ||
                        !StringUtils.equals(
                                MainContext.OnlineUserStatusEnum.ONLINE.toString(), passportWebIMUser.getStatus())) {
                    // 当新的session与从DB或缓存查找的session不一致时，或者当数据库或缓存的OnlineUser状态不是ONLINE时
                    // 代表该用户登录了新的Session或从离线变为上线！

                    passportWebIMUser.setStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString()); // 设置用户到上线
                    passportWebIMUser.setChannel(channel);          // 设置渠道
                    passportWebIMUser.setAppid(appid);
                    passportWebIMUser.setUpdatetime(now);           // 刷新更新时间
                    if (StringUtils.isNotBlank(passportWebIMUser.getSessionid()) && !StringUtils.equals(
                            passportWebIMUser.getSessionid(), sessionid)) {
                        passportWebIMUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                        passportWebIMUser.setSessionid(sessionid);  // 设置新的session信息
                        passportWebIMUser.setLogintime(now);        // 设置更新时间
                        passportWebIMUser.setInvitetimes(0);        // 重置邀请次数
                    }
                }

                // 处理联系人关联信息
                if (contacts != null) {
                    // 当关联到联系人
                    if (StringUtils.isNotBlank(contacts.getId()) && StringUtils.isNotBlank(
                            contacts.getName()) && (StringUtils.isBlank(
                            passportWebIMUser.getContactsid()) || !contacts.getName().equals(passportWebIMUser.getUsername()))) {
                        if (StringUtils.isBlank(passportWebIMUser.getContactsid())) {
                            passportWebIMUser.setContactsid(contacts.getId());
                        }
                        if (!contacts.getName().equals(passportWebIMUser.getUsername())) {
                            passportWebIMUser.setUsername(contacts.getName());
                        }
                        passportWebIMUser.setUpdatetime(now);
                    }
                }

                if (StringUtils.isBlank(passportWebIMUser.getUsername()) && StringUtils.isNotBlank(user.getUsername())) {
                    passportWebIMUser.setUseragent(user.getUsername());
                    passportWebIMUser.setUpdatetime(now);
                }
            }

            if (invite.isRecordhis() && StringUtils.isNotBlank(request.getParameter("traceid"))) {
                UserTraceHistory trace = new UserTraceHistory();
                trace.setId(request.getParameter("traceid"));
                trace.setTitle(request.getParameter("title"));
                trace.setUrl(request.getParameter("url"));
                trace.setUpdatetime(new Date());
                trace.setUsername(passportWebIMUser.getUsername());
                getUserTraceRes().save(trace);
            }

            // 完成获取及更新OnlineUser, 将信息加入缓存
            if (passportWebIMUser != null && StringUtils.isNotBlank(passportWebIMUser.getUserid())) {
//                logger.info(
//                        "[online] onlineUser id {}, status {}, invite status {}", onlineUser.getId(),
//                        onlineUser.getStatus(), onlineUser.getInvitestatus());
                // 存储到缓存及数据库
                getOnlineUserRes().save(passportWebIMUser);
            }
        }
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
     * @throws Exception
     */
    public static void offline(String user) {
        if (MainContext.getContext() != null) {
            PassportWebIMUser passportWebIMUser = getCache().findOneOnlineUserByUserId(user);
            if (passportWebIMUser != null) {
                passportWebIMUser.setStatus(MainContext.OnlineUserStatusEnum.OFFLINE.toString());
                passportWebIMUser.setInvitestatus(MainContext.OnlineUserInviteStatus.DEFAULT.toString());
                passportWebIMUser.setBetweentime((int) (new Date().getTime() - passportWebIMUser.getLogintime().getTime()));
                passportWebIMUser.setUpdatetime(new Date());
                getOnlineUserRes().save(passportWebIMUser);

                final PassportWebIMUserHist his = getOnlineUserHisRes().findOneBySessionid(
                        passportWebIMUser.getSessionid()).orElseGet(PassportWebIMUserHist::new);
                MainUtils.copyProperties(passportWebIMUser, his);
                his.setDataid(passportWebIMUser.getId());
                getOnlineUserHisRes().save(his);
            }
            getCache().deleteOnlineUserById(user);
        }
    }

    /**
     * 设置onlineUser为离线
     *
     * @param passportWebIMUser
     * @throws Exception
     */
    public static void offline(PassportWebIMUser passportWebIMUser) {
        if (passportWebIMUser != null) {
            offline(passportWebIMUser.getId());
        }
    }

    /**
     * @param user
     * @throws Exception
     */
    public static void refuseInvite(final String user) {
        PassportWebIMUser passportWebIMUser = getOnlineUserRes().findById(user).orElse(null);
        if (passportWebIMUser != null) {
            passportWebIMUser.setInvitestatus(MainContext.OnlineUserInviteStatus.REFUSE.toString());
            passportWebIMUser.setRefusetimes(passportWebIMUser.getRefusetimes() + 1);
            getOnlineUserRes().save(passportWebIMUser);
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
        Map<String, String[]> values = new HashMap<>();
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
    public static PassportWebIMUser createNewOnlineUserWithContactAndChannel(final Contacts contact, final User logined, final String channel) {
        final Date now = new Date();
        PassportWebIMUser passportWebIMUser = new PassportWebIMUser();
        passportWebIMUser.setId(MainUtils.getUUID());
        passportWebIMUser.setUserid(passportWebIMUser.getId());
        passportWebIMUser.setLogintime(now);
        passportWebIMUser.setUpdateuser(logined.getId());
        passportWebIMUser.setContactsid(contact.getId());
        passportWebIMUser.setUsername(contact.getName());
        passportWebIMUser.setChannel(channel);
        passportWebIMUser.setCity(contact.getCity());
        passportWebIMUser.setCreater(logined.getId());

        logger.info(
                "[createNewOnlineUserWithContactAndChannel] onlineUser id {}, userId {}", passportWebIMUser.getId(),
                passportWebIMUser.getUserid());
        // TODO 此处没有创建 onlineUser 的 appid
        getOnlineUserRes().save(passportWebIMUser);
        return passportWebIMUser;

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


    private static PassportWebIMUserRepository getOnlineUserRes() {
        if (onlineUserRes == null) {
            onlineUserRes = MainContext.getContext().getBean(PassportWebIMUserRepository.class);
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

    private static PassportWebIMUserHistRepository getOnlineUserHisRes() {
        if (onlineUserHisRes == null) {
            onlineUserHisRes = MainContext.getContext().getBean(PassportWebIMUserHistRepository.class);
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

    public static UserProxy getUserProxy() {
        if (userProxy == null) {
            userProxy = MainContext.getContext().getBean(UserProxy.class);
        }
        return userProxy;
    }
}
