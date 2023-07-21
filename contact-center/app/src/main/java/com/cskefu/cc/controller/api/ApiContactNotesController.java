/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.controller.api;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.util.restapi.RestUtils;
import com.cskefu.cc.exception.CSKefuRestException;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.ContactNotesRepository;
import com.cskefu.cc.persistence.repository.ContactsRepository;
import com.cskefu.cc.persistence.repository.OrganRepository;
import com.cskefu.cc.persistence.repository.OrganUserRepository;
import com.cskefu.cc.persistence.repository.UserRepository;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.json.GsonTools;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 联系人笔记
 * 管理联系人笔记
 */
@RestController
@RequestMapping("/api/contacts/notes")
public class ApiContactNotesController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiContactNotesController.class);

    @Autowired
    private ContactNotesRepository contactNotesRes;


    @Autowired
    private ContactsRepository contactsRes;


    @Autowired
    private UserRepository userRes;


    @Autowired
    private OrganRepository organRes;


    @Autowired
    private OrganUserRepository organUserRes;

    /**
     * 获取创建人
     *
     * @param creater
     * @return
     */
    private JsonObject creater(final String creater) {
        JsonObject data = new JsonObject();
        // 增加创建人
        User u = userRes.findById(creater).orElse(null);
        if (u != null) {
            data.addProperty("creater", u.getId());
            data.addProperty("creatername", u.getUname());

            final List<OrganUser> organs = organUserRes.findByUserid(u.getId());


            // 获取创建者部门
            if (organs != null && organs.size() > 0) {

                JsonArray y = new JsonArray();

                for (final OrganUser organ : organs) {
                    Organ o = organRes.findById(organ.getOrgan()).orElse(null);
                    if (o != null) {
                        JsonObject x = new JsonObject();
                        x.addProperty("createrorgan", o.getName());
                        x.addProperty("createrorganid", o.getId());
                        y.add(x);
                    }
                }
                data.add("organs", y);
            }
        } else {
            logger.warn("[contact notes] detail [{}] 无法得到创建者。", creater);
        }
        return data;
    }

    /**
     * 获取笔记详情
     *
     * @param j
     * @return
     */
    private JsonObject detail(final JsonObject j) throws GsonTools.JsonObjectExtensionConflictException {
        logger.info("[contact note] detail {}] {}", j.toString());
        JsonObject resp = new JsonObject();
        // TODO 增加权限检查
        if (j.has("id") && StringUtils.isNotBlank(j.get("id").getAsString())) {
            ContactNotes cn = contactNotesRes.findById(j.get("id").getAsString()).orElse(null);
            if (cn != null) {
                JsonObject data = new JsonObject();
                data.addProperty("contactid", cn.getContactid());
                data.addProperty("category", cn.getCategory());
                data.addProperty("createtime", Constants.DISPLAY_DATE_FORMATTER.format(cn.getCreatetime()));
                data.addProperty("updatetime", Constants.DISPLAY_DATE_FORMATTER.format(cn.getUpdatetime()));
                data.addProperty("content", cn.getContent());
                data.addProperty("agentuser", cn.getAgentuser());
                data.addProperty("onlineuser", cn.getOnlineuser());
                GsonTools.extendJsonObject(data, GsonTools.ConflictStrategy.PREFER_FIRST_OBJ, creater(cn.getCreater()));
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
                resp.add(RestUtils.RESP_KEY_DATA, data);
            } else {
                resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
                resp.addProperty(RestUtils.RESP_KEY_ERROR, "不存在该联系人笔记。");
            }
        }
        return resp;
    }

    /**
     * 创建联系人笔记
     *
     * @param payload
     * @return
     */
    private JsonObject create(final JsonObject payload) throws GsonTools.JsonObjectExtensionConflictException {
        logger.info("[contact note] create {}", payload.toString());
        JsonObject resp = new JsonObject();
        // validate parameters
        String invalid = validateCreatePayload(payload);
        if (invalid != null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, invalid);
            return resp;
        }

        ContactNotes cn = new ContactNotes();
        cn.setId(MainUtils.getUUID());
        cn.setCategory(payload.get("category").getAsString());
        cn.setContent(payload.get("content").getAsString());
        cn.setCreater(payload.get("creater").getAsString());
        cn.setContactid(payload.get("contactid").getAsString());
        cn.setDatastatus(false);

        Date dt = new Date();
        cn.setCreatetime(dt);
        cn.setUpdatetime(dt);

        if (payload.has("agentuser")) {
            cn.setAgentuser(payload.get("agentuser").getAsString());
        }

        if (payload.has("onlineuser")) {
            cn.setOnlineuser(payload.get("onlineuser").getAsString());
        }

        contactNotesRes.save(cn);
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        JsonObject data = new JsonObject();
        data.addProperty("id", cn.getId());
        data.addProperty("updatetime", Constants.DISPLAY_DATE_FORMATTER.format(dt));
        GsonTools.extendJsonObject(data, GsonTools.ConflictStrategy.PREFER_NON_NULL, creater(cn.getCreater()));
        resp.add("data", data);
        return resp;
    }

    /**
     * 验证创建数据
     *
     * @param payload
     * @return
     */
    private String validateCreatePayload(JsonObject payload) {
        if (!payload.has("category")) {
            return "参数传递不合法，没有[category]。";
        }

        if ((!payload.has("content")) || StringUtils.isBlank(payload.get("content").getAsString())) {
            return "参数传递不合法，没有[content]。";
        }

        if ((!payload.has("contactid")) || StringUtils.isBlank(payload.get("contactid").getAsString())) {
            return "参数传递不合法，没有[contactid]。";
        } else {
            Contacts c = contactsRes.findById(payload.get("contactid").getAsString()).orElse(null);
            if (c == null)
                return "参数不合法，不存在该联系人。";
        }

        return null;
    }

    /**
     * Build query string
     *
     * @param j
     * @return
     */
    private String querybuilder(final JsonObject j) {
        StringBuffer sb = new StringBuffer();
        return sb.toString();
    }

    /**
     * 根据联系人ID获取联系人笔记列表
     *
     * @param j
     * @param request
     * @return
     */
    private JsonObject fetch(final JsonObject j, final HttpServletRequest request) throws GsonTools.JsonObjectExtensionConflictException {
        logger.info("[contact note] fetch [{}]", j.toString());
        JsonObject resp = new JsonObject();
        if ((!j.has("contactid")) || StringUtils.isBlank(j.get("contactid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "参数传递不合法，没有[contactid]。");
            return resp;
        }
        final String cid = j.get("contactid").getAsString();
        Contacts c = contactsRes.findById(cid).orElse(null);

        if (c == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不存在该联系人。");
            return resp;
        }

        String q = querybuilder(j);

        Page<ContactNotes> cns = contactNotesRes.findByContactidOrderByCreatetimeDesc(cid, PageRequest.of(super.getP(request), super.getPs(request)));

        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.addProperty("size", cns.getSize());
        resp.addProperty("number", cns.getNumber());
        resp.addProperty("totalPage", cns.getTotalPages());
        resp.addProperty("totalElements", cns.getTotalElements());

        // 转化 page 为 json
        JsonArray data = new JsonArray();
        for (ContactNotes cn : cns) {
            if (cn != null) {
                JsonObject x = new JsonObject();
                x.addProperty("contactid", cn.getContactid());
                x.addProperty("category", cn.getCategory());
                x.addProperty("createtime", Constants.DISPLAY_DATE_FORMATTER.format(cn.getCreatetime()));
                x.addProperty("updatetime", Constants.DISPLAY_DATE_FORMATTER.format(cn.getUpdatetime()));
                x.addProperty("content", cn.getContent());
                x.addProperty("agentuser", cn.getAgentuser());
                x.addProperty("onlineuser", cn.getOnlineuser());
                GsonTools.extendJsonObject(x, GsonTools.ConflictStrategy.PREFER_FIRST_OBJ, creater(cn.getCreater()));
                data.add(x);
            }
        }

        resp.add("data", data);
        return resp;
    }

    /**
     * 联系人笔记
     *
     * @param request
     * @param body
     * @return
     * @throws CSKefuRestException
     * @throws GsonTools.JsonObjectExtensionConflictException
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "contactnotes", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) throws CSKefuRestException, GsonTools.JsonObjectExtensionConflictException {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[contact note] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        j.addProperty("creater", super.getUser(request).getId());

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "create":
                    json = create(j);
                    break;
                case "detail":
                    json = detail(j);
                    break;
                case "fetch":
                    json = fetch(j, request);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不支持的操作。");
                    break;
            }
        }
        return new ResponseEntity<>(json.toString(), headers, HttpStatus.OK);
    }


}
