/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.controller.api;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.persistence.repository.AgentServiceRepository;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.RestResult;
import com.cskefu.cc.util.RestResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 访客留言功能
 */
@RestController
@RequestMapping("/api/leavemsg")
public class ApiLeavemsgController extends Handler {

    @Autowired
    private AgentServiceRepository agentServiceRepository;

    /**
     * 获取留言列表
     *
     * @param request
     * @param username 搜索用户名，精确搜索
     * @return
     */
    @RequestMapping("/list")
    @Menu(type = "apps", subtype = "app", access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request, @RequestBody RequestValues<AgentService> values) {
        Page<AgentService> page = agentServiceRepository.findAll((root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();

            list.add(cb.equal(root.get("leavemsg").as(Boolean.class), true));

            list.add(cb.equal(root.get("leavemsgstatus").as(String.class), MainContext.LeaveMsgStatus.NOTPROCESS.toString()));

            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, PageRequest.of(super.getP(values.getQuery()), super.getPs(values.getQuery()), Sort.Direction.DESC, "createtime"));
        return new ResponseEntity<>(new RestResult(RestResultType.OK, page), HttpStatus.OK);
    }
}