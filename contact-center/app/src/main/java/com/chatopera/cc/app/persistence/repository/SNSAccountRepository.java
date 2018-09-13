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

import com.chatopera.cc.app.model.SNSAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public abstract interface SNSAccountRepository
  extends JpaRepository<SNSAccount, String>
{
  public abstract SNSAccount findByIdAndOrgi(String paramString, String orgi);

  public abstract boolean existsBySnsidAndSnstypeAndOrgi(String snsid, String snsType, String orgi);

  public abstract SNSAccount findBySnsid(String snsid);
  
  public abstract SNSAccount findBySnsidAndOrgi(String snsid, String orgi);
  
  public abstract int countByAppkeyAndOrgi(String appkey, String orgi);
  
  public abstract int countBySnsidAndOrgi(String snsid, String orgi);
  
  public abstract List<SNSAccount> findBySnstypeAndOrgi(String paramString , String orgi);
  
  public abstract List<SNSAccount> findBySnstype(String snsType);
  
  public abstract Page<SNSAccount> findBySnstypeAndOrgi(String paramString ,String orgi, Pageable page);

  @Query(value = "select s from SNSAccount s where " +
          "(:orgi is null or s.orgi = :orgi) and " +
          "(:snsType is null or s.snstype = :snsType) and " +
          "(:myorgans is null or s.organ IN :myorgans)")
  public List<SNSAccount> findBySnstypeAndOrgiAndOrgans(@Param("snsType") String snsType, @Param("orgi") String orgi, @Param("myorgans") List<String> organs);
}
