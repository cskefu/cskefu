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
package com.chatopera.cc.webim.web.handler.admin.system;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.webim.service.cache.CacheHelper;
import com.chatopera.cc.webim.web.handler.Handler;

@Controller
@RequestMapping("/admin/monitor")
public class HazelcastMonitorController extends Handler{
	
    @RequestMapping("/hazelcast")
    @Menu(type = "admin" , subtype = "metadata" , admin = true)
    public ModelAndView index(ModelMap map , HttpServletRequest request , HttpServletResponse response) throws SQLException {
    	Map<String , Object> jsonObjectMap = new HashMap< String , Object>();
    	
    	jsonObjectMap.put("agentUser" , convert(CacheHelper.getAgentUserCacheBean().getStatics())) ;
    	jsonObjectMap.put("agentStatus" , convert(CacheHelper.getAgentStatusCacheBean().getStatics())) ;
    	
    	jsonObjectMap.put("apiUser" , convert(CacheHelper.getApiUserCacheBean().getStatics())) ;
    	jsonObjectMap.put("imrCache" , convert(CacheHelper.getIMRCacheBean().getStatics())) ;
    	jsonObjectMap.put("onlineUser" , convert(CacheHelper.getOnlineUserCacheBean().getStatics())) ;
    	jsonObjectMap.put("systemCache" , convert(CacheHelper.getSystemCacheBean().getStatics())) ;
    	
    	map.addAttribute("systemStatics", new Gson().toJson(jsonObjectMap)) ;
    	
    	
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("application/json; charset=utf-8");
        return request(super.createRequestPageTempletResponse("/admin/system/monitor/hazelcast"));
    }
    /**
     * 转换统计数据
     * @param json
     * @return
     */
    private Map<String , Object> convert(JsonObject json){
    	Map<String , Object> values = new HashMap<String , Object>();
    	for(String key : json.names()){
    		values.put(key, json.get(key)) ;
    	}
    	return values;
    }
}