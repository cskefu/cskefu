/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2023 Chatopera Inc, <https://www.chatopera.com>
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

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.basic.Viewport;
import com.cskefu.cc.basic.auth.BearerTokenMgr;
import com.cskefu.cc.cache.Cache;
import com.cskefu.cc.controller.api.QueryParams;
import com.cskefu.cc.exception.CSKefuException;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.model.StreamingFile;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.blob.JpaBlobHelper;
import com.cskefu.cc.persistence.repository.StreamingFileRepository;
import com.cskefu.cc.proxy.OrganProxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cskefu.cc.basic.Constants.AUTH_TOKEN_TYPE_BASIC;
import static com.cskefu.cc.basic.Constants.AUTH_TOKEN_TYPE_BEARER;

@Controller
@SessionAttributes
public class Handler {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Autowired
    private StreamingFileRepository streamingFileRes;

    @Autowired
    private Cache cache;

    @Autowired
    private BearerTokenMgr bearerTokenMgr;

    @Autowired
    private OrganProxy organProxy;

    public final static int PAGE_SIZE_BG = 1;
    public final static int PAGE_SIZE_TW = 20;
    public final static int PAGE_SIZE_FV = 50;
    public final static int PAGE_SIZE_HA = 100;

    private long starttime = System.currentTimeMillis();

    public User getUser(HttpServletRequest request) {
        User user = (User) request.getSession(true).getAttribute(Constants.USER_SESSION_NAME);
        if (user == null) {
            String authorization = request.getHeader("authorization");
            if (StringUtils.isBlank(authorization) && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("authorization")) {
                        authorization = cookie.getValue();
                        break;
                    }
                }
            }

            // trim token
            if (StringUtils.isNotBlank(authorization)) {
                String authorizationTrimed = authorization;
                if (authorization.startsWith(String.format("%s ", AUTH_TOKEN_TYPE_BEARER))) {
                    authorizationTrimed = StringUtils.substring(authorization, 7);
                    if (StringUtils.isNotBlank(authorizationTrimed)) {
                        user = bearerTokenMgr.retrieve(authorizationTrimed);
                    }
                } else if (authorization.startsWith(String.format("%s ", AUTH_TOKEN_TYPE_BASIC))) {
                    authorizationTrimed = StringUtils.substring(authorization, 6);
                    // TODO https://gitlab.chatopera.com/chatopera/chatopera.bot/issues/1292
                    // get user with basic token mgr
                }
            }

