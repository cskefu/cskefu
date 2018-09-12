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
package com.chatopera.cc.app.handler.api;

import com.chatopera.cc.app.MainUtils;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.service.cache.CacheHelper;
import com.chatopera.cc.app.service.repository.UserRoleRepository;
import com.chatopera.cc.app.model.User;
import com.chatopera.cc.app.model.UserRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chatopera.cc.app.MainContext;
import com.chatopera.cc.app.service.repository.UserRepository;
import com.chatopera.cc.app.handler.Handler;

@RestController
@RequestMapping("/tokens")
@Api(value = "登录服务", description = "账号密码登录")
public class ApiLoginController extends Handler{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository userRoleRes ;

    @SuppressWarnings("rawtypes")
	@RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps" , subtype = "token" , access = true)
    @ApiOperation("登录服务，传入登录账号和密码")
    public ResponseEntity login(HttpServletRequest request , HttpServletResponse response , @Valid String username, @Valid String password) {
    	User loginUser = userRepository.findByUsernameAndPassword(username , MainUtils.md5(password)) ;
    	ResponseEntity entity = null ;
        if(loginUser!=null && !StringUtils.isBlank(loginUser.getId())){
        	loginUser.setLogin(true);
        	List<UserRole> userRoleList = userRoleRes.findByOrgiAndUser(loginUser.getOrgi(), loginUser);
        	if(userRoleList!=null && userRoleList.size()>0){
        		for(UserRole userRole : userRoleList){
        			loginUser.getRoleList().add(userRole.getRole()) ;
        		}
        	}
        	loginUser.setLastlogintime(new Date());
        	if(!StringUtils.isBlank(loginUser.getId())){
        		userRepository.save(loginUser) ;
        	}
        	String auth = MainUtils.getUUID();
        	CacheHelper.getApiUserCacheBean().put(auth, loginUser, MainContext.SYSTEM_ORGI);
        	entity = new ResponseEntity<>(auth, HttpStatus.OK) ;
        	response.addCookie(new Cookie("authorization",auth));
        }else{
        	entity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED) ;
        }
        return entity;
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps" , subtype = "token" , access = true)
    public ResponseEntity error(HttpServletRequest request) {
    	User data = super.getUser(request);
        return new ResponseEntity<>(data , data!=null ?  HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("rawtypes")
	@RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity logout(HttpServletRequest request , @RequestHeader(value="authorization") String authorization) {
    	CacheHelper.getApiUserCacheBean().delete(authorization, MainContext.SYSTEM_ORGI);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}