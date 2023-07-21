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
package com.cskefu.cc.controller.apps;

import com.cskefu.cc.acd.ACDWorkMonitor;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class AppsController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(AppsController.class);

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private PassportWebIMUserRepository onlineUserRes;

    @Autowired
    private UserEventRepository userEventRes;

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private Cache cache;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private ConsultInviteRepository invite;

    @RequestMapping({"/apps/content"})
    @Menu(type = "apps", subtype = "content")
    public ModelAndView content(ModelMap map, HttpServletRequest request, @Valid String msg) {
        final User user = super.getUser(request);
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        List<String> appids = new ArrayList<>();
        if (organs.size() > 0) {
            appids = invite.findSNSIdBySkill(organs.keySet());
        }


        /****************************
         * 获得在线访客列表
         ****************************/

//        TODO 此处为从数据库加载
        final Page<PassportWebIMUser> onlineUserList = onlineUserRes.findByStatusAndAppidIn(
                MainContext.OnlineUserStatusEnum.ONLINE.toString(),
                appids,
                PageRequest.of(
                        super.getP(request),
                        super.getPs(request),
                        Sort.Direction.DESC,
                        "createtime"
                )
        );

        final long msec = System.currentTimeMillis();
        final List<String> contactIds = new ArrayList<>();

        /**
         * 设置访客状态
         *
         */
        for (final PassportWebIMUser passportWebIMUser : onlineUserList.getContent()) {
            passportWebIMUser.setBetweentime((int) (msec - passportWebIMUser.getLogintime().getTime()));
            if (StringUtils.isNotBlank(passportWebIMUser.getContactsid())) {
                contactIds.add(passportWebIMUser.getContactsid());
            }
        }

        /**
         * 获得在线访客与联系人的关联信息
         */
        if (contactIds.size() > 0) {
            final Iterable<Contacts> contacts = contactsRes.findAllById(contactIds);
            for (final PassportWebIMUser passportWebIMUser : onlineUserList.getContent()) {
                if (StringUtils.isNotBlank(passportWebIMUser.getContactsid())) {
                    for (final Contacts contact : contacts) {
                        if (StringUtils.equals(passportWebIMUser.getContactsid(), contact.getId())) {
                            passportWebIMUser.setContacts(contact);
                            break;
                        }
                    }
                }
            }
        }

        map.put("onlineUserList", onlineUserList);
        map.put("msg", msg);
        map.put("now", new Date());

        aggValues(map, request);

        // 获取agentStatus
        map.put("agentStatus", cache.findOneAgentStatusByAgentno(user.getId()));
        return request(super.createView("/apps/desktop/index"));
    }

    private void aggValues(ModelMap map, HttpServletRequest request) {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);

        List<Object> onlineUsers = new ArrayList<>();
        List<Object> userEvents = new ArrayList<>();
        if (organs.size() > 0) {
            List<String> appids = invite.findSNSIdBySkill(organs.keySet());
            if (appids.size() > 0) {
                onlineUsers = onlineUserRes.findByStatusAndInAppIds(
                        MainContext.OnlineUserStatusEnum.ONLINE.toString(), appids);
                userEvents = userEventRes.findByCreatetimeRangeAndInAppIds(MainUtils.getStartTime(),
                        MainUtils.getEndTime(), appids);
            }
        }

        map.put("agentReport", acdWorkMonitor.getAgentReport(currentOrgan != null ? currentOrgan.getId() : null));
        map.put(
                "webIMReport", MainUtils.getWebIMReport(userEvents));

        // TODO 此处为什么不用agentReport中的agents？
        map.put("agents", getUsers(request).size());

        map.put(
                "webIMInvite", MainUtils.getWebIMInviteStatus(onlineUsers));

        map.put(
                "inviteResult", MainUtils.getWebIMInviteResult(
                        onlineUserRes.findByAgentnoAndCreatetimeRange(
                                super.getUser(request).getId(),
                                MainUtils.getStartTime(),
                                MainUtils.getEndTime())));

        map.put(
                "agentUserCount", onlineUserRes.countByAgentForAgentUser(
                        MainContext.AgentUserStatusEnum.INSERVICE.toString(),
                        super.getUser(request).getId(),
                        MainUtils.getStartTime(),
                        MainUtils.getEndTime()));

        map.put(
                "agentServicesCount", onlineUserRes.countByAgentForAgentUser(
                        MainContext.AgentUserStatusEnum.END.toString(),
                        super.getUser(request).getId(),
                        MainUtils.getStartTime(),
                        MainUtils.getEndTime()));

        map.put(
                "agentServicesAvg", onlineUserRes.countByAgentForAvagTime(
                        MainContext.AgentUserStatusEnum.END.toString(),
                        super.getUser(request).getId(),
                        MainUtils.getStartTime(),
                        MainUtils.getEndTime()));

    }

    @RequestMapping({"/apps/onlineuser"})
    @Menu(type = "apps", subtype = "onlineuser")
    public ModelAndView onlineuser(ModelMap map, HttpServletRequest request) {
        Page<PassportWebIMUser> onlineUserList = this.onlineUserRes.findByStatus(MainContext.OnlineUserStatusEnum.ONLINE.toString(),
                PageRequest.of(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime"));
        List<String> ids = new ArrayList<>();
        for (PassportWebIMUser passportWebIMUser : onlineUserList.getContent()) {
            passportWebIMUser.setBetweentime((int) (System.currentTimeMillis() - passportWebIMUser.getLogintime().getTime()));
            if (StringUtils.isNotBlank(passportWebIMUser.getContactsid())) {
                ids.add(passportWebIMUser.getContactsid());
            }
        }
        if (ids.size() > 0) {
            Iterable<Contacts> contactsList = contactsRes.findAllById(ids);
            for (PassportWebIMUser passportWebIMUser : onlineUserList.getContent()) {
                if (StringUtils.isNotBlank(passportWebIMUser.getContactsid())) {
                    for (Contacts contacts : contactsList) {
                        if (passportWebIMUser.getContactsid().equals(contacts.getId())) {
                            passportWebIMUser.setContacts(contacts);
                        }
                    }
                }
            }
        }
        map.put("onlineUserList", onlineUserList);
        aggValues(map, request);

        return request(super.createView("/apps/desktop/onlineuser"));
    }

    @RequestMapping({"/apps/profile"})
    @Menu(type = "apps", subtype = "content")
    public ModelAndView profile(ModelMap map, HttpServletRequest request, @Valid String index) {
        map.addAttribute("userData", super.getUser(request));
        map.addAttribute("index", index);
        return request(super.createView("/apps/desktop/profile"));
    }

    @RequestMapping({"/apps/profile/save"})
    @Menu(type = "apps", subtype = "content")
    public ModelAndView profile(ModelMap map, HttpServletRequest request, @Valid User user, @Valid String index) {
        User tempUser = userRes.findById(user.getId()).orElse(null);
        final User logined = super.getUser(request);
        // 用户名不可修改
        user.setUsername(logined.getUsername());

        if (tempUser != null) {
            String msg = userProxy.validUserUpdate(user, tempUser);
            if (StringUtils.isNotBlank(msg) && (!StringUtils.equals(msg, "edit_user_success"))) {
                // 处理异常返回
                if (StringUtils.isBlank(index)) {
                    return request(super.createView("redirect:/apps/content.html?msg=" + msg));
                }
                return request(super.createView("redirect:/apps/tenant/index.html?msg=" + msg));
            }

            // 执行更新
            tempUser.setUname(user.getUname());
            tempUser.setEmail(user.getEmail());
            tempUser.setMobile(user.getMobile());

            if (logined.isAdmin()) {
                // 作为管理员，强制设置为坐席
                tempUser.setAgent(true);
            }

            final Date now = new Date();
            if (StringUtils.isNotBlank(user.getPassword())) {
                tempUser.setPassword(MainUtils.md5(user.getPassword()));
            }
            if (tempUser.getCreatetime() == null) {
                tempUser.setCreatetime(now);
            }
            tempUser.setUpdatetime(now);
            userRes.save(tempUser);
            User sessionUser = super.getUser(request);
            tempUser.setRoleList(sessionUser.getRoleList());
            tempUser.setRoleAuthMap(sessionUser.getRoleAuthMap());
            tempUser.setAffiliates(sessionUser.getAffiliates());
            User u = tempUser;
            super.setUser(request, u);
            //切换成非坐席 判断是否坐席 以及 是否有对话
            if (!user.isAgent()) {
                AgentStatus agentStatus = cache.findOneAgentStatusByAgentno(
                        (super.getUser(request)).getId());

                if (!(agentStatus == null && cache.getInservAgentUsersSizeByAgentno(
                        super.getUser(request).getId()) == 0)) {
                    if (StringUtils.isBlank(index)) {
                        return request(super.createView("redirect:/apps/content.html?msg=t1"));
                    }
                    return request(super.createView("redirect:/apps/tenant/index.html?msg=t1"));
                }
            }

        }
        if (StringUtils.isBlank(index)) {
            return request(super.createView("redirect:/apps/content.html"));
        }
        return request(super.createView("redirect:/apps/tenant/index.html"));
    }

    /**
     * 获取当前产品下人员信息
     *
     * @param request
     * @return
     */
    private List<User> getUsers(HttpServletRequest request) {
        Map<String, Organ> organs = organProxy.findAllOrganByParent(super.getOrgan(request));
        List<User> userList = userProxy.findByOrganInAndAgentAndDatastatus(organs.keySet(), true, false);
        if (userList == null) {
            userList = new ArrayList<>();
        }

        return userList;
    }

}
