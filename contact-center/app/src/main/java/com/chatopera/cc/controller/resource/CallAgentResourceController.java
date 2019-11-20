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
package com.chatopera.cc.controller.resource;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.UserRepository;
import com.chatopera.cc.util.CallCenterUtils;
import com.chatopera.cc.util.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CallAgentResourceController extends Handler {
	
	@Autowired
	private UserRepository userRes ;
	
	@RequestMapping("/res/agent")
    @Menu(type = "res" , subtype = "agent")
    public ModelAndView add(ModelMap map , HttpServletRequest request , @Valid String q) {
		if(q==null){
			q = "" ;
		}
		final String search = q;
		final String orgi = super.getOrgi(request);
		final List<String> organList = CallCenterUtils.getExistOrgan(super.getUser(request));
		map.put("owneruserList", userRes.findAll(new Specification<User>(){
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();  
				In<Object> in = cb.in(root.get("organ"));
				
				list.add(cb.equal(root.get("orgi").as(String.class),orgi ));
				
				list.add(cb.or(cb.like(root.get("username").as(String.class),"%"+search+"%" ),cb.like(root.get("uname").as(String.class),"%"+search+"%" )));
				
				if(organList.size() > 0){
					
					for(String id : organList){
						in.value(id) ;
					}
				}else{
					in.value(Constants.CSKEFU_SYSTEM_NO_DAT) ;
				}
				list.add(in) ;
				
				Predicate[] p = new Predicate[list.size()];  
				return cb.and(list.toArray(p));   
			}}));
		return request(super.createRequestPageTempletResponse("/public/agent"));
    }
}