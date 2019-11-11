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
import com.chatopera.cc.model.QuickReply;
import com.chatopera.cc.persistence.es.QuickReplyRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import com.chatopera.cc.util.RestResultType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 快捷回复服务
 * 快捷回复管理功能
 */
@RestController
@RequestMapping("/api/quickreply")
public class ApiQuickReplyController extends Handler {

    @Autowired
    private QuickReplyRepository quickReplyRepository;

    /**
     * 返回快捷回复列表，cate为分类id，通过/api/quicktype 获取分类id，支持分页，分页参数为 p=1&ps=50，默认分页尺寸为 20条每页
     * @param request
     * @param cate	搜索分类id，精确搜索，通过/api/quicktype 获取分类id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "quickreply", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, String id, @Valid String cate, @Valid String q, Integer p, Integer ps) {
        if (StringUtils.isNotBlank(id)) {
            return new ResponseEntity<>(new RestResult(RestResultType.OK, quickReplyRepository.findOne(id)), HttpStatus.OK);
        }

        Page<QuickReply> replyList = quickReplyRepository.getByOrgiAndCate(getOrgi(request), cate, q,
                new PageRequest(p == null ? 1 : p, ps == null ? 20 : ps));
        return new ResponseEntity<>(new RestResult(RestResultType.OK, replyList), HttpStatus.OK);
    }

    /**
     * 新增或修改快捷回复
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @Menu(type = "apps", subtype = "quickreply", access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request, @Valid QuickReply quickReply) {
        if (quickReply != null && !StringUtils.isBlank(quickReply.getTitle())) {
            quickReply.setOrgi(getOrgi(request));
            quickReply.setCreater(getUser(request).getId());
            quickReplyRepository.save(quickReply);
        }
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }

    /**
     * 删除用户，只提供 按照用户ID删除
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @Menu(type = "apps", subtype = "quickreply", access = true)
    public ResponseEntity<RestResult> delete(HttpServletRequest request, @Valid String id) {
        RestResult result = new RestResult(RestResultType.OK);
        if (!StringUtils.isBlank(id)) {
            QuickReply reply = quickReplyRepository.findOne(id);
            if (reply != null) {
                quickReplyRepository.delete(reply);
            } else {
                return new ResponseEntity<>(new RestResult(RestResultType.ORGAN_DELETE), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
