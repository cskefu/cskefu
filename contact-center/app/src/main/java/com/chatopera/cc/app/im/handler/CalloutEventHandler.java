/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.app.im.handler;

import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.im.client.NettyClients;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

public class CalloutEventHandler
{
	protected SocketIOServer server;
	private final Logger logger = LoggerFactory.getLogger(CalloutEventHandler.class);


    @Autowired
    public CalloutEventHandler(SocketIOServer server)
    {  
        this.server = server ;
    }  


    @OnConnect
    public void onConnect(SocketIOClient client)  
    {
        String user = client.getHandshakeData().getSingleUrlParam("userid") ;
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
        String session = client.getHandshakeData().getSingleUrlParam("session") ;
        String admin = client.getHandshakeData().getSingleUrlParam("admin") ;
        logger.info("onConnect userid {}, orgi {}.", user, orgi);

        if(StringUtils.isNotBlank(user) && StringUtils.isNotBlank(user)){
            client.set("agentno", user);
            InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress()  ;
            String ip = MainUtils.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString()) ;

            NettyClients.getInstance().putCalloutEventClient(user, client);
        }
    }  
      
    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息  
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {
        String user = client.getHandshakeData().getSingleUrlParam("userid") ;
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
        String session = client.getHandshakeData().getSingleUrlParam("session") ;
        String admin = client.getHandshakeData().getSingleUrlParam("admin") ;
		logger.info("onDisconnect userid {}, orgi {}", user, orgi);
        NettyClients.getInstance().removeCalloutEventClient(user, MainUtils.getContextID(client.getSessionId().toString()));

    }
}