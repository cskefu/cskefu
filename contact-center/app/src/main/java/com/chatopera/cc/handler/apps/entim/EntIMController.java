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

package com.chatopera.cc.handler.apps.entim;

import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.handler.Handler;
import com.chatopera.cc.model.IMGroup;
import com.chatopera.cc.model.IMGroupUser;
import com.chatopera.cc.model.RecentUser;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.UserProxy;
import com.chatopera.cc.socketio.client.NettyClients;
import com.chatopera.cc.socketio.message.ChatMessage;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/ent/im")
public class EntIMController extends Handler {

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    private IMGroupRepository imGroupRes;

    @Autowired
    private IMGroupUserRepository imGroupUserRes;

    @Autowired
    private ChatMessageRepository chatMessageRes;

    @Autowired
    private RecentUserRepository recentUserRes;

    @RequestMapping("/index")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/index"));
        view.addObject("organList", organRes.findByOrgi(super.getOrgi(request)));
        List<User> users = userRes.findByOrgiAndDatastatus(super.getOrgi(request), false);

        // TODO: 优化性能
        for (User u : users) {
            UserProxy.attachOrgansPropertiesForUser(u);
        }

        view.addObject("userList", users);

        view.addObject("groupList", imGroupRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request)));

        view.addObject("joinGroupList", imGroupUserRes.findByUserAndOrgi(super.getUser(request), super.getOrgi(request)));

        view.addObject("recentUserList", recentUserRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request)));

        return view;
    }

    @RequestMapping("/skin")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView skin(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/skin"));

        return view;
    }

    @RequestMapping("/point")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView point(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/point"));
        view.addObject("recentUserList", recentUserRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request)));
        return view;
    }

    @RequestMapping("/chat")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView chat(HttpServletRequest request, HttpServletResponse response, @Valid String userid) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/chat"));
        view.addObject("entimuser", userRes.findByIdAndOrgi(userid, super.getOrgi(request)));
        view.addObject("contextid", MainUtils.genNewID(super.getUser(request).getId(), userid));
        view.addObject("online", NettyClients.getInstance().getEntIMClientsNum(userid) > 0);

        Page<ChatMessage> chatMessageList = chatMessageRes.findByContextidAndUseridAndOrgi(userid, super.getUser(request).getId(), super.getOrgi(request), new PageRequest(0, 20, Sort.Direction.DESC, "createtime"));

        view.addObject("chatMessageList", chatMessageList);

        RecentUser recentUser = recentUserRes.findByCreaterAndUserAndOrgi(super.getUser(request).getId(), new User(userid), super.getOrgi(request));
        /**
         * 我的最近联系人
         */
        if (recentUser == null) {
            recentUser = new RecentUser();
            recentUser.setOrgi(super.getOrgi(request));
            recentUser.setCreater(super.getUser(request).getId());
            recentUser.setUser(new User(userid));
        } else {
            recentUser.setNewmsg(0);
        }
        recentUserRes.save(recentUser);
        /**
         * 对方的最近联系人
         */
        recentUser = recentUserRes.findByCreaterAndUserAndOrgi(userid, super.getUser(request), super.getOrgi(request));
        if (recentUser == null) {
            recentUser = new RecentUser();
            recentUser.setOrgi(super.getOrgi(request));
            recentUser.setCreater(userid);
            recentUser.setUser(super.getUser(request));
            recentUserRes.save(recentUser);
        }
        return view;
    }

    @RequestMapping("/chat/more")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView chatMore(HttpServletRequest request, HttpServletResponse response, @Valid String userid, @Valid Date createtime) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/more"));

        Page<ChatMessage> chatMessageList = chatMessageRes.findByContextidAndUseridAndOrgiAndCreatetimeLessThan(userid, super.getUser(request).getId(), super.getOrgi(request), createtime, new PageRequest(0, 20, Sort.Direction.DESC, "createtime"));
        view.addObject("chatMessageList", chatMessageList);

        return view;
    }

    @RequestMapping("/group")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView groupMore(HttpServletRequest request, HttpServletResponse response, @Valid String id) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/group/index"));
        IMGroup imGroup = imGroupRes.findById(id);
        view.addObject("imGroup", imGroup);
        view.addObject("imGroupUserList", imGroupUserRes.findByImgroupAndOrgi(imGroup, super.getOrgi(request)));
        view.addObject("contextid", id);
        view.addObject("chatMessageList", chatMessageRes.findByContextidAndOrgi(id, super.getOrgi(request), new PageRequest(0, 20, Sort.Direction.DESC, "createtime")));
        return view;
    }

    @RequestMapping("/group/more")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView group(HttpServletRequest request, HttpServletResponse response, @Valid String id, @Valid Date createtime) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/group/more"));
        view.addObject("chatMessageList", chatMessageRes.findByContextidAndOrgiAndCreatetimeLessThan(id, super.getOrgi(request), createtime, new PageRequest(0, 20, Sort.Direction.DESC, "createtime")));
        return view;
    }

    @RequestMapping("/group/user")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView user(HttpServletRequest request, HttpServletResponse response, @Valid String id) {
        ModelAndView view = request(super.createEntIMTempletResponse("/apps/entim/group/user"));
        User logined = super.getUser(request);
        UserProxy.attachOrgansPropertiesForUser(logined);

        // TODO: 优化性能
        List<String> organIds = UserProxy.findOrgansByUserid(logined.getId());
        List<User> users = UserProxy.findByOrganInAndDatastatus(organIds, false);
        for (User u : users) {
            UserProxy.attachOrgansPropertiesForUser(u);
        }

        IMGroup imGroup = imGroupRes.findById(id);

        view.addObject("imGroup", imGroup);
        view.addObject("organList", logined.getOrgans().values());
        view.addObject("userList", users);
        view.addObject("imGroupUserList", imGroupUserRes.findByImgroupAndOrgi(imGroup, super.getOrgi(request)));

        return view;
    }

    @RequestMapping("/group/seluser")
    @Menu(type = "im", subtype = "entim", access = false)
    public void seluser(HttpServletRequest request, HttpServletResponse response, @Valid String id, @Valid String user) {
        IMGroup imGroup = new IMGroup();
        imGroup.setId(id);
        User curUser = new User();
        curUser.setId(user);
        IMGroupUser imGroupUser = imGroupUserRes.findByImgroupAndUserAndOrgi(imGroup, curUser, super.getOrgi(request));
        if (imGroupUser == null) {
            imGroupUser = new IMGroupUser();
            imGroupUser.setImgroup(imGroup);
            imGroupUser.setUser(curUser);
            imGroupUser.setOrgi(super.getUser(request).getOrgi());
            imGroupUser.setCreater(super.getUser(request).getId());
            imGroupUserRes.save(imGroupUser);
        }
    }

    @RequestMapping("/group/rmuser")
    @Menu(type = "im", subtype = "entim", access = false)
    public void rmluser(HttpServletRequest request, HttpServletResponse response, @Valid String id, @Valid String user) {
        IMGroup imGroup = new IMGroup();
        imGroup.setId(id);
        User curUser = new User();
        curUser.setId(user);
        IMGroupUser imGroupUser = imGroupUserRes.findByImgroupAndUserAndOrgi(imGroup, curUser, super.getOrgi(request));
        if (imGroupUser != null) {
            imGroupUserRes.delete(imGroupUser);
        }
    }

    @RequestMapping("/group/tipmsg")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView tipmsg(HttpServletRequest request, HttpServletResponse response, @Valid String id, @Valid String tipmsg) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/group/tipmsg"));
        IMGroup imGroup = imGroupRes.findById(id);
        if (imGroup != null) {
            imGroup.setTipmessage(tipmsg);
            imGroupRes.save(imGroup);
        }
        view.addObject("imGroup", imGroup);
        return view;
    }


    @RequestMapping("/group/save")
    @Menu(type = "im", subtype = "entim", access = false)
    public ModelAndView groupsave(HttpServletRequest request, HttpServletResponse response, @Valid IMGroup group) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/entim/group/grouplist"));
        if (!StringUtils.isBlank(group.getName()) && imGroupRes.countByNameAndOrgi(group.getName(), super.getOrgi(request)) == 0) {
            group.setOrgi(super.getUser(request).getOrgi());
            group.setCreater(super.getUser(request).getId());
            imGroupRes.save(group);

            IMGroupUser imGroupUser = new IMGroupUser();
            imGroupUser.setOrgi(super.getUser(request).getOrgi());
            imGroupUser.setUser(super.getUser(request));
            imGroupUser.setImgroup(group);
            imGroupUser.setAdmin(true);
            imGroupUser.setCreater(super.getUser(request).getId());
            imGroupUserRes.save(imGroupUser);
        }
        view.addObject("groupList", imGroupRes.findByCreaterAndOrgi(super.getUser(request).getId(), super.getOrgi(request)));

        view.addObject("joinGroupList", imGroupUserRes.findByUserAndOrgi(super.getUser(request), super.getOrgi(request)));

        return view;
    }
}