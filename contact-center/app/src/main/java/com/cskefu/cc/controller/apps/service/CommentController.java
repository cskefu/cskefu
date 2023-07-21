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
package com.cskefu.cc.controller.apps.service;

import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.persistence.repository.AgentServiceRepository;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.util.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/service")
public class CommentController extends Handler {
    @Autowired
    private AgentServiceRepository agentServiceRes;

    @Autowired
    private OrganProxy organProxy;

    @RequestMapping("/comment/index")
    @Menu(type = "service", subtype = "comment", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, String userid, String agentservice, @Valid String channel) {
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        Page<AgentService> agentServiceList = agentServiceRes.findBySatisfactionAndSkillIn(true, organs.keySet(), PageRequest.of(super.getP(request), super.getPs(request)));
        map.addAttribute("serviceList", agentServiceList);
        return request(super.createView("/apps/service/comment/index"));
    }
}
