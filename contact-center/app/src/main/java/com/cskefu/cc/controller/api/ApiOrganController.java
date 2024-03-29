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

import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.persistence.repository.OrganRepository;
import com.cskefu.cc.util.Menu;
import com.cskefu.cc.util.RestResult;
import com.cskefu.cc.util.RestResultType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 组织机构/部门/技能组功能
 */
@RestController
@RequestMapping("/api/organ")
public class ApiOrganController extends Handler{

	@Autowired
	private OrganRepository organRepository;
	
	/**
	 * 返回所有部门
	 * @param request
	 * @param username	搜索用户名，精确搜索
	 * @return
	 */
	@RequestMapping( method = RequestMethod.GET)
	@Menu(type = "apps" , subtype = "organ" , access = true)
    public ResponseEntity<RestResult> list(HttpServletRequest request) {
        return new ResponseEntity<>(new RestResult(RestResultType.OK, organRepository.findAll()), HttpStatus.OK);
    }
	
	/**
	 * 新增或修改部门
	 * @param request
	 * @param user
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	@Menu(type = "apps" , subtype = "organ" , access = true)
    public ResponseEntity<RestResult> put(HttpServletRequest request , @Valid Organ organ) {
    	if(organ != null && !StringUtils.isBlank(organ.getName())){
    		organRepository.save(organ) ;
    	}
        return new ResponseEntity<>(new RestResult(RestResultType.OK), HttpStatus.OK);
    }
	
	/**
	 * 删除用户，只提供 按照用户ID删除 ， 并且，不能删除系统管理员
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@Menu(type = "apps" , subtype = "user" , access = true)
    public ResponseEntity<RestResult> delete(HttpServletRequest request , @Valid String id) {
		RestResult result = new RestResult(RestResultType.OK) ; 
    	Organ organ = null ;
    	if(!StringUtils.isBlank(id)){
    		organ = organRepository.findById(id).orElse(null);
    		if(organ != null){	//系统管理员， 不允许 使用 接口删除
    			organRepository.delete(organ);
    		}else{
    			result.setStatus(RestResultType.ORGAN_DELETE);
    		}
    	}
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}