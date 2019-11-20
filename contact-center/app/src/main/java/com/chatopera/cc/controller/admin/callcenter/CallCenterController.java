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

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.PbxHost;
import com.chatopera.cc.persistence.interfaces.CallCenterInterface;
import com.chatopera.cc.persistence.repository.PbxHostRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/callcenter")
public class CallCenterController extends Handler {

    @Autowired
    private PbxHostRepository pbxHostRes;

    @RequestMapping(value = "/index")
    @Menu(type = "callcenter", subtype = "callcenter", access = false, admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String msg) {
        List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request));
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
            CallCenterInterface callCenterImpl = (CallCenterInterface) MainContext.getContext().getBean("callcenter");

            for (PbxHost pbxHost : pbxHostList) {
                if (callCenterImpl != null) {
                    pbxHost.setConnected(callCenterImpl.connected(pbxHost.getId()));
                }
            }
        }
        map.addAttribute("pbxHostList", pbxHostList);
        return request(super.createAdminTempletResponse("/admin/callcenter/index"));
    }

    @RequestMapping(value = "/pbxhost")
    @Menu(type = "callcenter", subtype = "pbxhost", access = false, admin = true)
    public ModelAndView pbxhost(ModelMap map, HttpServletRequest request) {
        List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request));
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
            CallCenterInterface callCenterImpl = (CallCenterInterface) MainContext.getContext().getBean("callcenter");

            for (PbxHost pbxHost : pbxHostList) {
                if (callCenterImpl != null) {
                    pbxHost.setConnected(callCenterImpl.connected(pbxHost.getId()));
                }
            }
        }
        map.addAttribute("pbxHostList", pbxHostList);
        return request(super.createRequestPageTempletResponse("/admin/callcenter/pbxhost/index"));
    }

    @RequestMapping(value = "/pbxhost/add")
    @Menu(type = "callcenter", subtype = "pbxhost", access = false, admin = true)
    public ModelAndView pbxhostadd(ModelMap map, HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/callcenter/pbxhost/add"));
    }

    @RequestMapping(value = "/pbxhost/save")
    @Menu(type = "callcenter", subtype = "pbxhost", access = false, admin = true)
    public ModelAndView pbxhostsave(ModelMap map, HttpServletRequest request, @Valid PbxHost pbxHost) {
        ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/pbxhost.html"));
        String msg = null;
        if (!StringUtils.isBlank(pbxHost.getName())) {
            int count = pbxHostRes.countByHostnameAndOrgi(pbxHost.getHostname(), super.getOrgi(request));
            if (count == 0) {
                pbxHost.setOrgi(super.getOrgi(request));
                pbxHost.setCreater(super.getUser(request).getId());
                pbxHostRes.save(pbxHost);
                if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
                    CallCenterInterface callCenterImpl = (CallCenterInterface) MainContext.getContext().getBean(
                            "callcenter");
                    if (callCenterImpl != null) {
                        try {
                            callCenterImpl.init(pbxHost);
                        } catch (Exception ex) {
                            msg = ex.getMessage();
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        if (!StringUtils.isBlank(msg)) {
            view = request(
                    super.createRequestPageTempletResponse("redirect:/admin/callcenter/pbxhost.html?msg=" + msg));
        }
        return view;
    }

    @RequestMapping(value = "/pbxhost/edit")
    @Menu(type = "callcenter", subtype = "pbxhost", access = false, admin = true)
    public ModelAndView pbxhostedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        map.addAttribute("pbxHost", pbxHostRes.findByIdAndOrgi(id, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/pbxhost/edit"));
    }

    @RequestMapping(value = "/pbxhost/update")
    @Menu(type = "callcenter", subtype = "pbxhost", access = false, admin = true)
    public ModelAndView pbxhostupdate(ModelMap map, HttpServletRequest request, @Valid PbxHost pbxHost) {
        ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/pbxhost.html"));
        String msg = null;
        if (!StringUtils.isBlank(pbxHost.getId())) {
            PbxHost destHost = pbxHostRes.findByIdAndOrgi(pbxHost.getId(), super.getOrgi(request));
            destHost.setHostname(pbxHost.getHostname());
            destHost.setIpaddr(pbxHost.getIpaddr());
            destHost.setName(pbxHost.getName());
            destHost.setPort(pbxHost.getPort());
            if (!StringUtils.isBlank(pbxHost.getPassword())) {
                destHost.setPassword(pbxHost.getPassword());
            }
            pbxHostRes.save(destHost);
            if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
                CallCenterInterface callCenterImpl = (CallCenterInterface) MainContext.getContext().getBean(
                        "callcenter");
                if (callCenterImpl != null) {
                    try {
                        callCenterImpl.init(destHost);
                    } catch (Exception ex) {
                        msg = ex.getMessage();
                        ex.printStackTrace();
                    }
                }
            }
        }
        if (!StringUtils.isBlank(msg)) {
            view = request(
                    super.createRequestPageTempletResponse("redirect:/admin/callcenter/pbxhost.html?msg=" + msg));
        }
        return view;
    }

    @RequestMapping(value = "/pbxhost/delete")
    @Menu(type = "callcenter", subtype = "pbxhost", access = false, admin = true)
    public ModelAndView mediadelete(ModelMap map, HttpServletRequest request, @Valid String id) {
        if (!StringUtils.isBlank(id)) {
            pbxHostRes.delete(id);
            if (MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
                CallCenterInterface callCenterImpl = (CallCenterInterface) MainContext.getContext().getBean(
                        "callcenter");
                if (callCenterImpl != null) {
                    callCenterImpl.remove(id);
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/pbxhost.html"));
    }
}
