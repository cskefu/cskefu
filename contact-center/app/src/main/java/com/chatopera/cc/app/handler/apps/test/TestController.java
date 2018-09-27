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
package com.chatopera.cc.app.handler.apps.test;

import javax.servlet.http.HttpServletRequest;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.OnlineUserUtils;
import com.chatopera.cc.app.handler.Handler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController extends Handler {
	

	@RequestMapping({"/test/demo"})
	@Menu(type="apps", subtype="test" , access=false , admin = true)
	public ModelAndView content(ModelMap map , HttpServletRequest request){
		for(int i=0 ; i<500; i++){
			String user = MainUtils.getUUID();
			try {
				OnlineUserUtils.newRequestMessage(user, "ukewo", "user", "system", "localhost" , "win10", "test" , MainContext.ChannelTypeEnum.WEBIM.toString() , null , null , "admin" , "标题" , "https://www.chatopera.com" , "12434" , MainContext.ChatInitiatorType.USER.toString()) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return request(super.createAppsTempletResponse("/public/success"));
	}
}
