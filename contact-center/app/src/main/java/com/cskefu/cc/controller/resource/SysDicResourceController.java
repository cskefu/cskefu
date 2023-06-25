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

import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.Dict;
import com.cskefu.cc.model.SysDic;
import com.cskefu.cc.util.Menu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/res")
public class SysDicResourceController extends Handler{
	
    @RequestMapping("/dic")
    @Menu(type = "resouce" , subtype = "dic" , access = true)
    public ModelAndView index(ModelMap map , HttpServletResponse response, @Valid String id, @Valid String name, @Valid String attr , @Valid String style) throws IOException {
    	List<SysDic> itemList = new ArrayList<>() ;
    	SysDic sysDic = Dict.getInstance().getDicItem(id) ;
    	if(sysDic!=null){
	    	SysDic dic = Dict.getInstance().getDicItem(sysDic.getDicid()) ;
			List<SysDic> sysDicList = Dict.getInstance().getSysDic(dic.getCode());
	    	for(SysDic item : sysDicList){
	    		if(item.getParentid().equals(id)){
	    			itemList.add(item) ;
	    		}
	    	}
    	}
    	map.addAttribute("sysDicList", itemList) ;
    	map.addAttribute("name", name) ;
    	map.addAttribute("attr", attr) ;
    	map.addAttribute("style", style) ;
    	return request(super.createView("/public/select"));
    }
    
}