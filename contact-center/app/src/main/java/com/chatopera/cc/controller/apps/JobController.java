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

package com.chatopera.cc.controller.apps;

import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.JobDetail;
import com.chatopera.cc.model.JobTask;
import com.chatopera.cc.persistence.repository.JobDetailRepository;
import com.chatopera.cc.util.Menu;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.Date;


@Controller
@RequestMapping("/apps/job")
public class JobController extends Handler {
	
	@Autowired
	private JobDetailRepository jobRes ;
	/**
	 * 跳转到修改执行时间的页面
	 * @param request
	 * @param orgi
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({"/setting"})
	@Menu(type="job", subtype="setting")
	public ModelAndView setting(ModelMap map , HttpServletRequest request , @Valid String id) throws Exception {
		JobDetail detail = jobRes.findByIdAndOrgi(id, super.getOrgi(request)) ;
		if(detail.getTaskinfo() != null && detail.getTaskinfo() != ""){
			ObjectMapper objectMapper = new ObjectMapper();  
			JobTask taskInfo = objectMapper.readValue(detail.getTaskinfo(), JobTask.class) ;
			map.put("taskinfo",taskInfo);
		}
		map.put("job", detail);
		return request(super.createRequestPageTempletResponse("/apps/business/job/setting"));
	}

	@RequestMapping({"/save"})
	@Menu(type="job", subtype="save")
	public ModelAndView save(HttpServletRequest request,
			@Valid JobTask taskinfo,@Valid Boolean plantask, @Valid String id) throws ParseException {
		JobDetail detail = jobRes.findByIdAndOrgi(id, super.getOrgi(request)) ;
		
		if(detail != null ){
			try {
				detail.setPlantask(plantask) ;
				ObjectMapper mapper = new ObjectMapper();  
				detail.setTaskinfo(mapper.writeValueAsString(taskinfo));
				
				detail.setCronexp(MainUtils.convertCrond(taskinfo));
				/**
				 * 设定触发时间
				 */
				detail.setNextfiretime(new Date());
				detail.setNextfiretime(MainUtils.updateTaskNextFireTime(detail));
			} catch (Exception e) {
				e.printStackTrace();
			}
			jobRes.save(detail) ;
		}
		return request(super.createRequestPageTempletResponse("/public/success"));
	}
}
