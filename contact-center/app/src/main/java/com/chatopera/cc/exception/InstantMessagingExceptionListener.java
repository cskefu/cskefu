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
package com.chatopera.cc.exception;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class InstantMessagingExceptionListener extends ExceptionListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(InstantMessagingExceptionListener.class);

    @Override
    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
    	if(e instanceof IOException){
    		logger.info(e.getMessage());
    	}else{
    		logger.error(e.getMessage(), e);
    	}
    	client.disconnect();
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
    	if(e instanceof IOException){
    		logger.info(e.getMessage());
    	}else{
    		logger.error(e.getMessage(), e);
    	}
    	client.disconnect();
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
    	if(e instanceof IOException){
    		logger.info(e.getMessage());
    	}else{
    		logger.error(e.getMessage(), e);
    	}
    	client.disconnect();
    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
    	if(e instanceof IOException){
    		logger.info(e.getMessage());
    	}else{
    		logger.error(e.getMessage(), e);
    	}
    	ctx.close();
        return true;
    }
}
