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

import com.chatopera.cc.app.model.Instruction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface InstructionRepository  extends JpaRepository<Instruction, String>
{
  public abstract Instruction findByIdAndOrgi(String id , String orgi);
  
  public abstract Page<Instruction> findByOrgi(String orgi , Pageable paramPageable);
  
  public abstract Instruction findByNameAndOrgi(String name, String orgi);
  
  public abstract List<Instruction> findByOrgi(String orgi);
  
  public abstract List<Instruction> findBySnsidAndOrgi(String snsid, String orgi);
  
  public abstract Page<Instruction> findBySnsidAndOrgi(String snsid, String orgi  , Pageable paramPageable);
  
  public abstract long countByNameAndSnsidAndOrgi(String name , String snsid, String orgi);
  
  public abstract long countByNameAndSnsidAndOrgiAndIdNot(String name , String snsid, String orgi, String id);
}
