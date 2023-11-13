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

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.exception.BillingQuotaException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.ConsultInviteRepository;
import com.cskefu.cc.persistence.repository.OrganRepository;
import com.cskefu.cc.persistence.repository.ChannelRepository;
import com.cskefu.cc.persistence.repository.SecretRepository;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.util.Base62;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping("/admin/im")
public class ChannelController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private ChannelRepository snsAccountRes;

    @Autowired
    private ConsultInviteRepository invite;

    @Autowired
    private SecretRepository secRes;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private Cache cache;


    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "im", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String execute, @RequestParam(name = "status", required = false) String status) {
        Map<String, Organ> organs = organProxy.findAllOrganByParent(super.getOrgan(request));
        map.addAttribute("snsAccountList", snsAccountRes.findByTypeAndOrgan(MainContext.ChannelType.WEBIM.toString(), organs.keySet(), PageRequest.of(super.getP(request), super.getPs(request))));

        map.addAttribute("status", status);
        List<Secret> secretConfig = secRes.findAll();
        if (secretConfig != null && secretConfig.size() > 0) {
            map.addAttribute("secret", secretConfig.get(0));
        }
        if (StringUtils.isNotBlank(execute) && execute.equals("false")) {
            map.addAttribute("execute", execute);
        }
        return request(super.createView("/admin/channel/im/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "send", admin = true)
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        Organ currentOrgan = super.getOrgan(request);
        map.put("organ", currentOrgan);
        return request(super.createView("/admin/channel/im/add"));
    }

    /**
     * 创建新的网站渠道
     *
     * @param request
     * @param channel
     * @return
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "im")
    public ModelAndView save(HttpServletRequest request,
                             @Valid Channel channel) throws NoSuchAlgorithmException {
        Organ currentOrgan = super.getOrgan(request);
        String status = "new_webim_fail";
        if (StringUtils.isNotBlank(channel.getBaseURL())) {
            try {
                channel.setSnsid(Base62.encode(channel.getBaseURL()).toLowerCase());
                int count = snsAccountRes.countBySnsid(channel.getSnsid());
                if (count == 0) {
                    status = "new_webim_success";
                    channel.setType(MainContext.ChannelType.WEBIM.toString());
                    channel.setCreatetime(new Date());
                    User curr = super.getUser(request);
                    channel.setCreater(curr.getId());
                    channel.setOrgan(currentOrgan.getId());

                    snsAccountRes.save(channel);

                    /**
                     * 同时创建CousultInvite 记录
                     */
                    CousultInvite coultInvite = invite.findBySnsaccountid(channel.getSnsid());
                    if (coultInvite == null) {
                        coultInvite = new CousultInvite();
                        coultInvite.setSnsaccountid(channel.getSnsid());
                        coultInvite.setCreate_time(new Date());
                        coultInvite.setName(channel.getName());
                        coultInvite.setOwner(channel.getCreater());
                        coultInvite.setSkill(false); // 不启动技能组
                        coultInvite.setConsult_skill_fixed(false); // 不绑定唯一技能组
                        coultInvite.setAi(false);
                        coultInvite.setAifirst(false);
                        invite.save(coultInvite);
                    }
                }
            } catch (Exception e) {
                if (e instanceof UndeclaredThrowableException) {
                    logger.error("[save] BillingQuotaException", e);
                    if (StringUtils.startsWith(e.getCause().getMessage(), BillingQuotaException.SUFFIX)) {
                        status = e.getCause().getMessage();
                    }
                } else {
                    logger.error("[save] err", e);
                }
            }
        }
        return request(super.createView("redirect:/admin/im/index.html?status=" + status));
    }

    @RequestMapping("/delete")
    @Menu(type = "weixin", subtype = "delete")
    public ModelAndView delete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String confirm) {
        boolean execute;
        if (execute = MainUtils.secConfirm(secRes, confirm)) {
            Channel channel = snsAccountRes.findById(id).orElse(null);
            if (snsAccountRes != null) {
                // 删除网站渠道记录
                snsAccountRes.delete(channel);
                /**
                 * 删除网站渠道客服配置
                 */
                CousultInvite coultInvite = invite.findBySnsaccountid(channel.getSnsid());
                if (coultInvite != null) {
                    invite.delete(coultInvite);
                }
                // 删除缓存
                cache.deleteConsultInviteBySnsid(channel.getSnsid());
            }
        }

        return request(super.createView("redirect:/admin/im/index.html?execute=" + execute));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "send", admin = true)
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        Channel channel = snsAccountRes.findById(id).orElse(null);
        Organ organ = organRes.findById(channel.getOrgan()).orElse(null);
        map.put("organ", organ);
        map.addAttribute("channel", channel);
        return request(super.createView("/admin/channel/im/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "send", admin = true)
    public ModelAndView update(HttpServletRequest request, @Valid Channel channel) throws NoSuchAlgorithmException {
        Channel oldChannel = snsAccountRes.findById(channel.getId()).orElse(null);
        if (oldChannel != null) {
            oldChannel.setName(channel.getName());
            oldChannel.setBaseURL(channel.getBaseURL());
            oldChannel.setUpdatetime(new Date());
            /**
             * SNSID如果有变更，需要同时变更 CoultInvite 表的 记录
             */
            if (StringUtils.isNotBlank(oldChannel.getSnsid())) {
                CousultInvite coultInvite = invite.findBySnsaccountid(oldChannel.getSnsid());
                if (coultInvite == null) {
                    /**
                     * 同时创建CousultInvite 记录
                     */
                    coultInvite = new CousultInvite();
                    coultInvite.setSnsaccountid(oldChannel.getSnsid());
                    coultInvite.setCreate_time(new Date());
                    coultInvite.setName(channel.getName());
                    invite.save(coultInvite);
                }
            }

            oldChannel.setType(MainContext.ChannelType.WEBIM.toString());
            snsAccountRes.save(oldChannel);
        }
        return request(super.createView("redirect:/admin/im/index.html"));
    }
}