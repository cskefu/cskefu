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
package com.chatopera.cc.webim.util.disruptor.multiupdate;

import com.lmax.disruptor.EventHandler;
import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.event.MultiUpdateEvent;
import com.chatopera.cc.util.event.UserDataEvent;
import com.chatopera.cc.util.task.ESData;

@SuppressWarnings("rawtypes")
public class MultiUpdateEventHandler implements EventHandler<UserDataEvent>{

	@Override
	public void onEvent(UserDataEvent event, long arg1, boolean arg2)
			throws Exception {
		if(event.getEvent()!=null){
			MultiUpdateEvent multiEventEvent = (MultiUpdateEvent)event.getEvent() ;
			if(multiEventEvent.getData() instanceof ESData){
				//只存储到 ES，不存储到数据库。目前只有名单数据 
			}else{
				if(UKDataContext.MultiUpdateType.SAVE.toString().equals(multiEventEvent.getEventype())){
					multiEventEvent.getCrudRes().delete(multiEventEvent.getData()) ;
					multiEventEvent.getCrudRes().save(multiEventEvent.getData()) ;
				}else if(UKDataContext.MultiUpdateType.DELETE.toString().equals(multiEventEvent.getEventype())){
					multiEventEvent.getCrudRes().delete(multiEventEvent.getData()) ;
				}
			}
		}
	}

}
