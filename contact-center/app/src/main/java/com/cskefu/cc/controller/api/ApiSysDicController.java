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
package com.cskefu.cc.controller.api;

import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.controller.admin.system.SysDicController;
import com.cskefu.cc.controller.api.request.RestUtils;
import com.cskefu.cc.model.Dict;
import com.cskefu.cc.model.SysDic;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.SysDicRepository;
import com.cskefu.cc.util.CskefuIdGenerator;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.RestResult;
import com.cskefu.cc.util.RestResultType;
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
import javax.validation.Valid;
import java.util.Date;

/**
 * 数据字典
 * 数据字典功能
 */
@RestController
@RequestMapping("/api/sysdic")
public class ApiSysDicController extends Handler {

    private final static Logger logger = LoggerFactory.getLogger(ApiSysDicController.class);

    @Autowired
    private SysDicRepository sysDicRes;

    @Autowired
    private SysDicController sysDicCtrl;

    /**
     * 获取数据字典
     *
     * @param request
     * @param code
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "sysdic", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String code) {
        return new ResponseEntity<>(new RestResult(RestResultType.OK, Dict.getInstance().getDic(code)), HttpStatus.OK);
    }


    /**
     * 创建联系人类型
     * @param creator
     * @param orgi
     * @param payload
     * @return
     */
    private JsonObject saveCkind(final String creator, final String orgi, final JsonObject payload) {
        JsonObject result = new JsonObject();
        SysDic parent = sysDicRes.findByCode("com.dic.contacts.ckind");
        if (parent == null) {
            result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_2);
            result.addProperty(RestUtils.RESP_KEY_ERROR, "系统数据字典中不存在【联系人类型】。");
        } else if ((!payload.has("name"))) {
            result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_3);
            result.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的参数，缺少`name`");
        } else {
            SysDic record = new SysDic();
            record.setCreater(creator);
            record.setOrgi(orgi);
            record.setCreatetime(new Date());
            record.setParentid(parent.getId());
            record.setDicid(parent.getId());

            record.setName(payload.get("name").getAsString());
            record.setCode(CskefuIdGenerator.randomAlphaNumeric(3));
            record.setTitle("pub");

            record.setHaschild(false);
            sysDicRes.save(record);
            sysDicCtrl.reloadSysDicItem(record, orgi);

            JsonObject data = new JsonObject();
            data.addProperty("id", record.getId());
            data.addProperty("parentid", record.getParentid());
            data.addProperty("dicid", record.getDicid());

            result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_SUCC);
            result.add(RestUtils.RESP_KEY_DATA, data);
        }
        return result;
    }


    /**
     * 数据字典
     * @param request
     * @param body
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "sysdict", access = true)
    public ResponseEntity<String> operations(HttpServletRequest request, @RequestBody final String body) {
        final JsonObject j = (new JsonParser()).parse(body).getAsJsonObject();
        logger.info("[sysdict api] operations payload {}", j.toString());
        JsonObject result = new JsonObject();
        HttpHeaders headers = RestUtils.header();
        User logined = super.getUser(request);

        if (!j.has("ops")) {
            result.addProperty(RestUtils.RESP_KEY_RC, RestUtils.RESP_RC_FAIL_1);
            result.addProperty(RestUtils.RESP_KEY_ERROR, "不合法的请求参数。");
        } else {
            switch (StringUtils.lowerCase(j.get("ops").getAsString())) {
                case "com.dic.contacts.ckind::create": // 增加联系人类型
                    result = saveCkind(logined.getId(), logined.getOrgi(), j);
                    break;
                default:
                    logger.info("[api] unknown operation {}", j.toString());
            }
        }
        return new ResponseEntity<String>(result.toString(), headers, HttpStatus.OK);
    }


}