            if (user == null) {
                user = new User();
                user.setId(MainUtils.getContextID(request.getSession().getId()));
                user.setUsername(Constants.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                user.setSessionid(user.getId());
            }
        } else {
            user.setSessionid(MainUtils.getContextID(request.getSession().getId()));
        }
        return user;
    }

    /**
     * 获得登录账号的当前导航的组织机构
     *
     * @param request
     * @return
     */
    public Organ getOrgan(HttpServletRequest request) {
        User user = getUser(request);
        if (user.getOrgans() != null) {

            Organ organ = (Organ) request.getSession(true).getAttribute(Constants.ORGAN_SESSION_NAME);
            if (organ == null) {
                organ = organProxy.getDefault(user.getOrgans().values());

                if (organ != null) {
                    request.getSession(true).setAttribute(Constants.ORGAN_SESSION_NAME, organ);
                }
            }
            return organ;
        } else {
            return null;
        }
    }

    /**
     * 获得该用户的组织机构及附属组织机构的数组
     *
     * @param user
     * @return
     */
    public List<String> getMyAffiliatesFlat(final User user) {
        ArrayList<String> organIds = new ArrayList<>(user.getAffiliates());
        return organIds;
    }

    /**
     * 获得当前用户导航的组织机构和附属组织机构的信息
     *
     * @param user
     * @return
     */
    public List<String> getMyCurrentAffiliatesFlat(final User user) {
        ArrayList<String> organIds = new ArrayList<>(user.getCurrOrganAffiliates());
        return organIds;
    }

    /**
     * 构建ElasticSearch基于部门查询的Filter
     *
     * @param request
     * @return
     * @throws CSKefuException
     */
    public boolean preCheckPermissions(final HttpServletRequest request)
            throws CSKefuException {
        // 组合部门条件
        User u = getUser(request);
        if (u == null) {
            throw new CSKefuException("[esOrganFilter] 未能获取到登录用户。");
        } else if (u.isAdmin()) {
            // 管理员, 查看任何数据
            return true;
        } else {
            // 用户在部门中，通过部门过滤数据
            // String[] values = u.getAffiliates().toArray(new
            // String[u.getAffiliates().size()]);
            // boolQueryBuilder.filter(termsQuery("organ", values));
            // 不对contacts进行过滤，普通用户也可以查看该租户的任何数据
            // return true;
        }
        return true;
    }

    /**
     * 创建或从HTTP会话中查找到访客的User对象，该对象不在数据库中，属于临时会话。
     * 这个User很可能是打开一个WebIM访客聊天控件，随机生成用户名，之后和Contact关联
     * 这个用户可能关联一个OnlineUser，如果开始给TA分配坐席
     *
     * @param request
     * @param userid
     * @param nickname
     * @return
     */
    public User getIMUser(HttpServletRequest request, String userid, String nickname) {
        User user = (User) request.getSession(true).getAttribute(Constants.IM_USER_SESSION_NAME);
        if (user == null) {
            user = new User();
            if (StringUtils.isNotBlank(userid)) {
                user.setId(userid);
            } else {
                user.setId(MainUtils.getContextID(request.getSession().getId()));
            }
            if (StringUtils.isNotBlank(nickname)) {
                user.setUsername(nickname);
            } else {
                Map<String, String> sessionMessage = cache.findOneSystemMapById(request.getSession().getId());
                if (sessionMessage != null) {
                    String struname = sessionMessage.get("username");
                    String strcname = sessionMessage.get("company_name");

                    user.setUsername(struname + "@" + strcname);
                } else {
                    user.setUsername(Constants.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                }
            }
            user.setSessionid(user.getId());
        } else {
            user.setSessionid(MainUtils.getContextID(request.getSession().getId()));
        }
        return user;
    }

    public User getIMUser(HttpServletRequest request, String userid, String nickname, String sessionid) {
        User user = (User) request.getSession(true).getAttribute(Constants.IM_USER_SESSION_NAME);
        if (user == null) {
            user = new User();
            if (StringUtils.isNotBlank(userid)) {
                user.setId(userid);
            } else {
                user.setId(MainUtils.getContextID(request.getSession().getId()));
            }
            if (StringUtils.isNotBlank(nickname)) {
                user.setUsername(nickname);
            } else {
                Map<String, String> sessionMessage = cache.findOneSystemMapById(sessionid);
                if (sessionMessage != null) {
                    String struname = sessionMessage.get("username");
                    String strcname = sessionMessage.get("company_name");

                    user.setUsername(struname + "@" + strcname);
                } else {
                    user.setUsername(Constants.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                }
            }
            user.setSessionid(user.getId());
        } else {
            user.setSessionid(MainUtils.getContextID(request.getSession().getId()));
        }
        return user;
    }

    public void setUser(HttpServletRequest request, User user) {
        request.getSession(true).removeAttribute(Constants.USER_SESSION_NAME);
        request.getSession(true).setAttribute(Constants.USER_SESSION_NAME, user);
    }

    /**
     * 创建系统监控的 模板页面
     *
     * @param page
     * @return
     */
    public Viewport createViewIncludedByFreemarkerTplForAdmin(String page) {
        return new Viewport("/admin/include/tpl", page);
    }

    /**
     * 创建系统监控的 模板页面
     *
     * @param page
     * @return
     */
    public Viewport createViewIncludedByFreemarkerTpl(String page) {
        return new Viewport("/apps/include/tpl", page);
    }

    /**
     * 创建系统监控的 模板页面
     *
     * @param page
     * @return
     */
    public Viewport createViewIncludedByFreemarkerTplForEntIM(final String page) {
        return new Viewport("/apps/entim/include/tpl", page);
    }

    public Viewport createView(final String page) {
        return new Viewport(page);
    }

    /**
     * @param data
     * @return
     */
    public ModelAndView request(Viewport data) {
        return new ModelAndView(data.getTemplate() != null ? data.getTemplate() : data.getPage(), "data", data);
    }

    public int getP(HttpServletRequest request) {
        int page = 0;
        String p = request.getParameter("p");
        if (StringUtils.isNotBlank(p) && p.matches("[\\d]*")) {
            page = Integer.parseInt(p);
            if (page > 0) {
                page = page - 1;
            }
        }
        return page;
    }

    public int getPs(HttpServletRequest request) {
        int pagesize = PAGE_SIZE_TW;
        String ps = request.getParameter("ps");
        if (StringUtils.isNotBlank(ps) && ps.matches("[\\d]*")) {
            pagesize = Integer.parseInt(ps);
        }
        return pagesize;
    }

    public int getP(QueryParams params) {
        int page = 0;
        if (params != null && StringUtils.isNotBlank(params.getP()) && params.getP().matches("[\\d]*")) {
            page = Integer.parseInt(params.getP());
            if (page > 0) {
                page = page - 1;
            }
        }
        return page;
    }

    public int getPs(QueryParams params) {
        int pagesize = PAGE_SIZE_TW;
        if (params != null && StringUtils.isNotBlank(params.getPs()) && params.getPs().matches("[\\d]*")) {
            pagesize = Integer.parseInt(params.getPs());
        }
        return pagesize;
    }

    public int get50Ps(HttpServletRequest request) {
        int pagesize = PAGE_SIZE_FV;
        String ps = request.getParameter("ps");
        if (StringUtils.isNotBlank(ps) && ps.matches("[\\d]*")) {
            pagesize = Integer.parseInt(ps);
        }
        return pagesize;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    /**
     * 使用Blob保存文件
     *
     * @param multipart
     * @return id
     * @throws IOException
     */
    public String saveImageFileWithMultipart(MultipartFile multipart) throws IOException {
        StreamingFile sf = new StreamingFile();
        final String fileid = MainUtils.getUUID();
        sf.setId(fileid);
        sf.setMime(multipart.getContentType());
        sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
        sf.setName(multipart.getOriginalFilename());
        streamingFileRes.save(sf);
        return fileid;
    }

    /**
     * 使用Blob保存文件
     *
     * @param dataStr Data URL 图片数据
     * @return id
     * @throws IOException
     */
    public String saveImageFileWithDataURL(String dataStr) throws IOException {
        String[] cell = dataStr.split(";");
        String mime = cell[0].substring(5);
        String base64Str = cell[1].substring(7);
        byte[] buf = Base64.decodeBase64(base64Str);

        StreamingFile sf = new StreamingFile();
        final String fileid = MainUtils.getUUID();
        sf.setId(fileid);
        sf.setMime(mime);
        sf.setData(jpaBlobHelper.createBlob(new ByteArrayInputStream(buf),
                buf.length));
        sf.setName(fileid);
        streamingFileRes.save(sf);
        return fileid;
    }

    public String getSchema(HttpServletRequest request) {
        String schema = request.getScheme();
        String headerProto = request.getHeader("X-Forwarded-Proto");
        if (StringUtils.isNotBlank(headerProto)) {
            schema = headerProto;
        }
        return schema;
    }

}
