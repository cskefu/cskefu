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
package com.chatopera.cc.controller.admin.channel;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.CousultInvite;
import com.chatopera.cc.model.SNSAccount;
import com.chatopera.cc.model.Secret;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.ConsultInviteRepository;
import com.chatopera.cc.persistence.repository.SNSAccountRepository;
import com.chatopera.cc.persistence.repository.SecretRepository;
import com.chatopera.cc.util.Base62;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 *
 *
 */
@Controller
@RequestMapping("/admin/im")
public class SNSAccountIMController extends Handler {

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private ConsultInviteRepository invite;

    @Autowired
    private SecretRepository secRes;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "im", access = false, admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String execute,@RequestParam(name = "status",required = false) String status ) {
        map.addAttribute("snsAccountList", snsAccountRes.findBySnstypeAndOrgi(MainContext.ChannelType.WEBIM.toString(), super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request))));
        map.addAttribute("status",status);
        List<Secret> secretConfig = secRes.findByOrgi(super.getOrgi(request));
        if (secretConfig != null && secretConfig.size() > 0) {
            map.addAttribute("secret", secretConfig.get(0));
        }
        if (StringUtils.isNotBlank(execute) && execute.equals("false")) {
            map.addAttribute("execute", execute);
        }
        return request(super.createAdminTempletResponse("/admin/channel/im/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "send", access = false, admin = true)
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/channel/im/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "weixin")
    public ModelAndView save(HttpServletRequest request,
                             @Valid SNSAccount snsAccount) throws NoSuchAlgorithmException {
        String status = "new_webim_fail";
        if (StringUtils.isNotBlank(snsAccount.getBaseURL())) {
            snsAccount.setSnsid(Base62.encode(snsAccount.getBaseURL()).toLowerCase());
            int count = snsAccountRes.countBySnsidAndOrgi(snsAccount.getSnsid(), super.getOrgi(request));
            if (count == 0) {
                status = "new_webim_success";
                snsAccount.setOrgi(super.getOrgi(request));
                snsAccount.setSnstype(MainContext.ChannelType.WEBIM.toString());
                snsAccount.setCreatetime(new Date());
                User curr = super.getUser(request);
                snsAccount.setCreater(curr.getId());

                snsAccountRes.save(snsAccount);

                /**
                 * 同时创建CousultInvite 记录
                 */
                CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(snsAccount.getSnsid(), super.getOrgi(request));
                if (coultInvite == null) {
                    coultInvite = new CousultInvite();
                    coultInvite.setSnsaccountid(snsAccount.getSnsid());
                    coultInvite.setCreate_time(new Date());
                    coultInvite.setOrgi(super.getOrgi(request));
                    coultInvite.setName(snsAccount.getName());
                    coultInvite.setOwner(snsAccount.getCreater());
                    coultInvite.setSkill(false); // 不启动技能组
                    coultInvite.setConsult_skill_fixed(false); // 不绑定唯一技能组
                    coultInvite.setAi(false);
                    coultInvite.setAifirst(false);
                    invite.save(coultInvite);
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/im/index.html?status=" + status));
    }

    @RequestMapping("/delete")
    @Menu(type = "weixin", subtype = "delete")
    public ModelAndView delete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String confirm) {
        boolean execute;
        if (execute = MainUtils.secConfirm(secRes, super.getOrgi(request), confirm)) {
            SNSAccount snsAccount = snsAccountRes.findByIdAndOrgi(id, super.getOrgi(request));
            if (snsAccountRes != null) {
                snsAccountRes.delete(snsAccount);
                CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(snsAccount.getSnsid(), super.getOrgi(request));
                if (coultInvite != null) {
                    invite.delete(coultInvite);
                }
            }
        }

        return request(super.createRequestPageTempletResponse("redirect:/admin/im/index.html?execute=" + execute));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "send", access = false, admin = true)
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("snsAccount", snsAccountRes.findByIdAndOrgi(id, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/channel/im/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "send", access = false, admin = true)
    public ModelAndView update(HttpServletRequest request, @Valid SNSAccount snsAccount) throws NoSuchAlgorithmException {
        SNSAccount oldSnsAccount = snsAccountRes.findByIdAndOrgi(snsAccount.getId(), super.getOrgi(request));
        if (oldSnsAccount != null) {
            oldSnsAccount.setName(snsAccount.getName());
            oldSnsAccount.setBaseURL(snsAccount.getBaseURL());
            oldSnsAccount.setUpdatetime(new Date());
            /**
             * SNSID如果有变更，需要同时变更 CoultInvite 表的 记录
             */
            if (StringUtils.isNotBlank(oldSnsAccount.getSnsid())) {
                CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(oldSnsAccount.getSnsid(), super.getOrgi(request));
                if (coultInvite == null) {
                    /**
                     * 同时创建CousultInvite 记录
                     */
                    coultInvite = new CousultInvite();
                    coultInvite.setSnsaccountid(oldSnsAccount.getSnsid());
                    coultInvite.setCreate_time(new Date());
                    coultInvite.setOrgi(super.getOrgi(request));
                    coultInvite.setName(snsAccount.getName());
                    invite.save(coultInvite);
                }
            }

            oldSnsAccount.setSnstype(MainContext.ChannelType.WEBIM.toString());
            snsAccountRes.save(oldSnsAccount);
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/im/index.html"));
    }
}