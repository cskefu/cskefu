/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.cskefu.cc.util.asr;

public class AsrResult implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 540873644154169045L;
	private String message ;
	private String id ;
	private String num ;
	
	private int speakms ;
	
	private String recordpath ;
	
	public AsrResult(String id , String message , String num) {
		this.id = id ;
		this.message = message ; 
		this.num = num ;
		
		this.recordpath = id+"_"+num+".wav";
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}

	public String getRecordpath() {
		return recordpath;
	}

	public void setRecordpath(String recordpath) {
		this.recordpath = recordpath;
	}

	public int getSpeakms() {
		return speakms;
	}

	public void setSpeakms(int speakms) {
		this.speakms = speakms;
	}
	
}
