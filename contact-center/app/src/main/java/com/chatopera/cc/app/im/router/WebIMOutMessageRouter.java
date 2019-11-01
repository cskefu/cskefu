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
package com.chatopera.cc.app.im.router;

import com.chatopera.cc.app.im.client.NettyClients;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.chatopera.cc.app.model.MessageOutContent;

@Component
public class WebIMOutMessageRouter implements OutMessageRouter{

	@Bean(name="webim")
	public WebIMOutMessageRouter initWebIMessageRouter(){
		return new WebIMOutMessageRouter() ;
	}
	@Override
	public void handler(String touser, String msgtype, String appid,
			MessageOutContent outMessage) {
		NettyClients.getInstance().publishIMEventMessage(touser, msgtype, outMessage);
	}

}
