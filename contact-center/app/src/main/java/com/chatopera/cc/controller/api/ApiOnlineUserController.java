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

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.OnlineUser;
import com.chatopera.cc.persistence.repository.OnlineUserRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import com.chatopera.cc.util.RestResultType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 获取在线访客功能
 */
@RestController
@RequestMapping("/api/online/user")
public class ApiOnlineUserController extends Handler {

    @Autowired
    private OnlineUserRepository onlineUserRepository;

    /**
     * 获取在线客服
     *
     * @param request
     * @param username 搜索用户名，精确搜索
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "sysdic", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String userid) {
        Page<OnlineUser> onlineUserList = null;
        if (!StringUtils.isBlank(userid)) {
            onlineUserList = onlineUserRepository.findByUseridAndOrgi(userid, super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime"));
        } else {
            onlineUserList = onlineUserRepository.findByOrgiAndStatus(super.getOrgi(request), MainContext.OnlineUserStatusEnum.ONLINE.toString(), new PageRequest(super.getP(request), super.getPs(request), Sort.Direction.DESC, "createtime"));
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK, onlineUserList), HttpStatus.OK);
    }
}