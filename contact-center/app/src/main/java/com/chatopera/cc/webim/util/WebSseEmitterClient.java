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
package com.chatopera.cc.webim.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.chatopera.cc.util.webim.WebIMClient;

public class WebSseEmitterClient{
	
	private ConcurrentMap<String, WebIMClient> imClientsMap = new MapMaker().weakValues().makeMap();
	
	public List<WebIMClient> getClients(String userid){
		Collection<WebIMClient> values = imClientsMap.values() ;
		List<WebIMClient> clents = new ArrayList<WebIMClient>();
		for(WebIMClient client : values){
			if(client.getUserid().equals(userid)){
				clents.add(client) ;
			}
		}
		return clents;
	}
	
	public int size(){
		return imClientsMap.size() ;
	}
	
	public void putClient(String userid , WebIMClient client){
		imClientsMap.put(client.getClient(), client) ;
	}
	
	public void removeClient(String userid , String client , boolean timeout) throws Exception{
		List<WebIMClient> keyClients = this.getClients(userid) ;
		for(int i=0 ; i<keyClients.size() ; i++){
			WebIMClient webIMClient = keyClients.get(i) ;
			if(webIMClient.getClient()!=null && webIMClient.getClient().equals(client)){
				imClientsMap.remove(client) ;
				keyClients.remove(i) ;
				break ;
			}
		}
		if(keyClients.size() == 0 && timeout == true){
			OnlineUserUtils.offline(userid, userid);
		}
	}
}
