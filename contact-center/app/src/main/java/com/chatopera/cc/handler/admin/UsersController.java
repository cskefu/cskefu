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
package com.chatopera.cc.handler.admin;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.handler.Handler;
import com.chatopera.cc.model.AgentStatus;
import com.chatopera.cc.model.User;
import com.chatopera.cc.model.UserRole;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.persistence.repository.UserRoleRepository;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 */
@Controller
@RequestMapping("/admin/user")
public class UsersController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRes;

    @Autowired
    private Cache cache;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView index(ModelMap map, HttpServletRequest request) throws IOException {
        map.addAttribute(
                "userList",
                userRepository.findByDatastatusAndOrgiAndOrgid(
                        false,
                        super.getOrgiByTenantshare(request),
                        super.getOrgid(request),
                        new PageRequest(
                                super.getP(request),
                                super.getPs(request),
                                Sort.Direction.ASC,
                                "createtime"
                        )
                                                              )
                        );
        return request(super.createAdminTempletResponse("/admin/user/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/user/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView save(HttpServletRequest request, @Valid User user) {
        String msg = "";
        msg = validUser(user);
        if (!StringUtils.isBlank(msg) && !msg.equals("new_user_success")) {
            return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
        } else {
//            if (request.getParameter("admin") != null) {
//                user.setUsertype("0");
//            } else {
//                user.setUsertype(null);
//            }
            if (user.isSuperuser()) {
                user.setSuperuser(true);
                user.setUsertype("0");
            } else {
                user.setSuperuser(false);
                user.setUsertype("");
            }
            if (!StringUtils.isBlank(user.getPassword())) {
                user.setPassword(MainUtils.md5(user.getPassword()));
            }

            user.setOrgi(super.getOrgiByTenantshare(request));
            if (!StringUtils.isBlank(super.getUser(request).getOrgid())) {
                user.setOrgid(super.getUser(request).getOrgid());
            } else {
                user.setOrgid(MainContext.SYSTEM_ORGI);
            }
            userRepository.save(user);
            OnlineUserProxy.clean(super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
    }

    private String validUser(User user) {
        String msg = "";
        User tempUser = userRepository.findByUsernameAndDatastatus(user.getUsername(), false);
        if (tempUser != null) {
            msg = "username_exist";
            return msg;
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            tempUser = userRepository.findByEmailAndDatastatus(user.getEmail(), false);
            if (tempUser != null) {
                msg = "email_exist";
                return msg;
            }
        }

        if (StringUtils.isNotBlank(user.getMobile())) {
            tempUser = userRepository.findByMobileAndDatastatus(user.getMobile(), false);
            if (tempUser != null) {
                msg = "mobile_exist";
                return msg;
            }
        }

        if (!validUserCallcenterParams(user)) {
            msg = "sip_account_exist";
            return msg;
        }

        if (tempUser == null && validUserCallcenterParams(user)) {
            msg = "new_user_success";
            return msg;
        }

        return msg;
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/admin/user/edit"));
        view.addObject("userData", userRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request)));
        return view;
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "user", admin = true)
    public ModelAndView update(HttpServletRequest request, @Valid User user) {
        User tempUser = userRepository.getOne(user.getId());
        String msg = validUserUpdate(user, tempUser);
        if (tempUser != null) {
            if (!StringUtils.isBlank(msg) && !msg.equals("edit_user_success")) {
                return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
            }
            if (user.isSuperuser()) {
                tempUser.setSuperuser(true);
                tempUser.setUsertype("0");
            } else {
                tempUser.setSuperuser(false);
                tempUser.setUsertype("");
            }
            tempUser.setUname(user.getUname());
            tempUser.setUsername(user.getUsername());
            tempUser.setEmail(user.getEmail());
            tempUser.setMobile(user.getMobile());
            tempUser.setSipaccount(user.getSipaccount());
            //切换成非坐席 判断是否坐席 以及 是否有对话
            if (!user.isAgent()) {
                AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                        (super.getUser(request)).getId(), super.getOrgi(request));
                if (!(agentStatus == null && cache.getInservAgentUsersSizeByAgentnoAndOrgi(
                        super.getUser(request).getId(), super.getOrgi(request)) == 0)) {
                    return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=t1"));
                }
            }
            tempUser.setAgent(user.isAgent());

            tempUser.setOrgi(super.getOrgiByTenantshare(request));

            if (!StringUtils.isBlank(super.getUser(request).getOrgid())) {
                tempUser.setOrgid(super.getUser(request).getOrgid());
            } else {
                tempUser.setOrgid(MainContext.SYSTEM_ORGI);
            }

            tempUser.setCallcenter(user.isCallcenter());
            if (!StringUtils.isBlank(user.getPassword())) {
                tempUser.setPassword(MainUtils.md5(user.getPassword()));
            }

//            if (request.getParameter("admin") != null) {
//                tempUser.setUsertype("0");
//            } else {
//                tempUser.setUsertype(null);
//            }

            if (tempUser.getCreatetime() == null) {
                tempUser.setCreatetime(new Date());
            }
            tempUser.setUpdatetime(new Date());
            userRepository.save(tempUser);
            OnlineUserProxy.clean(super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
    }

    private String validUserUpdate(User user, User oldUser) {
        String msg = "edit_user_success";
        User tempUser = userRepository.findByUsernameAndDatastatus(user.getUsername(), false);
        if (tempUser != null && !user.getUsername().equals(oldUser.getUsername())) {
            msg = "username_exist";
            return msg;
        }

        if (StringUtils.isNotBlank(user.getEmail())) {
            tempUser = userRepository.findByEmailAndDatastatus(user.getEmail(), false);
            if (tempUser != null && !user.getEmail().equals(oldUser.getEmail())) {
                msg = "email_exist";
                return msg;
            }
        }

        if (StringUtils.isNotBlank(user.getMobile())) {
            tempUser = userRepository.findByMobileAndDatastatus(user.getMobile(), false);
            if (tempUser != null && !user.getMobile().equals(oldUser.getMobile())) {
                msg = "mobile_exist";
                return msg;
            }
        }

        if (!validUserCallcenterParams(user)) {
            msg = "sip_account_exist";
            return msg;
        }

        if (user.getUsername().equals(oldUser.getUsername()) && user.getEmail().equals(
                oldUser.getEmail()) && user.getMobile().equals(oldUser.getMobile()) && validUserCallcenterParams(
                user)) {
            return "";
        }


//        if ()

        return msg;
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView delete(HttpServletRequest request, @Valid User user) {
        String msg = "admin_user_delete";
        if (user != null) {
            List<UserRole> userRole = userRoleRes.findByOrgiAndUser(super.getOrgiByTenantshare(request), user);
            userRoleRes.delete(userRole);    //删除用户的时候，同时删除用户对应的
            user = userRepository.getOne(user.getId());
            user.setDatastatus(true);
            userRepository.save(user);
            OnlineUserProxy.clean(super.getOrgi(request));
        } else {
            msg = "admin_user_not_exist";
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
    }

    /**
     * 根据是否开启呼叫中心模块检测账号
     *
     * @param user
     * @return
     */
    private boolean validUserCallcenterParams(final User user) {
        if (user.isCallcenter() && MainContext.hasModule(Constants.CSKEFU_MODULE_CALLOUT)) {
            List<User> tempUserList = userRepository.findBySipaccountAndDatastatus(user.getSipaccount(), false);
            return tempUserList.size() == 0 || user.getSipaccount() == "";
        }
        return true;
    }

}