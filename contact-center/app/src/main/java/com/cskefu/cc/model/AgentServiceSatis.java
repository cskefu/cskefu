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
package com.cskefu.cc.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "uk_agentservice")
@Proxy(lazy = false)
public class AgentServiceSatis implements Serializable {
	private static final long serialVersionUID = -5052623717164550681L;
	private String id;
	private boolean satisfaction ;
	private Date satistime ;
	private String satislevel ;
	private String satiscomment ;
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return this.id;
	}

	public boolean isSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(boolean satisfaction) {
		this.satisfaction = satisfaction;
	}

	public Date getSatistime() {
		return satistime;
	}

	public void setSatistime(Date satistime) {
		this.satistime = satistime;
	}

	public String getSatislevel() {
		return satislevel;
	}

	public void setSatislevel(String satislevel) {
		this.satislevel = satislevel;
	}

	public String getSatiscomment() {
		return satiscomment;
	}

	public void setSatiscomment(String satiscomment) {
		this.satiscomment = satiscomment;
	}

	public void setId(String id) {
		this.id = id;
	}
}
