/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

import com.chatopera.cc.model.Chatbot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatbotRepository extends JpaRepository<Chatbot, String> {

    boolean existsByClientIdAndOrgi(String clientId, String orgi);

    boolean existsBySnsAccountIdentifierAndOrgi(String snsid, String orgi);

    List<Chatbot> findByIdAndOrgi(String id, String orgi);

    Chatbot findBySnsAccountIdentifierAndOrgi(String snsid, String orgi);

    @Query(value = "select c from Chatbot c")
    Page<Chatbot> findWithPagination(Pageable pageRequest);

    List<Chatbot> findByOrgi(final String orgi);
}
