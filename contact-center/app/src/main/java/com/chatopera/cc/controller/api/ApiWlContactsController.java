/*
 * Copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.controller.api;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.model.Contacts;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.es.ContactsRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import com.chatopera.cc.util.RestResultType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.Date;

/**
 * 联系人导入API
 * 联系人管理功能
 * https://wiki.chatopera.com/display/W4L/Wonder4life
 */
@RestController
@RequestMapping("/api/wl/contacts")
public class ApiWlContactsController extends Handler {
    final private static Logger logger = LoggerFactory.getLogger(ApiWlContactsController.class);

    @Autowired
    private ContactsRepository contactsRes;

    /**
     * 返回联系人列表，支持分页，分页参数为 p=1&ps=50，默认分页尺寸为 20条每页
     *
     * @param request
     * @param creater
     * @param q
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String creater, @Valid String q) {
        Page<Contacts> contactsList = null;
        if (StringUtils.isNotBlank(creater)) {
            User user = super.getUser(request);
            contactsList = contactsRes.findByCreaterAndSharesAndOrgi(
                    user.getId(), user.getId(), super.getOrgi(request), false, q, new PageRequest(super.getP(request),
                            super.getPs(
                                    request)));
        } else {
            contactsList = contactsRes.findByOrgi(
                    super.getOrgi(request), false, q, new PageRequest(super.getP(request), super.getPs(request)));
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK, contactsList), HttpStatus.OK);
    }


    /**
     * 联系人
     *
     * @param request
     * @param body
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "contacts", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[wl/contacts api] operations payload {}", j.toString());
        JsonObject result = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        User logined = super.getUser(request);

        if (!j.has("ops")) {
            result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            result.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "create": // 增加联系人类型
                    result = createContact(logined.getId(), logined.getOrgi(), j);
                    break;
                default:
                    logger.info("[wl/contacts api] unknown operation {}", j.toString());
            }
        }
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK);
    }

    /**
     * 创建/更新联系人
     * 通过UID和SID和CID唯一确定一个联系人
     *
     * @param creator
     * @param orgi
     * @param j
     * @return
     */
    private JsonObject createContact(
            final String creator,
            final String orgi,
            final JsonObject j) {
        JsonObject result = new JsonObject();

        if (j.has("uid") && j.has("sid")) {
            final String uid = j.get("uid").getAsString();
            final String sid = j.get("sid").getAsString();
            final String cid = j.get("cid").getAsString();
            Contacts record = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(uid, sid, cid, false);
            boolean isNew = false;
            if (record == null) {
                // create new obj
                record = new Contacts();
                isNew = true;
                record.setWluid(uid);
                record.setWlsid(sid);
                record.setOrgi(orgi);
            } // else, just update exist one.

            // 验证其他必填项
            boolean invalid = false;
            String invalidmsg = "";
            if (isNew && !j.has("username")) {
                invalid = true;
                invalidmsg = "username, ";
            } else if (j.has("username")) {
                record.setUsername(j.get("username").getAsString());
                record.setWlusername(record.getUsername());
                record.setName(record.getUsername());
            }

            if (isNew && !j.has("cid")) {
                invalid = true;
                invalidmsg = "cid, ";
            } else if (j.has("cid")) {
                record.setWlcid(j.get("cid").getAsString());
            }

            if (isNew && !j.has("company_name")) {
                invalid = true;
                invalidmsg = "company_name, ";
            } else if (j.has("company_name")) {
                record.setWlcompany_name(j.get("company_name").getAsString());
            }

            if (isNew && !j.has("system_name")) {
                invalid = true;
                invalidmsg = "system_name, ";
            } else if (j.has("system_name")) {
                record.setWlsystem_name(j.get("system_name").getAsString());
            }

            if (isNew && !j.has("ckind")) {
                invalid = true;
                invalidmsg = "category, ";
            } else if (j.has("ckind")) {
                record.setCkind(j.get("ckind").getAsString());
            }

            if (invalid) {
                result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                result.addProperty(RestUtils.RESP_KEY_ERROR, invalidmsg + " are required.");
                return result;
            }

            // 保存其他值
            if (j.has("touchtime")) {
                try {
                    record.setTouchtime(Constants.QUERY_DATE_FORMATTER.parse(j.get("touchtime").getAsString()));
                } catch (ParseException e) {
                    logger.warn("[createContact] error {}", e.toString());
                    result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
                    result.addProperty(RestUtils.RESP_KEY_ERROR, "invalid format for touchtime.");
                    return result;
                }
            } else if (isNew) {
                record.setTouchtime(new Date());
            }

            if (j.has("skypeid")) {
                record.setSkypeid(j.get("skypeid").getAsString());
            }

            if (j.has("phone")) {
                record.setPhone(j.get("phone").getAsString());
                record.setMobileno(record.getPhone());
            }

            if (j.has("memo")) {
                record.setMemo(j.get("memo").getAsString());
            }

            if (j.has("email")) {
                record.setEmail(j.get("email").getAsString());
            }

            if (j.has("address")) {
                record.setAddress(j.get("address").getAsString());
            }

            if (j.has("shares")) {
                record.setShares(j.get("shares").getAsString());
            } else if (isNew) {
                record.setShares("all");
            }


            if (j.has("gender")) {
                record.setGender(j.get("gender").getAsString());
            }

            if (isNew) {
                record.setId(MainUtils.getUUID());
                record.setCreatetime(new Date());
                record.setCreater(creator);
            } else {
                record.setUpdatetime(new Date());
            }

            contactsRes.save(record);

            JsonObject data = new JsonObject();
            data.addProperty("id", record.getId());
            data.addProperty("ckind", record.getCkind());
            data.addProperty("orgi", record.getOrgi());

            result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            result.add(RestUtils.RESP_KEY_DATA, data);

        } else {
            result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            result.addProperty(RestUtils.RESP_KEY_ERROR, "Invalid contact properties, uid and sid are required.");
        }

        return result;
    }


}
