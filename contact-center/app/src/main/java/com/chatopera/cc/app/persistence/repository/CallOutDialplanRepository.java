/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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

import com.chatopera.cc.app.model.CallOutDialplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CallOutDialplanRepository extends JpaRepository<CallOutDialplan, String> {
    public abstract boolean existsById(String id);

    /**
     * Updating Entities with Update Query in Spring Data JPA
     * https://codingexplained.com/coding/java/spring-framework/updating-entities-with-update-query-spring-data-jpa
     * @param id
     * @return
     */
    @Query("UPDATE CallOutDialplan t set t.executed = t.executed + 1 WHERE t.id = :id")
    int increExecuted(@Param("id") String id);

    public abstract List<CallOutDialplan> findByStatusAndIsarchive(String status, boolean isarchive);

    public abstract Page<CallOutDialplan> findAllByIsarchiveNot(boolean isarchive, Pageable pageRequest);

    public abstract Page<CallOutDialplan> findByIsarchive(boolean isarchive, Pageable pageRequest);
}
