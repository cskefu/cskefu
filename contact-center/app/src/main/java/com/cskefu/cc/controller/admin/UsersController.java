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
package com.cskefu.cc.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.ExtensionRepository;
import com.cskefu.cc.persistence.repository.OrganUserRepository;
import com.cskefu.cc.persistence.repository.PbxHostRepository;
import com.cskefu.cc.persistence.repository.RoleRepository;
import com.cskefu.cc.persistence.repository.UserRepository;
import com.cskefu.cc.persistence.repository.UserRoleRepository;
import com.cskefu.cc.proxy.AgentSessionProxy;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.proxy.UserProxy;
import com.cskefu.cc.util.Menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 */
@Controller
@RequestMapping("/admin/user")
public class UsersController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRes;

    @Autowired
    private RoleRepository roleRes;

    @Autowired
    OrganProxy organProxy;

    @Autowired
    UserProxy userProxy;

    @Autowired
    private OrganUserRepository organUserRes;

    @Autowired
    private PbxHostRepository pbxHostRes;

    @Autowired
    private ExtensionRepository extensionRes;

    /**
     * 只返回根用户：只属于该部门的非下级部门的用户
     *
     * @param map
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView index(ModelMap map, HttpServletRequest request) throws IOException {
        Organ currentOrgan = super.getOrgan(request);
        ArrayList<String> organs = new ArrayList<>();
        organs.add(currentOrgan.getId());

        map.addAttribute("currentOrgan", currentOrgan);
        map.addAttribute("userList", userProxy.findUserInOrgans(organs, PageRequest.of(
                super.getP(request),
                super.getPs(request),
                Sort.Direction.ASC,
                "createtime")));

        return request(super.createView("/admin/user/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        ModelAndView view = request(super.createView("/admin/user/add"));
        Organ currentOrgan = super.getOrgan(request);
        Map<String, Organ> organs = organProxy.findAllOrganByParent(currentOrgan);
        List<Role> sysRoles = roleRes.findAll();
        map.addAttribute("currentOrgan", currentOrgan);
        map.addAttribute("organList", organs.values());
        map.addAttribute("sysRoles", sysRoles);

        return view;
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createView("/admin/user/edit"));
        User user = userRepository.findById(id).orElse(null);
        if (user != null && MainContext.hasModule(Constants.CSKEFU_MODULE_CALLCENTER)) {
            // 加载呼叫中心信息
            extensionRes.findByAgentno(user.getId()).ifPresent(p -> {
                user.setExtensionId(p.getId());
                user.setExtension(p);

                PbxHost one = pbxHostRes.findById(p.getHostid()).orElse(null);
                user.setPbxhostId(one.getId());
                user.setPbxHost(one);
            });
        }
        view.addObject("userData", user);
        return view;
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView delete(HttpServletRequest request, @Valid User user) {
        String msg = "admin_user_delete";
        if (user != null) {
            User dbUser = userRepository.findById(user.getId()).orElse(null);
            if (dbUser.isSuperadmin()) {
                msg = "admin_user_abandoned";
            } else {
                // 删除用户的时候，同时删除用户对应的权限数据
                List<UserRole> userRole = userRoleRes.findByUser(user);
                userRoleRes.deleteAll(userRole);
                // 删除用户对应的组织机构关系
                List<OrganUser> organUsers = organUserRes.findByUserid(user.getId());
                organUserRes.deleteAll(organUsers);

                userRepository.delete(dbUser);

                AgentSessionProxy agentSessionProxy = MainContext.getContext().getBean(AgentSessionProxy.class);
                agentSessionProxy.deleteUserSession(dbUser.getId());
            }
        } else {
            msg = "admin_user_not_exist";
        }
        return request(super.createView("redirect:/admin/user/index.html?msg=" + msg));
    }

}