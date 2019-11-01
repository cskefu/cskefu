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
package com.chatopera.cc.app.schedule;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.CallOutUtils;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.util.es.SearchTools;
import com.chatopera.cc.util.es.UKDataBean;
import com.chatopera.cc.util.freeswitch.model.CallCenterAgent;
import com.chatopera.cc.app.cache.CacheHelper;
import org.springframework.data.domain.Page;

public class NamesTask implements Runnable{
	
	private CallCenterAgent agent ;
	public NamesTask(CallCenterAgent agent) {
		this.agent = agent ;
	}
	@Override
	public void run() {
		if(agent!=null) {
			/**
			 * 更新状态
			 */
			agent.setWorkstatus(MainContext.WorkStatusEnum.PREVIEW.toString());
			/**
			 * 根据策略拉取名单 ，
			 * 1、拨打时间
			 * 2、允许或禁止拨打
			 * 3、优先拨打新名单/老名单/预约名单/未拨打成功的名单
			 */
			Page<UKDataBean> names = SearchTools.agentapsearch(this.agent.getOrgi(), agent.getUserid(), 0, 1) ;
			if(names.getTotalElements() == 0) {
				names = SearchTools.agentsearch(this.agent.getOrgi(), true, agent.getUserid() , 0, 1) ;
			}
			/**
			 * 找到名单，生成拨打任务，工作界面上，坐席只能看到自己的名单
			 */
			if(names!=null && names.getContent().size() > 0) {
				UKDataBean name = names.getContent().get(0) ;
				
				CallOutUtils.processNames(name, agent, agent.getOrgi(), (int)(names.getTotalElements() - 1)) ;
			}else {
				agent.setWorkstatus(MainContext.WorkStatusEnum.IDLE.toString());
				NettyClients.getInstance().sendCallCenterMessage(agent.getExtno(), "error", "nonames");
				
				NettyClients.getInstance().sendCallCenterMessage(agent.getExtno(), "docallout", agent);
			}
			
			CacheHelper.getCallCenterAgentCacheBean().put(agent.getUserid(), agent, agent.getOrgi());
		}
	}

}
