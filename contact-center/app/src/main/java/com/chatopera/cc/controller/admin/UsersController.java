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
package com.chatopera.cc.controller.admin;

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.User;
import com.chatopera.cc.model.UserRole;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.persistence.repository.UserRoleRepository;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.util.Menu;
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

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView index(ModelMap map, HttpServletRequest request) throws IOException {
        map.addAttribute(
                "userList",
                userRepository.findByDatastatusAndOrgiAndOrgidAndSuperadminNot(
                        false,
                        super.getOrgiByTenantshare(request),
                        super.getOrgid(request),
                        true,
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

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/admin/user/edit"));
        view.addObject("userData", userRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request)));
        return view;
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

}