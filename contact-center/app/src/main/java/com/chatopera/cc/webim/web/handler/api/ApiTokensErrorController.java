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
package com.chatopera.cc.webim.web.handler.api;

import com.chatopera.cc.webim.util.RestResult;
import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.webim.util.RestResultType;
import com.chatopera.cc.webim.web.handler.Handler;

@RestController
@RequestMapping("/tokens/error")
@Api(value = "登录服务", description = "Token验证失败")
public class ApiTokensErrorController extends Handler{
    @SuppressWarnings("rawtypes")
	@RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps" , subtype = "token" , access = true)
    public ResponseEntity error(HttpServletRequest request) {
        return new ResponseEntity<>(new RestResult(RestResultType.AUTH_ERROR), HttpStatus.OK);
    }
}