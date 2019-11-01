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
package com.chatopera.cc.persistence.repository;

import java.util.List;

import com.chatopera.cc.model.AdType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdTypeRepository extends JpaRepository<AdType, String> {
	
	AdType findByIdAndOrgi(String id, String orgi);

	int countByNameAndOrgi(String name, String orgi);
	
	List<AdType> findByOrgi(String orgi);
	
	List<AdType> findByAdposAndOrgi(String adpos, String orgi);
}