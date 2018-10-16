/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app.handler.admin.channel;

import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.CousultInvite;
import com.chatopera.cc.app.model.Organ;
import com.chatopera.cc.app.model.OrgiSkillRel;
import com.chatopera.cc.app.model.User;
import com.chatopera.cc.app.persistence.repository.*;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/webim")
public class WebIMController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(WebIMController.class);

    @Autowired
    private ConsultInviteRepository invite;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private ServiceAiRepository serviceAiRes;

    @Autowired
    private OrgiSkillRelRepository orgiSkillRelService;

    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @RequestMapping("/index")
    @Menu(type = "app", subtype = "app", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String snsid) {

        CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(snsid, super.getOrgi(request));
        if (coultInvite != null) {
            map.addAttribute("inviteData", coultInvite);
            map.addAttribute("skillList", getOrgans(request));
            map.addAttribute("agentList", getUsers(request));

            map.addAttribute("import", request.getServerPort());

            map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(snsid, super.getOrgi(request)));
        }
        return request(super.createAdminTempletResponse("/admin/webim/index"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "app", admin = true)
    public ModelAndView save(HttpServletRequest request, @Valid CousultInvite inviteData, @RequestParam(value = "webimlogo", required = false) MultipartFile webimlogo, @RequestParam(value = "agentheadimg", required = false) MultipartFile agentheadimg) throws IOException {
        if (!StringUtils.isBlank(inviteData.getSnsaccountid())) {
            CousultInvite tempData = invite.findBySnsaccountidAndOrgi(inviteData.getSnsaccountid(), super.getOrgi(request));
            if (tempData != null) {
                tempData.setConsult_vsitorbtn_model(inviteData.getConsult_vsitorbtn_model());
                tempData.setConsult_vsitorbtn_color(inviteData.getConsult_vsitorbtn_color());
                tempData.setConsult_vsitorbtn_position(inviteData.getConsult_vsitorbtn_position());
                tempData.setConsult_vsitorbtn_content(inviteData.getConsult_vsitorbtn_content());
                tempData.setConsult_vsitorbtn_display(inviteData.getConsult_vsitorbtn_display());
                tempData.setConsult_dialog_color(inviteData.getConsult_dialog_color());

                inviteData = tempData;
            }
        } else {
            inviteData.setSnsaccountid(super.getUser(request).getId());
        }
        inviteData.setOrgi(super.getOrgi(request));
        if (webimlogo != null && webimlogo.getOriginalFilename().lastIndexOf(".") > 0) {
            File logoDir = new File(path, "logo");
            if (!logoDir.exists()) {
                logoDir.mkdirs();
            }
            String fileName = "logo/" + inviteData.getId() + webimlogo.getOriginalFilename().substring(webimlogo.getOriginalFilename().lastIndexOf("."));
            FileCopyUtils.copy(webimlogo.getBytes(), new File(path, fileName));
            inviteData.setConsult_dialog_logo(fileName);
        }
        if (agentheadimg != null && agentheadimg.getOriginalFilename().lastIndexOf(".") > 0) {
            File headimgDir = new File(path, "headimg");
            if (!headimgDir.exists()) {
                headimgDir.mkdirs();
            }
            String fileName = "headimg/" + inviteData.getId() + agentheadimg.getOriginalFilename().substring(agentheadimg.getOriginalFilename().lastIndexOf("."));
            FileCopyUtils.copy(agentheadimg.getBytes(), new File(path, fileName));
            inviteData.setConsult_dialog_headimg(fileName);
        }
        invite.save(inviteData);
        CacheHelper.getSystemCacheBean().put(inviteData.getSnsaccountid(), inviteData, inviteData.getOrgi());
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/index.html?snsid=" + inviteData.getSnsaccountid()));
    }

    @RequestMapping("/profile")
    @Menu(type = "app", subtype = "profile", admin = true)
    public ModelAndView profile(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(snsid, super.getOrgi(request));
        if (coultInvite != null) {
            map.addAttribute("inviteData", coultInvite);
        }
        map.addAttribute("import", request.getServerPort());
        map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(snsid, super.getOrgi(request)));

        map.put("serviceAiList", serviceAiRes.findByOrgi(super.getOrgi(request)));
        return request(super.createAdminTempletResponse("/admin/webim/profile"));
    }

    @RequestMapping("/profile/save")
    @Menu(type = "admin", subtype = "profile", admin = true)
    public ModelAndView saveprofile(HttpServletRequest request, @Valid CousultInvite inviteData, @RequestParam(value = "dialogad", required = false) MultipartFile dialogad) throws IOException {
        CousultInvite tempInviteData;
        if (inviteData != null && !StringUtils.isBlank(inviteData.getId())) {
            tempInviteData = invite.findOne(inviteData.getId());
            if (tempInviteData != null) {
                tempInviteData.setDialog_name(inviteData.getDialog_name());
                tempInviteData.setDialog_address(inviteData.getDialog_address());
                tempInviteData.setDialog_phone(inviteData.getDialog_phone());
                tempInviteData.setDialog_mail(inviteData.getDialog_mail());
                tempInviteData.setDialog_introduction(inviteData.getDialog_introduction());
                tempInviteData.setDialog_message(inviteData.getDialog_message());
                tempInviteData.setLeavemessage(inviteData.isLeavemessage());
                tempInviteData.setLvmopentype(inviteData.getLvmopentype());
                tempInviteData.setLvmname(inviteData.isLvmname());
                tempInviteData.setLvmphone(inviteData.isLvmphone());
                tempInviteData.setLvmemail(inviteData.isLvmemail());
                tempInviteData.setLvmaddress(inviteData.isLvmaddress());
                tempInviteData.setLvmqq(inviteData.isLvmqq());
                tempInviteData.setSkill(inviteData.isSkill());

                tempInviteData.setConsult_skill_title(inviteData.getConsult_skill_title());
                tempInviteData.setConsult_skill_msg(inviteData.getConsult_skill_msg());
                tempInviteData.setConsult_skill_bottomtitle(inviteData.getConsult_skill_bottomtitle());
                tempInviteData.setConsult_skill_maxagent(inviteData.getConsult_skill_maxagent());
                tempInviteData.setConsult_skill_numbers(inviteData.getConsult_skill_numbers());
                tempInviteData.setConsult_skill_agent(inviteData.isConsult_skill_agent());

                tempInviteData.setOnlyareaskill(inviteData.isOnlyareaskill());
                tempInviteData.setAreaskilltipmsg(inviteData.getAreaskilltipmsg());

                tempInviteData.setConsult_info(inviteData.isConsult_info());
                tempInviteData.setConsult_info_email(inviteData.isConsult_info_email());
                tempInviteData.setConsult_info_name(inviteData.isConsult_info_name());
                tempInviteData.setConsult_info_phone(inviteData.isConsult_info_phone());
                tempInviteData.setConsult_info_resion(inviteData.isConsult_info_resion());
                tempInviteData.setConsult_info_message(inviteData.getConsult_info_message());
                tempInviteData.setConsult_info_cookies(inviteData.isConsult_info_cookies());

                tempInviteData.setRecordhis(inviteData.isRecordhis());
                tempInviteData.setTraceuser(inviteData.isTraceuser());


                tempInviteData.setAi(inviteData.isAi());
                tempInviteData.setAifirst(inviteData.isAifirst());
                tempInviteData.setAimsg(inviteData.getAimsg());
                tempInviteData.setAisuccesstip(inviteData.getAisuccesstip());
                tempInviteData.setAiname(inviteData.getAiname());
                tempInviteData.setAiid(inviteData.getAiid());


                tempInviteData.setMaxwordsnum(inviteData.getMaxwordsnum());

                tempInviteData.setCtrlenter(inviteData.isCtrlenter());

                if (dialogad != null && !StringUtils.isBlank(dialogad.getName()) && dialogad.getBytes() != null && dialogad.getBytes().length > 0) {
                    String fileName = "ad/" + inviteData.getId() + dialogad.getOriginalFilename().substring(dialogad.getOriginalFilename().lastIndexOf("."));
                    File file = new File(path, fileName);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileCopyUtils.copy(dialogad.getBytes(), file);
                    tempInviteData.setDialog_ad(fileName);
                }
                invite.save(tempInviteData);
                inviteData = tempInviteData;
            }
        } else {
            invite.save(inviteData);
        }
        CacheHelper.getSystemCacheBean().put(inviteData.getSnsaccountid(), inviteData, inviteData.getOrgi());
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/profile.html?snsid=" + inviteData.getSnsaccountid()));
    }

    @RequestMapping("/invote")
    @Menu(type = "app", subtype = "invote", admin = true)
    public ModelAndView invote(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(snsid, super.getOrgi(request));
        if (coultInvite != null) {
            map.addAttribute("inviteData", coultInvite);
        }
        map.addAttribute("import", request.getServerPort());
        map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(snsid, super.getOrgi(request)));
        return request(super.createAdminTempletResponse("/admin/webim/invote"));
    }

    @RequestMapping("/invote/save")
    @Menu(type = "admin", subtype = "profile", admin = true)
    public ModelAndView saveinvote(HttpServletRequest request, @Valid CousultInvite inviteData, @RequestParam(value = "invotebg", required = false) MultipartFile invotebg) throws IOException {
        CousultInvite tempInviteData;
        if (inviteData != null && !StringUtils.isBlank(inviteData.getId())) {
            tempInviteData = invite.findOne(inviteData.getId());
            if (tempInviteData != null) {
                tempInviteData.setConsult_invite_enable(inviteData.isConsult_invite_enable());
                tempInviteData.setConsult_invite_content(inviteData.getConsult_invite_content());
                tempInviteData.setConsult_invite_accept(inviteData.getConsult_invite_accept());
                tempInviteData.setConsult_invite_later(inviteData.getConsult_invite_later());
                tempInviteData.setConsult_invite_delay(inviteData.getConsult_invite_delay());

                tempInviteData.setConsult_invite_color(inviteData.getConsult_invite_color());

                if (invotebg != null && !StringUtils.isBlank(invotebg.getName()) && invotebg.getBytes() != null && invotebg.getBytes().length > 0) {
                    String fileName = "invote/" + inviteData.getId() + invotebg.getOriginalFilename().substring(invotebg.getOriginalFilename().lastIndexOf("."));
                    File file = new File(path, fileName);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileCopyUtils.copy(invotebg.getBytes(), file);
                    tempInviteData.setConsult_invite_bg(fileName);
                }
                invite.save(tempInviteData);
                inviteData = tempInviteData;
            }
        } else {
            invite.save(inviteData);
        }
        CacheHelper.getSystemCacheBean().put(inviteData.getSnsaccountid(), inviteData, inviteData.getOrgi());
        return request(super.createRequestPageTempletResponse("redirect:/admin/webim/invote.html?snsid=" + inviteData.getSnsaccountid()));
    }

    /**
     * 获取当前产品下组织信息
     *
     * @param request
     * @return
     */
    private List<Organ> getOrgans(HttpServletRequest request) {
        List<Organ> list = null;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            list = organRes.findAll(organIdList);
        } else {
            list = organRes.findByOrgiAndOrgid(super.getOrgi(request), super.getOrgid(request));
        }
        return list;
    }

    /**
     * 获取当前产品下人员信息
     *
     * @param request
     * @param q
     * @return
     */
    private List<User> getUsers(HttpServletRequest request) {
        List<User> userList = null;
        if (super.isTenantshare()) {
            List<String> organIdList = new ArrayList<>();
            List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelService.findByOrgi(super.getOrgi(request));
            if (!orgiSkillRelList.isEmpty()) {
                for (OrgiSkillRel rel : orgiSkillRelList) {
                    organIdList.add(rel.getSkillid());
                }
            }
            userList = userRes.findByOrganInAndAgentAndDatastatus(organIdList, true, false);
        } else {
            userList = userRes.findByOrgiAndAgentAndDatastatus(super.getOrgi(request), true, false);
        }
        return userList;
    }
}