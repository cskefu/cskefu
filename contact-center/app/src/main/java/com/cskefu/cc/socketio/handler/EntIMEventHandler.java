/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.socketio.handler;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.IMGroupUser;
import com.cskefu.cc.model.User;
import com.cskefu.cc.peer.PeerSyncEntIM;
import com.cskefu.cc.persistence.repository.IMGroupUserRepository;
import com.cskefu.cc.socketio.client.NettyClients;
import com.cskefu.cc.socketio.message.ChatMessage;
import com.cskefu.cc.socketio.message.Message;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EntIMEventHandler     
{  
	protected SocketIOServer server;
	
    @Autowired  
    public EntIMEventHandler(SocketIOServer server)   
    {  
        this.server = server ;
    }

    @Autowired
    private PeerSyncEntIM peerSyncEntIM;

    public PeerSyncEntIM getPeerSyncEntIM() {
        if (peerSyncEntIM == null) {
            peerSyncEntIM = MainContext.getContext().getBean(PeerSyncEntIM.class);
        }

        return peerSyncEntIM;
    }

    @OnConnect
    public void onConnect(SocketIOClient client)
    {
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String name = client.getHandshakeData().getSingleUrlParam("name") ;
			String group = client.getHandshakeData().getSingleUrlParam("group") ;
			String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
			if(!StringUtils.isBlank(group)){
				client.joinRoom(group);
			}else{
				if(NettyClients.getInstance().getEntIMClientsNum(user) == 0){
					Message outMessage = new Message() ;
			    	outMessage.setMessage("online");
			    	outMessage.setContextid(user);
			    	outMessage.setMessageType(MainContext.MessageType.MESSAGE.toString());
			    	outMessage.setCalltype(MainContext.CallType.IN.toString());
					client.getNamespace().getBroadcastOperations().sendEvent("status", outMessage); //广播所有人，用户上线
				}
				if(!StringUtils.isBlank(user)){
					NettyClients.getInstance().putEntIMEventClient(user, client);
				}
				IMGroupUserRepository imGroupUserRes = MainContext.getContext().getBean(IMGroupUserRepository.class) ;
				User imUser = new User(user);
				List<IMGroupUser> imGroupUserList = imGroupUserRes.findByUserAndOrgi(imUser , orgi) ;
				for(IMGroupUser imGroupUser : imGroupUserList){
					if(imGroupUser.getImgroup()!=null){
						client.joinRoom(imGroupUser.getImgroup().getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
      
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {  
    	try {
			String user = client.getHandshakeData().getSingleUrlParam("userid") ;
			String name = client.getHandshakeData().getSingleUrlParam("name") ;
			String group = client.getHandshakeData().getSingleUrlParam("group") ;
			String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
			if(!StringUtils.isBlank(group)){
				client.leaveRoom(group);
			}else{
				if(!StringUtils.isBlank(user)){
					NettyClients.getInstance().removeEntIMEventClient(user, client.getSessionId().toString());
				}
				if(NettyClients.getInstance().getEntIMClientsNum(user) == 0){
					Message outMessage = new Message() ;
			    	outMessage.setMessage("offline");
			    	outMessage.setContextid(user);
			    	outMessage.setMessageType(MainContext.MessageType.MESSAGE.toString());
			    	outMessage.setCalltype(MainContext.CallType.IN.toString());
					client.getNamespace().getBroadcastOperations().sendEvent("status", outMessage); //广播所有人，用户上线
				}
				
				IMGroupUserRepository imGroupUserRes = MainContext.getContext().getBean(IMGroupUserRepository.class) ;
				User imUser = new User(user);
				List<IMGroupUser> imGroupUserList = imGroupUserRes.findByUserAndOrgi(imUser , orgi) ;
				for(IMGroupUser imGroupUser : imGroupUserList){
					if(imGroupUser.getImgroup()!=null){
						client.leaveRoom(imGroupUser.getImgroup().getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
    
    @OnEvent(value = "message")  
    public void onEvent(SocketIOClient client, AckRequest request, ChatMessage data)
    {
    	if(data.getType() == null){
    		data.setType("message");
    	}
    	String user = client.getHandshakeData().getSingleUrlParam("userid") ;
//		String name = client.getHandshakeData().getSingleUrlParam("name") ;
        String group = client.getHandshakeData().getSingleUrlParam("group");
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi");


        getPeerSyncEntIM().send(user, group, orgi, MainContext.MessageType.MESSAGE, data);
    }
}  