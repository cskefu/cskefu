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

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.Organ;
import com.chatopera.cc.model.OrganUser;
import com.chatopera.cc.model.User;
import com.chatopera.cc.model.UserRole;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.proxy.OrganProxy;
import com.chatopera.cc.proxy.UserProxy;
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
import java.util.Map;

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
    OrganProxy organProxy;

    @Autowired
    UserProxy userProxy;

    @Autowired
    private OrganUserRepository organUserRes;

    @Autowired
    private PbxHostRepository pbxHostRes;

    @Autowired
    private ExtensionRepository extensionRes;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView index(ModelMap map, HttpServletRequest request) throws IOException {
        User logined = super.getUser(request);

        Map<String, Organ> organs = organProxy.findAllOrganByParentAndOrgi(super.getOrgan(request), super.getOrgi(request));
        map.addAttribute("userList", userProxy.findUserInOrgans(organs.keySet(), new PageRequest(
                super.getP(request),
                super.getPs(request),
                Sort.Direction.ASC,
                "createtime"
        )));


        return request(super.createAdminTempletResponse("/admin/user/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/admin/user/add"));
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParentAndOrgi(currentOrgan, super.getOrgi(request));
        map.addAttribute("currentOrgan", currentOrgan);
        map.addAttribute("organList", organs.values());

        return view;
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/admin/user/edit"));
        User user = userRepository.findById(id);
        if (user != null && MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
            // 加载呼叫中心信息
            extensionRes.findByAgentnoAndOrgi(user.getId(), user.getOrgi()).ifPresent(p -> {
                user.setExtensionId(p.getId());
                user.setExtension(p);

                pbxHostRes.findById(p.getHostid()).ifPresent(b -> {
                    user.setPbxhostId(b.getId());
                    user.setPbxHost(b);
                });
            });
        }
        view.addObject("userData", user);
        return view;
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView delete(HttpServletRequest request, @Valid User user) {
        String msg = "admin_user_delete";
        if (user != null) {
            User dbUser = userRepository.getOne(user.getId());
            if (dbUser.isSuperadmin()) {
                msg = "admin_user_abandoned";
            } else {
                // 删除用户的时候，同时删除用户对应的权限数据
                List<UserRole> userRole = userRoleRes.findByOrgiAndUser(super.getOrgi(), user);
                userRoleRes.delete(userRole);
                // 删除用户对应的组织机构关系
                List<OrganUser> organUsers = organUserRes.findByUserid(user.getId());
                organUserRes.delete(organUsers);

                userRepository.delete(dbUser);
            }
        } else {
            msg = "admin_user_not_exist";
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
    }

}