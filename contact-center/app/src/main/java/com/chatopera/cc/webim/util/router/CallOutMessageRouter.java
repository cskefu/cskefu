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
package com.chatopera.cc.webim.util.router;

import com.chatopera.cc.webim.web.model.MessageOutContent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CallOutMessageRouter implements OutMessageRouter{

	@Bean(name="phone")
	public CallOutMessageRouter initWebIMessageRouter(){
		return new CallOutMessageRouter() ;
	}
	@Override
	public void handler(String touser, String msgtype, String appid,
			MessageOutContent outMessage) {
		//do nothing
		//电话渠道通过TTS播放给电话系统
	}

}
