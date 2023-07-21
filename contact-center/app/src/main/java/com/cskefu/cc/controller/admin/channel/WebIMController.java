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
package com.cskefu.cc.controller.admin.channel;

import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.CousultInvite;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.model.Channel;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.OnlineUserProxy;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/webim")
public class WebIMController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(WebIMController.class);

    @Autowired
    private ConsultInviteRepository inviteRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private ServiceAiRepository serviceAiRes;

    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private ChannelRepository snsAccountRes;

    @Autowired
    private Cache cache;

    @RequestMapping("/index")
    @Menu(type = "app", subtype = "app", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = OnlineUserProxy.consult(snsid);

        if (coultInvite != null) {
            logger.info("[index] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", coultInvite.getSnsaccountid(), coultInvite.isAi(), coultInvite.isAifirst(), coultInvite.getAiname(), coultInvite.getAisuccesstip(), coultInvite.getAiid());
            map.addAttribute("inviteData", coultInvite);
            map.addAttribute("skillGroups", getSkillGroups(request));
            map.addAttribute("agentList", getUsers(request));
            map.addAttribute("port", request.getServerPort());

            Optional<Channel> snsAccountOpt = snsAccountRes.findBySnsid(snsid);
            snsAccountOpt.ifPresent(snsAccount -> map.addAttribute("channel", snsAccount));
        }
        return request(super.createView("/admin/webim/index"));
    }

    /**
     * @param request
     * @param inviteData
     * @param webimlogo
     * @param agentheadimg
     * @return
     * @throws IOException
     */
    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "app", admin = true)
    public ModelAndView save(HttpServletRequest request,
                             @Valid CousultInvite inviteData,
                             @RequestParam(value = "webimlogo", required = false) MultipartFile webimlogo,
                             @RequestParam(value = "agentheadimg", required = false) MultipartFile agentheadimg) throws IOException {
        logger.info("[save] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", inviteData.getSnsaccountid(), inviteData.isAi(), inviteData.isAifirst(), inviteData.getAiname(), inviteData.getAisuccesstip(), inviteData.getAiid());

        if (StringUtils.isNotBlank(inviteData.getSnsaccountid())) {
            CousultInvite tempData = inviteRes.findBySnsaccountid(inviteData.getSnsaccountid());
            if (tempData != null) {
                tempData.setConsult_vsitorbtn_model(inviteData.getConsult_vsitorbtn_model());
                tempData.setConsult_vsitorbtn_color(inviteData.getConsult_vsitorbtn_color());
                tempData.setConsult_vsitorbtn_position(inviteData.getConsult_vsitorbtn_position());
                tempData.setConsult_vsitorbtn_content(inviteData.getConsult_vsitorbtn_content());
                tempData.setConsult_vsitorbtn_delay(inviteData.getConsult_vsitorbtn_delay());
                tempData.setConsult_dialog_color(inviteData.getConsult_dialog_color());
                inviteData = tempData;
            }
        } else {
            inviteData.setSnsaccountid(super.getUser(request).getId());
        }
        // 网页品牌标识
        if (webimlogo != null && webimlogo.getOriginalFilename().lastIndexOf(".") > 0) {
            inviteData.setConsult_dialog_logo(super.saveImageFileWithMultipart(webimlogo));
        }

        // 网页坐席头像
        if (agentheadimg != null && agentheadimg.getOriginalFilename().lastIndexOf(".") > 0) {
            inviteData.setConsult_dialog_headimg(super.saveImageFileWithMultipart(agentheadimg));
        }
        inviteRes.save(inviteData);
        cache.putConsultInvite(inviteData);
        return request(super.createView("redirect:/admin/webim/index.html?snsid=" + inviteData.getSnsaccountid()));
    }

    @RequestMapping("/profile")
    @Menu(type = "app", subtype = "profile", admin = true)
    public ModelAndView profile(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = OnlineUserProxy.consult(snsid);

        if (coultInvite != null) {
            logger.info("[profile] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", coultInvite.getSnsaccountid(), coultInvite.isAi(), coultInvite.isAifirst(), coultInvite.getAiname(), coultInvite.getAisuccesstip(), coultInvite.getAiid());
            map.addAttribute("inviteData", coultInvite);
            map.addAttribute("skillGroups", getSkillGroups(request));
        }
        map.addAttribute("import", request.getServerPort());
        Optional<Channel> snsAccountOpt = snsAccountRes.findBySnsid(snsid);
        snsAccountOpt.ifPresent(snsAccount -> map.addAttribute("channel", snsAccount));

        map.put("serviceAiList", serviceAiRes.findAll());
        return request(super.createView("/admin/webim/profile"));
    }

    @RequestMapping("/profile/save")
    @Menu(type = "admin", subtype = "profile", admin = true)
    public ModelAndView saveprofile(HttpServletRequest request, @Valid CousultInvite inviteData, @RequestParam(value = "dialogad", required = false) MultipartFile dialogad) throws IOException {
        CousultInvite tempInviteData;
        logger.info("[profile/save] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}, traceUser {}",
                inviteData.getSnsaccountid(),
                inviteData.isAi(),
                inviteData.isAifirst(),
                inviteData.getAiname(),
                inviteData.getAisuccesstip(),
                inviteData.getAiid(),
                inviteData.isTraceuser());

        if (inviteData != null && StringUtils.isNotBlank(inviteData.getId())) {
            // 从Cache及DB加载consult
            tempInviteData = OnlineUserProxy.consult(inviteData.getSnsaccountid());

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

                tempInviteData.setConsult_skill_fixed(inviteData.isConsult_skill_fixed());
                tempInviteData.setConsult_skill_fixed_id(inviteData.getConsult_skill_fixed_id());
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
                tempInviteData.setWhitelist_mode(inviteData.isWhitelist_mode());

                if (dialogad != null && StringUtils.isNotBlank(dialogad.getName()) && dialogad.getBytes() != null && dialogad.getBytes().length > 0) {
                    tempInviteData.setDialog_ad(super.saveImageFileWithMultipart(dialogad));
                }
                // 保存到DB
                inviteRes.save(tempInviteData);
                inviteData = tempInviteData;
            }
        } else {
            inviteRes.save(inviteData);
        }

        cache.putConsultInvite(inviteData);
        return request(super.createView("redirect:/admin/webim/profile.html?snsid=" + inviteData.getSnsaccountid()));
    }

    @RequestMapping("/invote")
    @Menu(type = "app", subtype = "invote", admin = true)
    public ModelAndView invote(ModelMap map, HttpServletRequest request, @Valid String snsid) {
        CousultInvite coultInvite = OnlineUserProxy.consult(snsid);

        if (coultInvite != null) {
            logger.info("[invote] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", coultInvite.getSnsaccountid(), coultInvite.isAi(), coultInvite.isAifirst(), coultInvite.getAiname(), coultInvite.getAisuccesstip(), coultInvite.getAiid());
            map.addAttribute("inviteData", coultInvite);
        }
        map.addAttribute("import", request.getServerPort());
        Optional<Channel> snsAccountOpt = snsAccountRes.findBySnsid(snsid);
        snsAccountOpt.ifPresent(snsAccount -> map.addAttribute("channel", snsAccount));
        return request(super.createView("/admin/webim/invote"));
    }

    @RequestMapping("/invote/save")
    @Menu(type = "admin", subtype = "profile", admin = true)
    public ModelAndView saveinvote(HttpServletRequest request, @Valid CousultInvite inviteData, @RequestParam(value = "invotebg", required = false) MultipartFile invotebg) throws IOException {
        CousultInvite tempInviteData;
        logger.info("[invote/save] snsaccount Id {}, Ai {}, Aifirst {}, Ainame {}, Aisuccess {}, Aiid {}", inviteData.getSnsaccountid(), inviteData.isAi(), inviteData.isAifirst(), inviteData.getAiname(), inviteData.getAisuccesstip(), inviteData.getAiid());

        if (inviteData != null && StringUtils.isNotBlank(inviteData.getId())) {
            tempInviteData = OnlineUserProxy.consult(inviteData.getSnsaccountid());
            if (tempInviteData != null) {
                tempInviteData.setConsult_invite_enable(inviteData.isConsult_invite_enable());
                tempInviteData.setConsult_invite_content(inviteData.getConsult_invite_content());
                tempInviteData.setConsult_invite_accept(inviteData.getConsult_invite_accept());
                tempInviteData.setConsult_invite_later(inviteData.getConsult_invite_later());
                tempInviteData.setConsult_invite_delay(inviteData.getConsult_invite_delay());

                tempInviteData.setConsult_invite_color(inviteData.getConsult_invite_color());

                if (invotebg != null && StringUtils.isNotBlank(invotebg.getName()) && invotebg.getBytes() != null && invotebg.getBytes().length > 0) {
                    tempInviteData.setConsult_invite_bg(super.saveImageFileWithMultipart(invotebg));
                }
                inviteRes.save(tempInviteData);
                inviteData = tempInviteData;
            }
        } else {
            inviteRes.save(inviteData);
        }
        cache.putConsultInvite(inviteData);
        return request(super.createView("redirect:/admin/webim/invote.html?snsid=" + inviteData.getSnsaccountid()));
    }

    /**
     * 获取当前登录者组织下的技能组列表
     *
     * @param request
     * @return
     */
    private List<Organ> getSkillGroups(HttpServletRequest request) {
        List<Organ> skillgroups = new ArrayList<>();
        List<Organ> allgroups = organRes.findAll();
        for (Organ o : allgroups) {
            if (o.isSkill()) {
                skillgroups.add(o);
            }
        }
        return skillgroups;
    }

    /**
     * 获取当前产品下人员信息
     *
     * @param request
     * @return
     */
    private List<User> getUsers(HttpServletRequest request) {
        List<User> userList = userRes.findByAgentAndDatastatus(true, false);
        return userList;
    }
}