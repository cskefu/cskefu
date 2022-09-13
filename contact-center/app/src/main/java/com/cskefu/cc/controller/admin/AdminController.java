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

import com.cskefu.cc.acd.ACDWorkMonitor;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.OnlineUserRepository;
import com.cskefu.cc.persistence.repository.UserEventRepository;
import com.cskefu.cc.persistence.repository.UserRepository;
import com.cskefu.cc.proxy.OnlineUserProxy;
import com.cskefu.cc.socketio.client.NettyClients;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController extends Handler {

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private UserEventRepository userEventRes;

    @Autowired
    private Cache cache;

    @RequestMapping("/admin")
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        ModelAndView view = request(super.createView("redirect:/"));
        User user = super.getUser(request);
        view.addObject("agentStatusReport", acdWorkMonitor.getAgentReport(user.getOrgi()));
        view.addObject("agentStatus", cache.findOneAgentStatusByAgentnoAndOrig(user.getId(), user.getOrgi()));
        return view;
    }

    private void aggValues(ModelMap map, HttpServletRequest request) {
        String orgi = super.getOrgi(request);
        map.put("onlineUserCache", cache.getOnlineUserSizeByOrgi(orgi));
        map.put("onlineUserClients", OnlineUserProxy.webIMClients.size());
        map.put("chatClients", NettyClients.getInstance().size());
        map.put("systemCaches", cache.getSystemSizeByOrgi(Constants.SYSTEM_ORGI));

        map.put("agentReport", acdWorkMonitor.getAgentReport(orgi));
        map.put("webIMReport", MainUtils.getWebIMReport(userEventRes.findByOrgiAndCreatetimeRange(super.getOrgi(request), MainUtils.getStartTime(), MainUtils.getEndTime())));

        map.put("agents", getAgent(request).size());

        map.put("webIMInvite", MainUtils.getWebIMInviteStatus(onlineUserRes.findByOrgiAndStatus(super.getOrgi(request), MainContext.OnlineUserStatusEnum.ONLINE.toString())));

        map.put("inviteResult", MainUtils.getWebIMInviteResult(onlineUserRes.findByOrgiAndAgentnoAndCreatetimeRange(super.getOrgi(request), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime())));

        map.put("agentUserCount", onlineUserRes.countByAgentForAgentUser(super.getOrgi(request), MainContext.AgentUserStatusEnum.INSERVICE.toString(), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime()));

        map.put("agentServicesCount", onlineUserRes.countByAgentForAgentUser(super.getOrgi(request), MainContext.AgentUserStatusEnum.END.toString(), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime()));

        map.put("agentServicesAvg", onlineUserRes.countByAgentForAvagTime(super.getOrgi(request), MainContext.AgentUserStatusEnum.END.toString(), super.getUser(request).getId(), MainUtils.getStartTime(), MainUtils.getEndTime()));

        map.put("webInviteReport", MainUtils.getWebIMInviteAgg(onlineUserRes.findByOrgiAndCreatetimeRange(super.getOrgi(request), MainContext.ChannelType.WEBIM.toString(), MainUtils.getLast30Day(), MainUtils.getEndTime())));

        map.put("agentConsultReport", MainUtils.getWebIMDataAgg(onlineUserRes.findByOrgiAndCreatetimeRangeForAgent(super.getOrgi(request), MainUtils.getLast30Day(), MainUtils.getEndTime())));

        map.put("clentConsultReport", MainUtils.getWebIMDataAgg(onlineUserRes.findByOrgiAndCreatetimeRangeForClient(super.getOrgi(request), MainUtils.getLast30Day(), MainUtils.getEndTime(), MainContext.ChannelType.WEBIM.toString())));

        map.put("browserConsultReport", MainUtils.getWebIMDataAgg(onlineUserRes.findByOrgiAndCreatetimeRangeForBrowser(super.getOrgi(request), MainUtils.getLast30Day(), MainUtils.getEndTime(), MainContext.ChannelType.WEBIM.toString())));
    }

    private List<User> getAgent(HttpServletRequest request) {
        //获取当前产品or租户坐席数
        List<User> userList = userRes.findByOrgiAndAgentAndDatastatus(super.getOrgi(request), true, false);
        return userList.isEmpty() ? new ArrayList<User>() : userList;
    }

    @RequestMapping("/admin/content")
    @Menu(type = "admin", subtype = "content")
    public ModelAndView content(ModelMap map, HttpServletRequest request) {
        aggValues(map, request);
        return request(super.createView("/admin/content"));
    	/*if(super.getUser(request).isSuperuser()) {
    		aggValues(map, request);
        	return request(super.createAdminTempletResponse("/admin/content"));
    	}else {
    		return request(super.createAdminTempletResponse("/admin/user/index"));
    	}*/
    }

    @RequestMapping("/admin/auth/infoacq")
    @Menu(type = "admin", subtype = "infoacq", access = false, admin = true)
    public ModelAndView infoacq(ModelMap map, HttpServletRequest request) {
        String inacq = (String) request.getSession().getAttribute(Constants.CSKEFU_SYSTEM_INFOACQ);
        if (StringUtils.isNotBlank(inacq)) {
            request.getSession().removeAttribute(Constants.CSKEFU_SYSTEM_INFOACQ);
        } else {
            request.getSession().setAttribute(Constants.CSKEFU_SYSTEM_INFOACQ, "true");
        }
        return request(super.createView("redirect:/"));
    }
}