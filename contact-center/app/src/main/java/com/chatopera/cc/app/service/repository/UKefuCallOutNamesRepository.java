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
package com.chatopera.cc.app.service.repository;

import java.util.List;

import com.chatopera.cc.app.model.UKefuCallOutNames;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chatopera.cc.app.model.UKefuCallOutTask;

public abstract interface UKefuCallOutNamesRepository extends JpaRepository<UKefuCallOutNames, String> {
	
	public abstract UKefuCallOutNames findByIdAndOrgi(String id, String orgi);
	
	public abstract List<UKefuCallOutNames> findByNameAndOrgi(String name, String orgi);

	public abstract Page<UKefuCallOutNames> findByActidAndOrgi(String actid , String orgi , Pageable page) ;
	
	public abstract List<UKefuCallOutNames> findByActidAndOrgi(String actid , String orgi) ;
	
	public abstract List<UKefuCallOutNames> findByDataidAndOrgi(String dataid , String orgi) ;
	
	public abstract List<UKefuCallOutNames> findByDataidAndCreaterAndOrgi(String dataid , String creater, String orgi) ;
	
	public abstract Page<UKefuCallOutNames> findAll(Specification<UKefuCallOutTask> spec, Pageable pageable);
	
	public abstract Page<UKefuCallOutNames> findByCreaterAndOrgi(String creater , String orgi , Pageable page) ;
	
	public abstract Page<UKefuCallOutNames> findByOrganAndOrgi(String organ , String orgi , Pageable page) ;
	
	public abstract Page<UKefuCallOutNames> findByOrgi(String orgi , Pageable page) ;
	
	public abstract Page<UKefuCallOutNames> findByOwneruserAndOrgi(String owneruser , String orgi , Pageable page) ;
	
}
