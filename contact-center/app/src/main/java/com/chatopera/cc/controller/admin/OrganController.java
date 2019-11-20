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
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.proxy.OrganProxy;
import com.chatopera.cc.proxy.UserProxy;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 */
@Controller
@RequestMapping("/admin/organ")
public class OrganController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(OrganController.class);

    @Autowired
    private OrganRepository organRepository;

    @Autowired
    private OrganUserRepository organUserRes;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SysDicRepository sysDicRepository;

    @Autowired
    private AreaTypeRepository areaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganRoleRepository organRoleRes;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private Cache cache;

    @Autowired
    private UserProxy userProxy;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String organ, @Valid String msg) {
        List<Organ> organList = organRepository.findByOrgiAndOrgid(
                super.getOrgiByTenantshare(request), super.getOrgid(request));
        map.addAttribute("organList", organList);
        if (organList.size() > 0) {
            Organ organData = null;
            if (!StringUtils.isBlank(organ) && !"null".equals(organ)) {
                for (Organ data : organList) {
                    if (data.getId().equals(organ)) {
                        map.addAttribute("organData", data);
                        organData = data;
                    }
                }
            } else {
                map.addAttribute("organData", organData = organList.get(0));
            }
            if (organData != null) {
                map.addAttribute(
                        "userList", userProxy.findByOrganAndOrgiAndDatastatus(
                                organData.getId(),
                                super.getOrgiByTenantshare(request),
                                false));
            }
        }
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgiByTenantshare(request)));
        map.addAttribute(
                "roleList", roleRepository.findByOrgiAndOrgid(
                        super.getOrgiByTenantshare(request),
                        super.getOrgid(request)));
        map.put("msg", msg);
        return request(super.createAdminTempletResponse("/admin/organ/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String parent, @Valid String area) {
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgiByTenantshare(request)));
        if (!StringUtils.isBlank(parent)) {
            map.addAttribute("organ", organRepository.findByIdAndOrgi(parent, super.getOrgiByTenantshare(request)));
        }
        if (!StringUtils.isBlank(area)) {
            map.addAttribute("area", areaRepository.findByIdAndOrgi(area, super.getOrgiByTenantshare(request)));
        }

        map.addAttribute(
                "organList", organRepository.findByOrgiAndOrgid(
                        super.getOrgiByTenantshare(request),
                        super.getOrgid(request)));

        return request(super.createRequestPageTempletResponse("/admin/organ/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView save(HttpServletRequest request, @Valid Organ organ) {
        Organ tempOrgan = organRepository.findByNameAndOrgiAndOrgid(
                organ.getName(), super.getOrgiByTenantshare(request), super.getOrgid(request));
        String msg = "admin_organ_new_success";
        String firstId = null;
        if (tempOrgan != null) {
            msg = "admin_organ_update_name_not"; //分类名字重复
        } else {
            organ.setOrgi(super.getOrgiByTenantshare(request));

            if (!StringUtils.isBlank(super.getUser(request).getOrgid())) {
                organ.setOrgid(super.getUser(request).getOrgid());
            } else {
                organ.setOrgid(MainContext.SYSTEM_ORGI);
            }
            firstId = organ.getId();

            organRepository.save(organ);

            OnlineUserProxy.clean(super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse(
                "redirect:/admin/organ/index.html?msg=" + msg + "&organ=" + firstId));
    }

    /**
     * 添加用户到当前部门时选择坐席
     *
     * @param map
     * @param request
     * @param organ
     * @return
     */
    @RequestMapping("/seluser")
    @Menu(type = "admin", subtype = "seluser", admin = true)
    public ModelAndView seluser(ModelMap map, HttpServletRequest request, @Valid String organ) {
        map.addAttribute(
                "userList", userRepository.findByOrgiAndDatastatusAndOrgid(super.getOrgiByTenantshare(request), false,
                                                                           super.getOrgid(request)));
        Organ organData = organRepository.findByIdAndOrgi(organ, super.getOrgiByTenantshare(request));
        map.addAttribute("userOrganList", userProxy
                .findByOrganAndOrgiAndDatastatus(organ, super.getOrgiByTenantshare(request), false));
        map.addAttribute("organ", organData);
        return request(super.createRequestPageTempletResponse("/admin/organ/seluser"));
    }


    /**
     * 执行添加用户到组织中
     *
     * @param request
     * @param users
     * @param organ
     * @return
     */
    @RequestMapping("/saveuser")
    @Menu(type = "admin", subtype = "saveuser", admin = true)
    public ModelAndView saveuser(
            HttpServletRequest request,
            final @Valid String[] users,
            final @Valid String organ
                                ) {
        logger.info("[saveuser] save users {} into organ {}", StringUtils.join(users, ","), organ);
        final User loginUser = super.getUser(request);

        if (users != null && users.length > 0) {
            List<String> chosen = new ArrayList<String>(Arrays.asList(users));
            Organ organData = organRepository.findByIdAndOrgi(organ, super.getOrgiByTenantshare(request));
            List<User> organUserList = userRepository.findAll(chosen);
            for (final User user : organUserList) {
                OrganUser ou = organUserRes.findByUseridAndOrgan(user.getId(), organ);

                /**
                 * 检查人员和技能组关系
                 */
                if (organData.isSkill()) {
                    // 该组织机构是技能组
                    if (!user.isAgent()) {
                        // 该人员不是坐席
                        if (ou != null) {
                            organUserRes.delete(ou);
                        }
                        continue;
                    }
                }

                if (ou == null) {
                    ou = new OrganUser();
                }

                ou.setCreator(loginUser.getId());
                ou.setUserid(user.getId());
                ou.setOrgan(organ);

                organUserRes.save(ou);

                if (user.isAgent()) {
                    /**
                     * 以下更新技能组状态
                     */
                    AgentStatus agentStatus = cache.findOneAgentStatusByAgentnoAndOrig(
                            user.getId(), super.getOrgiByTenantshare(request));

                    // TODO 因为一个用户可以包含在多个技能组中，所以，skill应该对应
                    // 一个List列表，此处需要重构Skill为列表
                    if (agentStatus != null) {
                        userProxy.attachOrgansPropertiesForUser(user);
                        agentStatus.setSkills(user.getSkills());
                        cache.putAgentStatusByOrgi(agentStatus, super.getOrgiByTenantshare(request));
                    }
                }
            }
            userRepository.save(organUserList);
            OnlineUserProxy.clean(super.getOrgi(request));
        }

        return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?organ=" + organ));
    }

    @RequestMapping("/user/delete")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView userroledelete(
            final HttpServletRequest request,
            final @Valid String id,
            final @Valid String organ
                                      ) {
        logger.info("[userroledelete] user id {}, organ {}", id, organ);
        if (id != null) {
            organUserRes.deleteOrganUserByUseridAndOrgan(id, organ);
            OnlineUserProxy.clean(super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?organ=" + organ));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/admin/organ/edit"));
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgiByTenantshare(request)));
        view.addObject("organData", organRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request)));

        map.addAttribute(
                "organList", organRepository.findByOrgiAndOrgid(
                        super.getOrgiByTenantshare(request),
                        super.getOrgid(request)));
        return view;
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView update(HttpServletRequest request, @Valid Organ organ) {
        String msg = organProxy.updateOrgan(organ, super.getOrgi(request), super.getUser(request));
        return request(super.createRequestPageTempletResponse(
                "redirect:/admin/organ/index.html?msg=" + msg + "&organ=" + organ.getId()));
    }

    @RequestMapping("/area")
    @Menu(type = "admin", subtype = "area")
    public ModelAndView area(ModelMap map, HttpServletRequest request, @Valid String id) {

        SysDic sysDic = sysDicRepository.findByCode(Constants.CSKEFU_SYSTEM_AREA_DIC);
        if (sysDic != null) {
            map.addAttribute("sysarea", sysDic);
            map.addAttribute("areaList", sysDicRepository.findByDicid(sysDic.getId()));
        }
        map.addAttribute("cacheList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_AREA_DIC));

        map.addAttribute("organData", organRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request)));
        return request(super.createRequestPageTempletResponse("/admin/organ/area"));
    }


    @RequestMapping("/area/update")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView areaupdate(HttpServletRequest request, @Valid Organ organ) {
        Organ tempOrgan = organRepository.findByIdAndOrgi(organ.getId(), super.getOrgiByTenantshare(request));
        String msg = "admin_organ_update_success";
        if (tempOrgan != null) {
            tempOrgan.setArea(organ.getArea());
            organRepository.save(tempOrgan);
            OnlineUserProxy.clean(super.getOrgi(request));
        } else {
            msg = "admin_organ_update_not_exist";
        }
        return request(super.createRequestPageTempletResponse(
                "redirect:/admin/organ/index.html?msg=" + msg + "&organ=" + organ.getId()));
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView delete(HttpServletRequest request, @Valid Organ organ) {
        String msg = "admin_organ_delete";

        Organ organSelf = organRepository.findByIdAndOrgi(organ.getId(), super.getOrgiByTenantshare(request));
        List<Organ> organParentAre = organRepository.findByOrgiAndParent(organSelf.getOrgi(), organSelf.getId());
        if (organ != null && organParentAre != null && organParentAre.size() > 0) {
            msg = "admin_oran_not_delete";
        } else if (organ != null) {
            List<OrganUser> organUsers = organUserRes.findByOrgan(organ.getId());
            organUserRes.deleteInBatch(organUsers);
            organRepository.delete(organ);
            OnlineUserProxy.clean(super.getOrgi(request));
        } else {
            msg = "admin_organ_not_exist";
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?msg=" + msg));
    }

    @RequestMapping("/auth/save")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView authsave(HttpServletRequest request, @Valid String id, @Valid String menus) {
        Organ organData = organRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request));
        List<OrganRole> organRoleList = organRoleRes.findByOrgiAndOrgan(super.getOrgiByTenantshare(request), organData);
        organRoleRes.delete(organRoleList);
        if (!StringUtils.isBlank(menus)) {
            String[] menusarray = menus.split(",");
            for (String menu : menusarray) {
                OrganRole organRole = new OrganRole();
                SysDic sysDic = Dict.getInstance().getDicItem(menu);
                if (sysDic != null && !"0".equals(sysDic.getParentid())) {
                    organRole.setDicid(menu);
                    organRole.setDicvalue(sysDic.getCode());

                    organRole.setOrgan(organData);
                    organRole.setCreater(super.getUser(request).getId());
                    organRole.setOrgi(super.getOrgiByTenantshare(request));
                    organRole.setCreatetime(new Date());
                    organRoleRes.save(organRole);
                }

            }
        }
        return request(
                super.createRequestPageTempletResponse("redirect:/admin/organ/index.html?organ=" + organData.getId()));
    }
}