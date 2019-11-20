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
package com.chatopera.cc.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "uk_agentservice")
@Proxy(lazy = false)
public class AgentServiceSatis implements Serializable {
	private static final long serialVersionUID = -5052623717164550681L;

	private String orgi;
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

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
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
