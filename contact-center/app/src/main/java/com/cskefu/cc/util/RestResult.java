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
package com.cskefu.cc.util;

public class RestResult implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 783408637220961119L;
	private RestResultType status ;
	
	public RestResult(RestResultType status , Object data){
		this.status = status ;
		this.data = data ;
	}
	
	public RestResult(RestResultType status){
		this.status = status ;
	}
	
	public Object data ;

	public RestResultType getStatus() {
		return status;
	}

	public void setStatus(RestResultType status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	
}
