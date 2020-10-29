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
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.controller.api.request.RestUtils;
import com.chatopera.cc.exception.CSKefuRestException;
import com.chatopera.cc.model.Tag;
import com.chatopera.cc.model.TagRelation;
import com.chatopera.cc.persistence.repository.TagRelationRepository;
import com.chatopera.cc.persistence.repository.TagRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.json.GsonTools;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * 管理联系人标签
 */
@RestController
@RequestMapping("/api/contacts/tags")
public class ApiContactTagsController extends Handler {
    private static final Logger logger = LoggerFactory.getLogger(ApiContactTagsController.class);
    private static final String TAGTYPE_USER = "user";


    @Autowired
    private TagRepository tagRes;

    @Autowired
    private TagRelationRepository tagRelationRes;

    @Autowired
    private UserRepository userRes;


    /**
     * 获取联系人标签
     *
     * @param j
     * @return
     */
    private JsonObject fetch(JsonObject j) {
        JsonObject resp = new JsonObject();
        if (j.has("contactid") && StringUtils.isNotBlank(j.get("contactid").getAsString())) {
            String contactid = j.get("contactid").getAsString();
            // 获取联系人所有标签
            List<TagRelation> rels = tagRelationRes.findByUserid(contactid);
            HashMap<String, String> tagged = new HashMap<String, String>();

            for (TagRelation t : rels) {
                tagged.put(t.getTagid(), t.getId());
            }

            // 获取所有标签
            List<Tag> all = tagRes.findByOrgiAndTagtype(j.get("orgi").getAsString(), TAGTYPE_USER);
            JsonArray data = new JsonArray();

            for (Tag t : all) {
                JsonObject x = new JsonObject();
                x.addProperty("id", t.getId());
                x.addProperty("name", t.getTag());
                x.addProperty("type", t.getTagtype());
                x.addProperty("color", t.getColor());
                if (tagged.containsKey(t.getId())) {
                    x.addProperty("tagged", true);
                    x.addProperty("xid", tagged.get(t.getId()));
                } else {
                    x.addProperty("tagged", false);
                }
                data.add(x);
            }

            resp.add("data", data);
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        } else {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        }
        return resp;
    }

    /**
     * 创建联系人标签关系
     *
     * @param j
     * @return
     */
    private JsonObject create(JsonObject j) {
        JsonObject resp = new JsonObject();
        // 验证数据
        if ((!j.has("contactid")) || StringUtils.isBlank(j.get("contactid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
            return resp;
        }

        if ((!j.has("tagId")) || StringUtils.isBlank(j.get("tagId").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
            return resp;
        }

        final String tagId = j.get("tagId").getAsString();
        final String contactid = j.get("contactid").getAsString();
        Tag tag = tagRes.findOne(tagId);

        if (tag == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不存在该标签。");
            return resp;
        }

        // 创建关系
        TagRelation rel = new TagRelation();
        rel.setDataid(contactid);
        rel.setUserid(contactid);
        rel.setTagid(tagId);
        tagRelationRes.save(rel);

        JsonObject data = new JsonObject();
        data.addProperty("id", rel.getId());

        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.add(RestUtils.RESP_KEY_DATA, data);

        return resp;
    }

    /**
     * 去掉标签
     *
     * @param j
     * @return
     */
    private JsonObject remove(JsonObject j) {
        JsonObject resp = new JsonObject();
        if ((!j.has("xid")) || StringUtils.isBlank(j.get("xid").getAsString())) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
            return resp;
        }

        TagRelation t = tagRelationRes.findOne(j.get("xid").getAsString());
        if (t == null) {
            resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_4);
            resp.addProperty(RestUtils.RESP_KEY_ERROR, "该联系人没有打这个标签。");
            return resp;
        }

        tagRelationRes.delete(t);
        JsonObject data = new JsonObject();
        data.addProperty("msg", "删除成功。");
        resp.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
        resp.add("data", data);
        return resp;
    }


    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "contacttags", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) throws CSKefuRestException, GsonTools.JsonObjectExtensionConflictException {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[contact tags] operations payload {}", j.toString());
        JsonObject json = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        j.addProperty("creater", super.getUser(request).getId());
        j.addProperty("orgi", super.getOrgi(request));

        if (!j.has("ops")) {
            json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            json.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "fetch":
                    json = fetch(j);
                    break;
                case "create":
                    json = create(j);
                    break;
                case "remove":
                    json = remove(j);
                    break;
                default:
                    json.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
                    json.addProperty(RestUtils.RESP_KEY_ERROR, "不支持的操作。");
                    break;
            }
        }
        return new ResponseEntity<String>(json.toString(), headers, HttpStatus.OK);
    }

}
