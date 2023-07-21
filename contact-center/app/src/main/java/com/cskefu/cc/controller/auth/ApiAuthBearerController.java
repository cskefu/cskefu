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
package com.cskefu.cc.controller.auth;

import com.alibaba.fastjson.JSONObject;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.basic.auth.BearerTokenMgr;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.User;
import com.cskefu.cc.model.UserRole;
import com.cskefu.cc.persistence.repository.UserRepository;
import com.cskefu.cc.persistence.repository.UserRoleRepository;
import com.cskefu.cc.util.Menu;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 账号密码登录
 */
@RestController
@RequestMapping("/auth/token/bearer")
public class ApiAuthBearerController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(ApiAuthBearerController.class);

    @Value("${server.session-timeout}")
    private int tokenExpiredIn;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRes;

    @Autowired
    private BearerTokenMgr bearerTokenMgr;

    /**
     * 登录服务，传入登录账号和密码
     *
     * @param request
     * @param response
     * @param username
     * @param password
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.POST)
    @Menu(type = "apps", subtype = "token", access = true)
    public ResponseEntity login(HttpServletRequest request, HttpServletResponse response, @Valid String username, @Valid String password) {
        User loginUser = userRepository.findByUsernameAndPassword(username, MainUtils.md5(password));
        ResponseEntity entity = null;
        if (loginUser != null && !StringUtils.isBlank(loginUser.getId())) {
            loginUser.setLogin(true);
            List<UserRole> userRoleList = userRoleRes.findByUser(loginUser);
            if (userRoleList != null && userRoleList.size() > 0) {
                for (UserRole userRole : userRoleList) {
                    loginUser.getRoleList().add(userRole.getRole());
                }
            }
            loginUser.setLastlogintime(new Date());
            if (!StringUtils.isBlank(loginUser.getId())) {
                userRepository.save(loginUser);
            }
            String auth = MainUtils.getUUID();
            bearerTokenMgr.update(auth, loginUser);
            JSONObject body = new JSONObject();
            body.put("token", "Bearer " + auth);
            body.put("expiredInSeconds", tokenExpiredIn);

            entity = new ResponseEntity<>(body.toString(), HttpStatus.OK);
            response.addCookie(new Cookie("authorization", auth));
        } else {
            entity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


        return entity;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.GET)
    @Menu(type = "apps", subtype = "token", access = true)
    public ResponseEntity error(HttpServletRequest request) {
        User data = super.getUser(request);
        return new ResponseEntity<>(data, data != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity logout(HttpServletRequest request, @RequestHeader(value = "authorization") String authorization) {
        bearerTokenMgr.delete(authorization);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}