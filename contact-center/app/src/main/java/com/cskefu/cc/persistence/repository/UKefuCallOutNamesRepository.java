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
package com.cskefu.cc.persistence.repository;

import com.cskefu.cc.model.UKefuCallOutNames;
import com.cskefu.cc.model.UKefuCallOutTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UKefuCallOutNamesRepository extends JpaRepository<UKefuCallOutNames, String> {
	
	UKefuCallOutNames findByIdAndOrgi(String id, String orgi);
	
	List<UKefuCallOutNames> findByNameAndOrgi(String name, String orgi);

	Page<UKefuCallOutNames> findByActidAndOrgi(String actid, String orgi, Pageable page) ;
	
	List<UKefuCallOutNames> findByActidAndOrgi(String actid, String orgi) ;
	
	List<UKefuCallOutNames> findByDataidAndOrgi(String dataid, String orgi) ;
	
	List<UKefuCallOutNames> findByDataidAndCreaterAndOrgi(String dataid, String creater, String orgi) ;
	
	Page<UKefuCallOutNames> findAll(Specification<UKefuCallOutTask> spec, Pageable pageable);
	
	Page<UKefuCallOutNames> findByCreaterAndOrgi(String creater, String orgi, Pageable page) ;
	
	Page<UKefuCallOutNames> findByOrganAndOrgi(String organ, String orgi, Pageable page) ;
	
	Page<UKefuCallOutNames> findByOrgi(String orgi, Pageable page) ;
	
	Page<UKefuCallOutNames> findByOwneruserAndOrgi(String owneruser, String orgi, Pageable page) ;
	
}
