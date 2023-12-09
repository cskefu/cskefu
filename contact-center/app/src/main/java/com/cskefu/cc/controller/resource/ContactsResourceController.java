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
package com.cskefu.cc.controller.resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.Contacts;
import com.cskefu.cc.persistence.repository.ContactsRepository;
import com.cskefu.cc.util.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.Arrays;

@Controller
public class ContactsResourceController extends Handler {

    @Autowired
    private ContactsRepository contactsRes;

    @RequestMapping("/res/contacts")
    @Menu(type = "res", subtype = "contacts")
    @ResponseBody
    public String add(ModelMap map, HttpServletRequest request, @Valid String q) {
        if (q == null) {
            q = "";
        }
        Page<Contacts> contactsList = contactsRes.findByCreaterAndSharesInOrSharesIsNullAndDatastatus(super.getUser(request).getId(), Arrays.asList(super.getUser(request).getId(),"all"),false, PageRequest.of(0, 10));

        JSONArray result = new JSONArray();
        for (Contacts contact : contactsList.getContent()) {
            JSONObject item = new JSONObject();
            item.put("id", contact.getId());
            item.put("text", contact.getName());
            result.add(item);
        }

        return result.toJSONString();
    }
}