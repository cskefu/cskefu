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
package com.cskefu.cc.controller;

import com.cskefu.cc.acd.ACDWorkMonitor;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.basic.auth.AuthToken;
import com.cskefu.cc.model.AgentStatus;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.model.SystemConfig;
import com.cskefu.cc.model.User;
import com.cskefu.cc.model.UserRole;
import com.cskefu.cc.persistence.repository.UserRepository;
import com.cskefu.cc.persistence.repository.UserRoleRepository;
import com.cskefu.cc.proxy.AgentProxy;
import com.cskefu.cc.proxy.AgentSessionProxy;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * @author CSKefu
 * @version 1.0.1
 */
@Controller
public class LoginController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRes;

    @Autowired
    private AuthToken authToken;

    @Autowired
    private AgentProxy agentProxy;

    @Autowired
    private AgentSessionProxy agentSessionProxy;

    @Autowired
    private UserProxy userProxy;

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Value("${tongji.baidu.sitekey}")
    private String tongjiBaiduSiteKey;

    @Value("${extras.login.banner}")
    private String extrasLoginBanner;

    @Value("${extras.login.chatbox}")
    private String extrasLoginChatbox;

    private void putViewExtras(final ModelAndView view) {
        if (StringUtils.isNotBlank(extrasLoginBanner) && !StringUtils.equalsIgnoreCase(extrasLoginBanner, "off")) {
            view.addObject("extrasLoginBanner", extrasLoginBanner);
        } else {
            view.addObject("extrasLoginBanner", "off");
        }

        if (StringUtils.isNotBlank(extrasLoginChatbox) && !StringUtils.equalsIgnoreCase(extrasLoginChatbox, "off")) {
            view.addObject("extrasLoginChatbox", extrasLoginChatbox);
        } else {
            view.addObject("extrasLoginChatbox", "off");
        }
    }

    /**
     * 登录页面
     *
     * @param request
     * @param response
     * @param referer
     * @param msg
     * @return
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "user", access = true)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response,
            @RequestHeader(value = "referer", required = false) String referer, @Valid String msg) {
        ModelAndView view = new ModelAndView("redirect:/");
        if (request.getSession(true).getAttribute(Constants.USER_SESSION_NAME) == null) {
            view = new ModelAndView("/login");
            if (StringUtils.isNotBlank(request.getParameter("referer"))) {
                referer = request.getParameter("referer");
            }
            if (StringUtils.isNotBlank(referer)) {
                view.addObject("referer", referer);
            }
            Cookie[] cookies = request.getCookies(); // 这样便可以获取一个cookie数组
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie != null && StringUtils.isNotBlank(cookie.getName()) && StringUtils.isNotBlank(
                            cookie.getValue())) {
                        if (cookie.getName().equals(Constants.CSKEFU_SYSTEM_COOKIES_FLAG)) {
                            String flagid;
                            try {
                                flagid = MainUtils.decryption(cookie.getValue());
                                if (StringUtils.isNotBlank(flagid)) {
                                    User user = userRepository.findById(flagid);
                                    if (user != null) {
                                        view = this.processLogin(request, user, referer);
                                    }
                                }
                            } catch (EncryptionOperationNotPossibleException e) {
                                logger.error("[login] error:", e);
                                view = request(super.createView("/public/clearcookie"));
                                return view;
                            } catch (NoSuchAlgorithmException e) {
                                logger.error("[login] error:", e);
                            }
                        }
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(msg)) {
            view.addObject("msg", msg);
        }
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnableregorgi()) {
            view.addObject("show", true);
        }
        if (systemConfig != null) {
            view.addObject("systemConfig", systemConfig);
        }

        if (StringUtils.isNotBlank(tongjiBaiduSiteKey)
                && !StringUtils.equalsIgnoreCase(tongjiBaiduSiteKey, "placeholder")) {
            view.addObject("tongjiBaiduSiteKey", tongjiBaiduSiteKey);
        }

        putViewExtras(view);

        return view;
    }

    /**
     * 提交登录表单
     *
     * @param request
     * @param response
     * @param user
     * @param referer
     * @param sla
     * @return
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "user", access = true)
    public ModelAndView login(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @Valid User user,
            @Valid String referer,
            @Valid String sla) throws NoSuchAlgorithmException {
        ModelAndView view = new ModelAndView("redirect:/");
        if (request.getSession(true).getAttribute(Constants.USER_SESSION_NAME) == null) {
            if (user != null && user.getUsername() != null) {
                final User loginUser = userRepository.findByUsernameAndPasswordAndDatastatus(
                        user.getUsername(), MainUtils.md5(user.getPassword()), false);
                if (loginUser != null && StringUtils.isNotBlank(loginUser.getId())) {
                    view = this.processLogin(request, loginUser, referer);

                    // 自动登录
                    if (StringUtils.equals("1", sla)) {
                        Cookie flagid = new Cookie(
                                Constants.CSKEFU_SYSTEM_COOKIES_FLAG, MainUtils.encryption(loginUser.getId()));
                        flagid.setMaxAge(7 * 24 * 60 * 60);
                        response.addCookie(flagid);
                    }

                    // add authorization code for rest api
                    final String orgi = loginUser.getOrgi();
                    String auth = MainUtils.getUUID();
                    authToken.putUserByAuth(auth, loginUser);
                    userRepository.save(loginUser); // 更新登录状态到数据库
                    response.addCookie((new Cookie("authorization", auth)));

                    // 该登录用户是坐席，并且具有坐席对话的角色
                    if ((loginUser.isAgent() &&
                            loginUser.getRoleAuthMap().containsKey("A01") &&
                            ((boolean) loginUser.getRoleAuthMap().get("A01") == true))
                            || loginUser.isAdmin()) {
                        try {
                            /****************************************
                             * 登录成功，设置该坐席为就绪状态（默认）
                             ****************************************/
                            // https://gitlab.chatopera.com/chatopera/cosinee.w4l/issues/306
                            final AgentStatus agentStatus = agentProxy.resolveAgentStatusByAgentnoAndOrgi(
                                    loginUser.getId(), orgi, loginUser.getSkills());
                            agentStatus.setBusy(false);
                            agentProxy.ready(loginUser, agentStatus, false);

                            // 工作状态记录
                            acdWorkMonitor.recordAgentStatus(agentStatus.getAgentno(),
                                    agentStatus.getUsername(),
                                    agentStatus.getAgentno(),
                                    user.isAdmin(), // 0代表admin
                                    agentStatus.getAgentno(),
                                    MainContext.AgentStatusEnum.OFFLINE.toString(),
                                    MainContext.AgentStatusEnum.READY.toString(),
                                    MainContext.AgentWorkType.MEIDIACHAT.toString(),
                                    orgi, null);

                        } catch (Exception e) {
                            logger.error("[login] set agent status", e);
                        }
                    }
                } else {
                    view = request(super.createView("/login"));
                    if (StringUtils.isNotBlank(referer)) {
                        view.addObject("referer", referer);
                    }

                    putViewExtras(view);

                    view.addObject("msg", "0");
                }
            }
        }
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnableregorgi()) {
            view.addObject("show", true);
        }
        if (systemConfig != null) {
            view.addObject("systemConfig", systemConfig);
        }

        return view;
    }

    /**
     * 处理登录事件
     *
     * @param request
     * @param loginUser
     * @param referer
     * @return
     */
    private ModelAndView processLogin(final HttpServletRequest request, final User loginUser, String referer) {
        ModelAndView view = new ModelAndView();
        if (loginUser != null) {
            // 设置登录用户的状态
            loginUser.setLogin(true);
            // 更新redis session信息，用以支持sso
            agentSessionProxy.updateUserSession(
                    loginUser.getId(), MainUtils.getContextID(request.getSession().getId()), loginUser.getOrgi());
            loginUser.setSessionid(MainUtils.getContextID(request.getSession().getId()));

            if (StringUtils.isNotBlank(referer)) {
                view = new ModelAndView("redirect:" + referer);
            } else {
                view = new ModelAndView("redirect:/");
            }

            // 登录成功 判断是否进入多租户页面
            SystemConfig systemConfig = MainUtils.getSystemConfig();
            if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantconsole()
                    && !loginUser.isAdmin()) {
                view = new ModelAndView("redirect:/apps/tenant/index");
            }
            List<UserRole> userRoleList = userRoleRes.findByOrgiAndUser(loginUser.getOrgi(), loginUser);
            if (userRoleList != null && userRoleList.size() > 0) {
                for (UserRole userRole : userRoleList) {
                    loginUser.getRoleList().add(userRole.getRole());
                }
            }

            // 获取用户部门以及下级部门
            userProxy.attachOrgansPropertiesForUser(loginUser);

            Organ currentOrgan = super.getOrgan(request);

            // 添加角色信息
            userProxy.attachRolesMap(loginUser, currentOrgan);

            loginUser.setLastlogintime(new Date());
            if (StringUtils.isNotBlank(loginUser.getId())) {
                userRepository.save(loginUser);
            }

            super.setUser(request, loginUser);
        }
        return view;
    }

    /**
     * 登出用户
     * code代表登出的原因
     *
     * @param request
     * @param response
     * @param code     登出的代码
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "code", required = false) String code) throws UnsupportedEncodingException {
        final User user = super.getUser(request);
        request.getSession().removeAttribute(Constants.USER_SESSION_NAME);
        request.getSession().invalidate();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && StringUtils.isNotBlank(cookie.getName()) && StringUtils.isNotBlank(
                        cookie.getValue())) {
                    if (cookie.getName().equals(Constants.CSKEFU_SYSTEM_COOKIES_FLAG)) {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(code)) {
            return "redirect:/?msg=" + code;
        }

        return "redirect:/";
    }

    @RequestMapping(value = "/register")
    @Menu(type = "apps", subtype = "user", access = true)
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response, @Valid String msg) {
        ModelAndView view = request(super.createView("redirect:/"));
        if (request.getSession(true).getAttribute(Constants.USER_SESSION_NAME) == null) {
            view = request(super.createView("/register"));
        }
        if (StringUtils.isNotBlank(msg)) {
            view.addObject("msg", msg);
        }
        return view;
    }

    @RequestMapping("/addAdmin")
    @Menu(type = "apps", subtype = "user", access = true)
    public ModelAndView addAdmin(HttpServletRequest request, HttpServletResponse response, @Valid User user) {
        String msg = "";
        msg = validUser(user);
        if (StringUtils.isNotBlank(msg)) {
            return request(super.createView("redirect:/register.html?msg=" + msg));
        } else {
            user.setUname(user.getUsername());
            user.setAdmin(true);
            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setPassword(MainUtils.md5(user.getPassword()));
            }
            user.setOrgi(super.getOrgi());
            userRepository.save(user);
        }
        ModelAndView view = this.processLogin(request, user, "");
        return view;
    }

    private String validUser(User user) {
        String msg = "";
        User tempUser = userRepository.findByUsernameAndDatastatus(user.getUsername(), false);
        if (tempUser != null) {
            msg = "username_exist";
            return msg;
        }
        tempUser = userRepository.findByEmailAndDatastatus(user.getEmail(), false);
        if (tempUser != null) {
            msg = "email_exist";
            return msg;
        }
        tempUser = userRepository.findByMobileAndDatastatus(user.getMobile(), false);
        if (tempUser != null) {
            msg = "mobile_exist";
            return msg;
        }
        return msg;
    }
}