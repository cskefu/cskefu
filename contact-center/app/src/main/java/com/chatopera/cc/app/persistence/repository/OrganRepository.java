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
package com.chatopera.cc.app.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chatopera.cc.app.model.Organ;

public abstract interface OrganRepository
  extends JpaRepository<Organ, String>
{
  public abstract Organ findByIdAndOrgi(String paramString, String orgi);
  
  public abstract Page<Organ> findByOrgi(String orgi , Pageable paramPageable);

  public abstract Page<Organ> findByOrgiAndOrgid(String orgi ,String orgid, Pageable paramPageable);
  
  public abstract Organ findByNameAndOrgi(String paramString, String orgi);
  
  public abstract Organ findByNameAndOrgiAndOrgid(String paramString, String orgi,String orgid);
  
  
  public abstract List<Organ> findByOrgi(String orgi);
  
  public abstract List<Organ> findByOrgiAndOrgid(String orgi ,String orgid);
  
  public abstract List<Organ> findByOrgiAndSkill(String orgi , boolean skill);
  
  public abstract List<Organ> findByOrgiAndSkillAndOrgid(String orgi , boolean skill,String orgid);

  public abstract List<Organ> findByIdInAndSkill(List<String> organIdList, boolean b);
  
  public abstract List<Organ> findByOrgiAndParent(String orgi, String parent);
  
}
