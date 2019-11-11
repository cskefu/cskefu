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
package com.chatopera.cc.controller.api;

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.CousultInvite;
import com.chatopera.cc.persistence.repository.ConsultInviteRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import com.chatopera.cc.util.RestResultType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 在线客服
 * 在线客服设置功能
 */
@RestController
@RequestMapping("/api/webim")
public class ApiWebIMController extends Handler {

    @Autowired
    private ConsultInviteRepository consultInviteRepository;

    /**
     * 获取在线客服
     *
     * @param request
     * @param username 搜索用户名，精确搜索
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "app", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request) {
        return new ResponseEntity<>(new RestResult(RestResultType.OK, consultInviteRepository.findByOrgi(super.getOrgi(request))), HttpStatus.OK);
    }

    /**
     * 修改在线客服信息，只提供修改操作
     *
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @Menu(type = "apps", subtype = "app", access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request, @Valid CousultInvite consult) {
        if (consult != null && !StringUtils.isBlank(consult.getId())) {
            consultInviteRepository.save(consult);
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }
}