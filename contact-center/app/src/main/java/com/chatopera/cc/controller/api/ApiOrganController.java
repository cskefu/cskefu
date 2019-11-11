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
import com.chatopera.cc.model.Organ;
import com.chatopera.cc.persistence.repository.OrganRepository;
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
 * 组织机构/部门/技能组功能
 */
@RestController
@RequestMapping("/api/organ")
public class ApiOrganController extends Handler{

	@Autowired
	private OrganRepository organRepository;
	
	/**
	 * 返回所有部门
	 * @param request
	 * @param username	搜索用户名，精确搜索
	 * @return
	 */
	@RequestMapping( method = RequestMethod.GET)
	@Menu(type = "apps" , subtype = "organ" , access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request) {
        return new ResponseEntity<>(new RestResult(RestResultType.OK, organRepository.findByOrgi(super.getOrgi(request))), HttpStatus.OK);
    }
	
	/**
	 * 新增或修改部门
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@Menu(type = "apps" , subtype = "organ" , access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request , @Valid Organ organ) {
    	if(organ != null && !StringUtils.isBlank(organ.getName())){
    		organRepository.save(organ) ;
    	}
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }
	
	/**
	 * 删除用户，只提供 按照用户ID删除 ， 并且，不能删除系统管理员
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@Menu(type = "apps" , subtype = "user" , access = true)
    public ResponseEntity<RestResult> delete(HttpServletRequest request , @Valid String id) {
		RestResult result = new RestResult(RestResultType.OK) ; 
    	Organ organ = null ;
    	if(!StringUtils.isBlank(id)){
    		organ = organRepository.findByIdAndOrgi(id, super.getOrgi(request)) ;
    		if(organ != null){	//系统管理员， 不允许 使用 接口删除
    			organRepository.delete(organ);
    		}else{
    			result.setStatus(RestResultType.ORGAN_DELETE);
    		}
    	}
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}