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
package com.cskefu.cc.controller.admin;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.RoleAuthRepository;
import com.cskefu.cc.persistence.repository.RoleRepository;
import com.cskefu.cc.persistence.repository.SysDicRepository;
import com.cskefu.cc.persistence.repository.UserRoleRepository;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.json.GsonTools;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/role")
public class RoleController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRes;

    @Autowired
    private RoleAuthRepository roleAuthRes;

    @Autowired
    private SysDicRepository sysDicRes;

    @Autowired
    OrganProxy organProxy;

    @Autowired
    UserProxy userProxy;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String role, @Valid String msg) {
        Organ currentOrgan = super.getOrgan(request);

        List<Role> roleList = roleRepository.findByOrgi(super.getOrgi());
        map.addAttribute("roleList", roleList);
        map.addAttribute("msg", msg);
        map.addAttribute("currentOrgan", currentOrgan);
        if (roleList.size() > 0) {
            Role roleData = null;
            if (StringUtils.isNotBlank(role)) {
                for (Role data : roleList) {
                    if (data.getId().equals(role)) {
                        roleData = data;
                        map.addAttribute("roleData", data);
                    }
                }
            } else {
                map.addAttribute("roleData", roleData = roleList.get(0));
            }
            if (roleData != null) {
                Map<String, Organ> organs = organProxy.findAllOrganByParentAndOrgi(currentOrgan,
                        super.getOrgi(request));
                // List<String> userIds = userProxy.findUserIdsInOrgans(organs.keySet());

                // Page<UserRole> userRoleList =
                // userRoleRes.findByOrganAndRole(currentOrgan.getId(), roleData,
                // new PageRequest(super.getP(request), super.getPs(request)));

                Page<UserRole> userRoleList = userRoleRes.findByOrganInAndRole(organs.keySet(), roleData,
                        new PageRequest(super.getP(request), super.getPs(request)));

                if (userRoleList.getContent().size() > 0) {
                    for (UserRole ur : userRoleList.getContent()) {
                        organs.values().stream().filter(o -> o.getId().equals(ur.getOrgan())).findFirst()
                                .ifPresent(o -> {
                                    User u = GsonTools.copyObject(ur.getUser());
                                    u.setCurrOrganId(o.getId());
                                    u.setCurrOrganName(o.getName());
                                    ur.setUser(u);
                                });
                    }
                }

                map.addAttribute("userRoleList", userRoleList);
            }
        }
        return request(super.createView("/admin/role/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        return request(super.createView("/admin/role/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView save(HttpServletRequest request, @Valid Role role) {
        Organ currentOrgan = super.getOrgan(request);
        Role tempRole = roleRepository.findByNameAndOrgi(role.getName(), super.getOrgi());
        String msg = "admin_role_save_success";
        if (tempRole != null) {
            msg = "admin_role_save_exist";
        } else {
            role.setOrgi(super.getOrgi());
            role.setCreater(super.getUser(request).getId());
            role.setCreatetime(new Date());
            role.setUpdatetime(new Date());
            role.setOrgan(currentOrgan.getId());
            roleRepository.save(role);
        }
        return request(super.createView("redirect:/admin/role/index.html?msg=" + msg));
    }

    @RequestMapping("/seluser")
    @Menu(type = "admin", subtype = "seluser", admin = true)
    public ModelAndView seluser(ModelMap map, HttpServletRequest request, @Valid String role) {
        Organ currentOrgan = super.getOrgan(request);
        map.addAttribute("userList", userProxy.findUserInOrgans(Arrays.asList(currentOrgan.getId())));
        Role roleData = roleRepository.findByIdAndOrgi(role, super.getOrgi());
        map.addAttribute("userRoleList", userRoleRes.findByOrgiAndRole(super.getOrgi(), roleData));
        map.addAttribute("role", roleData);
        return request(super.createView("/admin/role/seluser"));
    }

    @RequestMapping("/saveuser")
    @Menu(type = "admin", subtype = "saveuser", admin = true)
    public ModelAndView saveuser(HttpServletRequest request, @Valid String[] users, @Valid String role) {
        Organ currentOrgan = super.getOrgan(request);
        Role roleData = roleRepository.findByIdAndOrgi(role, super.getOrgi());
        List<UserRole> userRoleList = userRoleRes.findByOrganAndRole(super.getOrgi(), roleData);
        if (users != null && users.length > 0) {
            for (String user : users) {
                boolean exist = false;
                for (UserRole userRole : userRoleList) {
                    if (user.equals(userRole.getUser().getId())) {
                        exist = true;
                        continue;
                    }
                }
                if (exist == false) {
                    UserRole userRole = new UserRole();
                    userRole.setUser(new User(user));
                    userRole.setRole(new Role(role));
                    userRole.setOrgi(super.getOrgi());
                    userRole.setCreater(super.getUser(request).getId());
                    userRole.setOrgan(currentOrgan.getId());
                    userRoleRes.save(userRole);
                }
            }
        }
        return request(super.createView("redirect:/admin/role/index.html?role=" + role));
    }

    @RequestMapping("/user/delete")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView userroledelete(HttpServletRequest request, @Valid String id, @Valid String role) {
        if (role != null) {
            userRoleRes.delete(id);
        }
        return request(super.createView("redirect:/admin/role/index.html?role=" + role));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createView("/admin/role/edit"));
        view.addObject("roleData", roleRepository.findByIdAndOrgi(id, super.getOrgi()));
        return view;
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView update(HttpServletRequest request, @Valid Role role) {
        Role tempRoleExist = roleRepository.findByNameAndOrgi(role.getName(), super.getOrgi());
        String msg = "";
        if (tempRoleExist == null) {
            msg = "admin_role_update_success";
            Role tempRole = roleRepository.findByIdAndOrgi(role.getId(), super.getOrgi());
            tempRole.setName(role.getName());
            tempRole.setUpdatetime(new Date());
            roleRepository.save(tempRole);
        } else if (!role.getId().equals(tempRoleExist.getId())) {
            msg = "admin_role_update_not_exist";
        }
        return request(super.createView("redirect:/admin/role/index.html?msg=" + msg));
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView delete(HttpServletRequest request, @Valid Role role) {
        String msg = "admin_role_delete";
        if (role != null) {
            userRoleRes.delete(userRoleRes.findByOrgiAndRole(super.getOrgi(), role));
            roleRepository.delete(role);
        } else {
            msg = "admin_role_not_exist";
        }
        return request(super.createView("redirect:/admin/role/index.html?msg=" + msg));
    }

    @RequestMapping("/auth")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView auth(ModelMap map, final HttpServletRequest request, final @Valid String id) {
        logger.info("[auth] role id {}", id);
        SysDic sysDic = sysDicRes.findByCode(Constants.CSKEFU_SYSTEM_AUTH_DIC);
        if (sysDic != null) {
            map.addAttribute("resourceList", sysDicRes.findByDicid(sysDic.getId()));
        }
        map.addAttribute("sysDic", sysDic);
        Role role = roleRepository.findByIdAndOrgi(id, super.getOrgi());
        map.addAttribute("role", role);
        map.addAttribute("roleAuthList", roleAuthRes.findByRoleidAndOrgi(role.getId(), super.getOrgi()));
        return request(super.createView("/admin/role/auth"));
    }

    @RequestMapping("/auth/save")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView authsave(HttpServletRequest request, @Valid String id, @Valid String menus) {
        // logger.info("[authsave] id {}, menus {}", id, menus);

        List<RoleAuth> roleAuthList = roleAuthRes.findByRoleidAndOrgi(id, super.getOrgi());
        roleAuthRes.delete(roleAuthList);
        if (StringUtils.isNotBlank(menus)) {
            String[] menuarray = menus.split(",");

            logger.info("[authsave] menus: {}", menus);

            for (String menu : menuarray) {
                RoleAuth roleAuth = new RoleAuth();

                roleAuth.setRoleid(id);
                roleAuth.setDicid(menu);
                SysDic sysDic = Dict.getInstance().getDicItem(menu);

                if (sysDic != null && (!StringUtils.equals(sysDic.getParentid(), "0"))) {
                    logger.debug("[authsave] get sysdict {}, code {}, name {}, parent {}", sysDic.getId(),
                            sysDic.getCode(), sysDic.getName(), sysDic.getParentid());
                    roleAuth.setCreater(super.getUser(request).getId());
                    roleAuth.setOrgi(super.getOrgi());
                    roleAuth.setCreatetime(new Date());
                    roleAuth.setName(sysDic.getName());
                    roleAuth.setDicvalue(sysDic.getCode());
                    logger.debug("[authsave] save role auth {}", roleAuth.toString());
                    roleAuthRes.save(roleAuth);
                }
            }
        }
        return request(super.createView("redirect:/admin/role/index.html?role=" + id));
    }
}
