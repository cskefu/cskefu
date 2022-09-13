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
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.json.GsonTools;
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
import java.util.*;

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

    private Collection<Organ> getOwnOragans(HttpServletRequest request) {
        Organ currentOrgan = super.getOrgan(request);
        return organProxy.findAllOrganByParentAndOrgi(currentOrgan, super.getOrgi()).values();
    }

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String organ, @Valid String msg) {
        Organ currentOrgan = super.getOrgan(request);
        List<Organ> organList = organRepository.findByOrgi(super.getOrgi());
        map.addAttribute("organList", getOwnOragans(request));
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
                map.addAttribute("organData", organData = currentOrgan);
            }
            if (organData != null) {
                map.addAttribute(
                        "userList", userProxy.findByOrganAndOrgiAndDatastatus(
                                organData.getId(),
                                super.getOrgi(),
                                false));

                // 处理附属组织
                final Map<String, Organ> affiliates = organProxy.findAllOrganByParentAndOrgi(organData,
                        super.getOrgi());
                List<User> affiliateUsers = new ArrayList<>();

                for (final Map.Entry<String, Organ> o : affiliates.entrySet()) {
                    if (StringUtils.equals(o.getKey(), organData.getId()))
                        continue;
                    List<User> ousers = userProxy.findByOrganAndOrgiAndDatastatus(
                            o.getKey(),
                            super.getOrgi(),
                            false);
                    if (ousers != null && ousers.size() > 0) {
                        for (User u : ousers) {
                            u.setCurrOrganId(o.getKey());
                            u.setCurrOrganName(o.getValue().getName());
                            // copy an object to avoid modify multi times
                            affiliateUsers.add(GsonTools.copyObject(u));
                        }
                    }
                }
                map.addAttribute("affiliateUsers", affiliateUsers);

            }
        }
        map.addAttribute("currentOrgan", currentOrgan);
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgi()));
        map.addAttribute("roleList", roleRepository.findByOrgi(super.getOrgi()));
        map.put("msg", msg);
        return request(super.createView("/admin/organ/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String parent, @Valid String area) {
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgi()));
        if (!StringUtils.isBlank(parent)) {
            map.addAttribute("organ", organRepository.findByIdAndOrgi(parent, super.getOrgi()));
        }
        if (!StringUtils.isBlank(area)) {
            map.addAttribute("area", areaRepository.findByIdAndOrgi(area, super.getOrgi()));
        }

        map.addAttribute("organList", getOwnOragans(request));

        return request(super.createView("/admin/organ/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView save(HttpServletRequest request, @Valid Organ organ) {
        Organ tempOrgan = organRepository.findByNameAndOrgi(organ.getName(), super.getOrgi(request));
        String msg = "admin_organ_new_success";
        String firstId = null;
        if (tempOrgan != null) {
            msg = "admin_organ_update_name_not"; // 分类名字重复
        } else {
            organ.setOrgi(super.getOrgi());
            firstId = organ.getId();

            organRepository.save(organ);
        }
        return request(super.createView(
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
        Map<String, Organ> organs = organProxy.findAllOrganByParentAndOrgi(super.getOrgan(request),
                super.getOrgi(request));
        map.addAttribute("userList", userProxy.findUserInOrgans(organs.keySet()));
        Organ organData = organRepository.findByIdAndOrgi(organ, super.getOrgi());
        map.addAttribute("userOrganList", userProxy
                .findByOrganAndOrgiAndDatastatus(organ, super.getOrgi(), false));
        map.addAttribute("organ", organData);
        return request(super.createView("/admin/organ/seluser"));
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
            final @Valid String organ) {
        logger.info("[saveuser] save users {} into organ {}", StringUtils.join(users, ","), organ);
        final User loginUser = super.getUser(request);

        if (users != null && users.length > 0) {
            List<String> chosen = new ArrayList<String>(Arrays.asList(users));
            Organ organData = organRepository.findByIdAndOrgi(organ, super.getOrgi());
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
                            user.getId(), super.getOrgi());

                    // TODO 因为一个用户可以包含在多个技能组中，所以，skill应该对应
                    // 一个List列表，此处需要重构Skill为列表
                    if (agentStatus != null) {
                        userProxy.attachOrgansPropertiesForUser(user);
                        agentStatus.setSkills(user.getSkills());
                        cache.putAgentStatusByOrgi(agentStatus, super.getOrgi());
                    }
                }
            }
            userRepository.save(organUserList);
        }

        return request(super.createView("redirect:/admin/organ/index.html?organ=" + organ));
    }

    @RequestMapping("/user/delete")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView userroledelete(
            final HttpServletRequest request,
            final @Valid String id,
            final @Valid String organ) {
        logger.info("[userroledelete] user id {}, organ {}", id, organ);
        if (id != null) {
            List<OrganUser> organUsers = organUserRes.findByUserid(id);
            if (organUsers.size() > 1) {
                organUserRes.deleteOrganUserByUseridAndOrgan(id, organ);
            } else {
                return request(super.createView(
                        "redirect:/admin/organ/index.html?organ=" + organ + "&msg=not_allow_remove_user"));
            }
        }
        return request(super.createView("redirect:/admin/organ/index.html?organ=" + organ));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createView("/admin/organ/edit"));
        Organ currentOrgan = super.getOrgan(request);
        map.addAttribute("areaList", areaRepository.findByOrgi(super.getOrgi()));
        view.addObject("organData", organRepository.findByIdAndOrgi(id, super.getOrgi()));
        view.addObject("isRootOrgan", id.equals(currentOrgan.getId()));
        map.addAttribute("organList", getOwnOragans(request));
        return view;
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView update(HttpServletRequest request, @Valid Organ organ) {
        String msg = organProxy.updateOrgan(organ, super.getOrgi(request), super.getUser(request));
        return request(super.createView(
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

        map.addAttribute("organData", organRepository.findByIdAndOrgi(id, super.getOrgi()));
        return request(super.createView("/admin/organ/area"));
    }

    @RequestMapping("/area/update")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView areaupdate(HttpServletRequest request, @Valid Organ organ) {
        Organ tempOrgan = organRepository.findByIdAndOrgi(organ.getId(), super.getOrgi());
        String msg = "admin_organ_update_success";
        if (tempOrgan != null) {
            tempOrgan.setArea(organ.getArea());
            organRepository.save(tempOrgan);
        } else {
            msg = "admin_organ_update_not_exist";
        }
        return request(super.createView(
                "redirect:/admin/organ/index.html?msg=" + msg + "&organ=" + organ.getId()));
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "organ")
    public ModelAndView delete(HttpServletRequest request, @Valid Organ organ) {
        String msg = "admin_organ_delete";

        Organ organSelf = organRepository.findByIdAndOrgi(organ.getId(), super.getOrgi());
        List<Organ> organParentAre = organRepository.findByOrgiAndParent(organSelf.getOrgi(), organSelf.getId());
        if (organ != null && organParentAre != null && organParentAre.size() > 0) {
            msg = "admin_oran_not_delete";
        } else if (organ != null) {
            List<OrganUser> organUsers = organUserRes.findByOrgan(organ.getId());
            if (organUsers.size() > 0) {
                msg = "admin_oran_not_empty";
            } else {
                organRepository.delete(organ);
            }
        }

        return request(super.createView("redirect:/admin/organ/index.html?msg=" + msg));
    }

    @RequestMapping("/auth/save")
    @Menu(type = "admin", subtype = "role")
    public ModelAndView authsave(HttpServletRequest request, @Valid String id, @Valid String menus) {
        Organ organData = organRepository.findByIdAndOrgi(id, super.getOrgi());
        List<OrganRole> organRoleList = organRoleRes.findByOrgiAndOrgan(super.getOrgi(), organData);
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
                    organRole.setOrgi(super.getOrgi(request));
                    organRole.setCreatetime(new Date());
                    organRoleRes.save(organRole);
                }

            }
        }
        return request(
                super.createView("redirect:/admin/organ/index.html?organ=" + organData.getId()));
    }
}