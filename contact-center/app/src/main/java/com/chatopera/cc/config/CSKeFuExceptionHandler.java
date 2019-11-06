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
package com.chatopera.cc.config;

import com.lmax.disruptor.ExceptionHandler;

public class CSKeFuExceptionHandler implements ExceptionHandler<Object>{

	@Override
	public void handleEventException(Throwable ex, long arg1, Object arg2) {
		ex.printStackTrace();
	}

	@Override
	public void handleOnShutdownException(Throwable ex) {
		
	}

	@Override
	public void handleOnStartException(Throwable ex) {
		// TODO Auto-generated method stub
		
	}

}
