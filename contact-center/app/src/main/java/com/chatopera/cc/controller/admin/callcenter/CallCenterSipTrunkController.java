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
package com.chatopera.cc.controller.admin.callcenter;

import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.SipTrunk;
import com.chatopera.cc.persistence.repository.PbxHostRepository;
import com.chatopera.cc.persistence.repository.SipTrunkRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin/callcenter")
public class CallCenterSipTrunkController extends Handler {

    @Autowired
    private PbxHostRepository pbxHostRes;

    @Autowired
    private SipTrunkRepository sipTrunkRes;

    @Autowired
    private Cache cache;

    @RequestMapping(value = "/siptrunk")
    @Menu(type = "callcenter", subtype = "callcenterresource", access = false, admin = true)
    public ModelAndView skill(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        if (!StringUtils.isBlank(hostid)) {
            map.addAttribute("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
            map.addAttribute("sipTrunkListList", sipTrunkRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));
        }
        return request(super.createRequestPageTempletResponse("/admin/callcenter/siptrunk/index"));
    }

    @RequestMapping(value = "/siptrunk/add")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentionadd(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/siptrunk/add"));
    }

    @RequestMapping(value = "/siptrunk/save")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentionsave(ModelMap map, HttpServletRequest request, @Valid SipTrunk siptrunk) {
        if (!StringUtils.isBlank(siptrunk.getName())) {
            int count = sipTrunkRes.countByNameAndOrgi(siptrunk.getName(), super.getOrgi(request));
            if (count == 0) {
                siptrunk.setOrgi(super.getOrgi(request));
                siptrunk.setCreater(super.getUser(request).getId());
                sipTrunkRes.save(siptrunk);

                cache.putSystemByIdAndOrgi(siptrunk.getId(), siptrunk.getOrgi(), siptrunk);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/siptrunk.html?hostid=" + siptrunk.getHostid()));
    }

    @RequestMapping(value = "/siptrunk/edit")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView siptrunkedit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String hostid) {
        map.addAttribute("siptrunk", sipTrunkRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/siptrunk/edit"));
    }

    @RequestMapping(value = "/siptrunk/update")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView pbxhostupdate(ModelMap map, HttpServletRequest request, @Valid SipTrunk siptrunk) {
        if (!StringUtils.isBlank(siptrunk.getId())) {
            SipTrunk oldSipTrunk = sipTrunkRes.findByIdAndOrgi(siptrunk.getId(), super.getOrgi(request));
            if (oldSipTrunk != null) {
                oldSipTrunk.setName(siptrunk.getName());
                oldSipTrunk.setSipserver(siptrunk.getSipserver());
                oldSipTrunk.setPort(siptrunk.getPort());
                oldSipTrunk.setProtocol(siptrunk.getProtocol());
                oldSipTrunk.setRegister(siptrunk.isRegister());
                oldSipTrunk.setDefaultsip(siptrunk.isDefaultsip());
                oldSipTrunk.setTitle(siptrunk.getTitle());

                oldSipTrunk.setEnablecallagent(siptrunk.isEnablecallagent());

                oldSipTrunk.setOutnumber(siptrunk.getOutnumber());
                oldSipTrunk.setBusyext(siptrunk.getBusyext());
                oldSipTrunk.setNotready(siptrunk.getNotready());

                oldSipTrunk.setNoname(siptrunk.getNoname());

                oldSipTrunk.setProvince(siptrunk.getProvince());
                oldSipTrunk.setCity(siptrunk.getCity());
                oldSipTrunk.setPrefix(siptrunk.getPrefix());

                sipTrunkRes.save(oldSipTrunk);
                cache.putSystemByIdAndOrgi(oldSipTrunk.getId(), oldSipTrunk.getOrgi(), oldSipTrunk);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/siptrunk.html?hostid=" + siptrunk.getHostid()));
    }

    @RequestMapping(value = "/siptrunk/code")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView siptrunkcode(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String hostid) {
        map.addAttribute("siptrunk", sipTrunkRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/siptrunk/code"));
    }

    @RequestMapping(value = "/siptrunk/code/update")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView siptrunkcodeupdate(ModelMap map, HttpServletRequest request, @Valid SipTrunk siptrunk) {
        if (!StringUtils.isBlank(siptrunk.getId())) {
            SipTrunk oldSipTrunk = sipTrunkRes.findByIdAndOrgi(siptrunk.getId(), super.getOrgi(request));
            if (!StringUtils.isBlank(siptrunk.getSipcontent())) {
                oldSipTrunk.setSipcontent(siptrunk.getSipcontent());
                sipTrunkRes.save(oldSipTrunk);
                cache.putSystemByIdAndOrgi(oldSipTrunk.getId(), oldSipTrunk.getOrgi(), oldSipTrunk);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/siptrunk.html?hostid=" + siptrunk.getHostid()));
    }

    @RequestMapping(value = "/siptrunk/delete")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentiondelete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String hostid) {
        if (!StringUtils.isBlank(id)) {
            sipTrunkRes.delete(id);
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/siptrunk.html?hostid=" + hostid));
    }
}
