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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatopera.cc.app.service.repository.AgentServiceRepository;
import com.chatopera.cc.util.RestResultType;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.AgentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/leavemsg")
@Api(value = "访客留言" , description = "访客留言功能")
public class ApiLeavemsgController extends Handler{

	@Autowired
	private AgentServiceRepository agentServiceRepository;
	
	/**
	 * 返回所有部门
	 * @param request
	 * @param username	搜索用户名，精确搜索
	 * @return
	 */
	@RequestMapping("/list")
	@Menu(type = "apps" , subtype = "app" , access = true)
	@ApiOperation("获取留言列表")
    public ResponseEntity<RestResult> list(HttpServletRequest request , @RequestBody RequestValues<AgentService> values) {
		Page<AgentService> page = agentServiceRepository.findAll(new Specification<AgentService>(){
			@Override
			public Predicate toPredicate(Root<AgentService> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();  
				list.add(cb.equal(root.get("leavemsg").as(Boolean.class), true)) ;
				
				list.add(cb.equal(root.get("leavemsgstatus").as(String.class), MainContext.LeaveMsgStatus.NOTPROCESS.toString())) ;
				
				Predicate[] p = new Predicate[list.size()];  
			    return cb.and(list.toArray(p));   
			}
		}, new PageRequest(super.getP(values.getQuery()), super.getPs(values.getQuery()), Sort.Direction.DESC, "createtime")) ;
        return new ResponseEntity<>(new RestResult(RestResultType.OK,page), HttpStatus.OK);
    }
}