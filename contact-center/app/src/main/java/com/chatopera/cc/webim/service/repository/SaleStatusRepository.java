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
package com.chatopera.cc.webim.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatopera.cc.webim.web.model.SaleStatus;

public abstract interface SaleStatusRepository extends JpaRepository<SaleStatus, String> {
	
	public abstract SaleStatus findByIdAndOrgi(String id, String orgi);

	public abstract List<SaleStatus> findByOrgi(String cate) ;
	
	public abstract List<SaleStatus> findByOrgiAndCate(String orgi, String cate) ;
	
	public abstract List<SaleStatus> findByOrgiAndActivityid(String orgi, String activityid) ;
	
	public abstract List<SaleStatus> findByOrgiAndCateAndActivityid(String orgi, String cate, String activityid) ;
	
	public abstract SaleStatus findByOrgiAndName(String orgi, String name) ;

}
