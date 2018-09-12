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
package com.chatopera.cc.app.handler.api.rest;

import com.chatopera.cc.util.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.service.repository.AgentUserRepository;
import com.chatopera.cc.util.RestResultType;
import com.chatopera.cc.app.handler.Handler;

@RestController
@RequestMapping("/api/agentuser")
@Api(value = "ACD服务", description = "获取当前对话中的访客")
public class ApiAgentUserController extends Handler{
	
	@Autowired
	private AgentUserRepository agentUserRepository ;

	/**
	 * 获取当前对话中的访客
	 * @param request
	 * @return
	 */
	@RequestMapping( method = RequestMethod.GET)
	@Menu(type = "apps" , subtype = "agentuser" , access = true)
	@ApiOperation("获取当前正在对话的访客信息，包含多种渠道来源的访客")
    public ResponseEntity<RestResult> list(HttpServletRequest request , @Valid String q) {
        return new ResponseEntity<>(new RestResult(RestResultType.OK , agentUserRepository.findByAgentnoAndOrgi(super.getUser(request).getId() , super.getOrgi(request) , new Sort(Direction.ASC,"status"))), HttpStatus.OK);
    }
}