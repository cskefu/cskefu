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
package com.chatopera.cc.controller.api;

import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.QuickType;
import com.chatopera.cc.persistence.es.QuickReplyRepository;
import com.chatopera.cc.persistence.repository.QuickTypeRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import com.chatopera.cc.util.RestResultType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 快捷回复分类服务
 * 快捷回复分类管理功能
 */
@RestController
@RequestMapping("/api/quicktype")
@RequiredArgsConstructor
public class ApiQuickTypeController extends Handler {

    @NonNull
    private final QuickTypeRepository quickTypeRepository;

    @NonNull
    private final QuickReplyRepository quickReplyRepository;

    /**
     * 返回快捷回复分类列表
     *
     * @param quicktype 搜索pub,pri
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "quicktype", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @Valid String id, @Valid String quicktype) {
        if (StringUtils.isNotBlank(id)) {
            return new ResponseEntity<>(new RestResult(RestResultType.OK, quickTypeRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Quick type %s not found", id)))), HttpStatus.OK);
        }
        List<QuickType> quickTypeList = quickTypeRepository.findByOrgiAndQuicktype(getOrgi(request), quicktype);
        return new ResponseEntity<>(new RestResult(RestResultType.OK, quickTypeList), HttpStatus.OK);
    }

    /**
     * 新增或修改快捷回复分类
     */
    @RequestMapping(method = RequestMethod.PUT)
    @Menu(type = "apps", subtype = "quicktype", access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request, @Valid QuickType quickType) {
        if (quickType != null && !StringUtils.isBlank(quickType.getName())) {
            quickType.setOrgi(getOrgi(request));
            quickType.setCreater(getUser(request).getId());
            quickType.setCreatetime(new Date());
            if (StringUtils.isNotBlank(quickType.getId())) {
                quickType.setUpdatetime(new Date());
            }
            quickType = quickTypeRepository.save(quickType);
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK, quickType), HttpStatus.OK);
    }

    /**
     * 删除分类，并且删除分类下的快捷回复
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @Menu(type = "apps", subtype = "reply", access = true)
    public ResponseEntity<RestResult> delete(@Valid String id) {
        RestResult result = new RestResult(RestResultType.OK);
        if (!StringUtils.isBlank(id)) {
            quickTypeRepository.findById(id).ifPresent(quickType -> {
                quickReplyRepository.deleteByCate(quickType.getId(), quickType.getOrgi());
                quickTypeRepository.delete(quickType);
            });
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
