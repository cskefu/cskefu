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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author <a href="http://blog.didispace.com>程序猿DD</a>
 * @version 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UsersController extends Handler {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final UserRoleRepository userRoleRes;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        map.addAttribute(
                "userList",
                userRepository.findByDatastatusAndOrgiAndOrgidAndSuperadminNot(
                        false,
                        super.getOrgiByTenantshare(request),
                        super.getOrgid(request),
                        true,
                        PageRequest.of(
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
    public ModelAndView add() {
        return request(super.createRequestPageTempletResponse("/admin/user/add"));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView edit(HttpServletRequest request, @Valid String id) {
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
            userRoleRes.deleteAll(userRole);    //删除用户的时候，同时删除用户对应的
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
