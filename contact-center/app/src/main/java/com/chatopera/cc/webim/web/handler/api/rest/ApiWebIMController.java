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
package com.chatopera.cc.webim.web.handler.api.rest;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.webim.util.RestResult;
import com.chatopera.cc.webim.web.model.CousultInvite;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chatopera.cc.webim.service.repository.ConsultInviteRepository;
import com.chatopera.cc.webim.util.RestResultType;
import com.chatopera.cc.webim.web.handler.Handler;

@RestController
@RequestMapping("/api/webim")
@Api(value = "在线客服" , description = "在线客服设置功能")
public class ApiWebIMController extends Handler{

	@Autowired
	private ConsultInviteRepository consultInviteRepository;
	
	/**
	 * 返回所有部门
	 * @param request
	 * @param username	搜索用户名，精确搜索
	 * @return
	 */
	@RequestMapping( method = RequestMethod.GET)
	@Menu(type = "apps" , subtype = "webim" , access = true)
	@ApiOperation("获取在线客服")
    public ResponseEntity<RestResult> list(HttpServletRequest request) {
        return new ResponseEntity<>(new RestResult(RestResultType.OK, consultInviteRepository.findByOrgi(super.getOrgi(request))), HttpStatus.OK);
    }
	
	/**
	 * 修改在线客服
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@Menu(type = "apps" , subtype = "webim" , access = true)
	@ApiOperation("修改在线客服信息，只提供修改操作")
    public ResponseEntity<RestResult> put(HttpServletRequest request , @Valid CousultInvite consult) {
    	if(consult != null && !StringUtils.isBlank(consult.getId())){
    		consultInviteRepository.save(consult) ;
    	}
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }
}