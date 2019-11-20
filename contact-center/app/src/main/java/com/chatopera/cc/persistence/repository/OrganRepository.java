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
package com.chatopera.cc.persistence.repository;

import com.chatopera.cc.model.Organ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganRepository
  extends JpaRepository<Organ, String>
{
  Organ findByIdAndOrgi(String paramString, String orgi);
  
  Page<Organ> findByOrgi(String orgi, Pageable paramPageable);

  Page<Organ> findByOrgiAndOrgid(String orgi, String orgid, Pageable paramPageable);
  
  Organ findByNameAndOrgi(String paramString, String orgi);
  
  Organ findByNameAndOrgiAndOrgid(String paramString, String orgi, String orgid);

  Organ findByParentAndOrgi(String parent, String orgi);

  List<Organ> findByOrgiAndParent(String orgi, String parent);

  List<Organ> findByOrgi(String orgi);
  
  List<Organ> findByOrgiAndOrgid(String orgi, String orgid);
  
  List<Organ> findByOrgiAndSkill(String orgi, boolean skill);
  
  List<Organ> findByOrgiAndSkillAndOrgid(String orgi, boolean skill, String orgid);

  List<Organ> findByIdInAndSkill(List<String> organIdList, boolean b);
  
}
