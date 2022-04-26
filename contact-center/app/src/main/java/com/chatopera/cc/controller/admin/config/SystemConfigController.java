/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.controller.admin.config;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.RedisCommand;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.interceptor.UserExperiencePlanInterceptorHandler;
import com.chatopera.cc.model.Dict;
import com.chatopera.cc.model.Secret;
import com.chatopera.cc.model.SysDic;
import com.chatopera.cc.model.SystemConfig;
import com.chatopera.cc.persistence.repository.SecretRepository;
import com.chatopera.cc.persistence.repository.SystemConfigRepository;
import com.chatopera.cc.persistence.repository.SystemMessageRepository;
import com.chatopera.cc.persistence.repository.TemplateRepository;
import com.chatopera.cc.util.Menu;
import com.corundumstudio.socketio.SocketIOServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/admin/config")
public class SystemConfigController extends Handler {

    @Value("${uk.im.server.port}")
    private Integer port;

    @Value("${web.upload-path}")
    private String path;

    @Autowired
    private SocketIOServer server;

    @Autowired
    private SystemConfigRepository systemConfigRes;

    @Autowired
    private RedisCommand redisCommand;

    @Autowired
    private SystemMessageRepository systemMessageRes;

    @Autowired
    private SecretRepository secRes;

    @Autowired
    private TemplateRepository templateRes;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "config", admin = true)
    public ModelAndView index(ModelMap map, HttpServletRequest request, @Valid String execute) throws SQLException {
        map.addAttribute("server", server);
        if (MainContext.hasModule(Constants.CSKEFU_MODULE_ENTIM)) {
            map.addAttribute(Constants.CSKEFU_MODULE_ENTIM, true);
        }
        if (request.getSession().getAttribute(Constants.CSKEFU_SYSTEM_INFOACQ) != null) {
            map.addAttribute(
                    Constants.CSKEFU_MODULE_ENTIM, request.getSession().getAttribute(Constants.CSKEFU_SYSTEM_INFOACQ));
        }
        map.addAttribute("server", server);
        map.addAttribute("imServerStatus", MainContext.getIMServerStatus());
        List<Secret> secretConfig = secRes.findByOrgi(super.getOrgi(request));
        // check out secretConfig
        if (secretConfig != null && secretConfig.size() > 0) {
            map.addAttribute("secret", secretConfig.get(0));
        }

        List<SysDic> dicList = Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_DIC);
        SysDic callCenterDic = null, workOrderDic = null, smsDic = null;
        for (SysDic dic : dicList) {
            if (dic.getCode().equals(Constants.CSKEFU_SYSTEM_CALLCENTER)) {
                callCenterDic = dic;
            }
            if (dic.getCode().equals(Constants.CSKEFU_SYSTEM_WORKORDEREMAIL)) {
                workOrderDic = dic;
            }
            if (dic.getCode().equals(Constants.CSKEFU_SYSTEM_SMSEMAIL)) {
                smsDic = dic;
            }
        }
        if (callCenterDic != null) {
            map.addAttribute(
                    "templateList",
                    templateRes.findByTemplettypeAndOrgi(callCenterDic.getId(), super.getOrgi(request)));
        }
        if (workOrderDic != null) {
            map.addAttribute(
                    "workOrderList",
                    templateRes.findByTemplettypeAndOrgi(workOrderDic.getId(), super.getOrgi(request)));
        }
        if (smsDic != null) {
            map.addAttribute("smsList", templateRes.findByTemplettypeAndOrgi(smsDic.getId(), super.getOrgi(request)));
        }

        map.addAttribute(
                "sysMessageList", systemMessageRes.findByMsgtypeAndOrgi(MainContext.SystemMessageType.EMAIL.toString(),
                        super.getOrgi(request)));

        if (StringUtils.isNotBlank(execute) && execute.equals("false")) {
            map.addAttribute("execute", execute);
        }
        if (StringUtils.isNotBlank(request.getParameter("msg"))) {
            map.addAttribute("msg", request.getParameter("msg"));
        }

        String userExpTelemetrySetting = redisCommand.get(UserExperiencePlanInterceptorHandler.FLAG_KEY);
        if (StringUtils.isEmpty(userExpTelemetrySetting) || StringUtils.equalsIgnoreCase(userExpTelemetrySetting, UserExperiencePlanInterceptorHandler.USER_EXP_PLAN_ON)) {
            map.addAttribute("userExpTelemetrySetting", true);
        } else {
            map.addAttribute("userExpTelemetrySetting", false);
        }

        return request(super.createView("/admin/config/index"));
    }

    @RequestMapping("/stopimserver")
    @Menu(type = "admin", subtype = "stopimserver", access = false, admin = true)
    public ModelAndView stopimserver(ModelMap map, HttpServletRequest request, @Valid String confirm) throws SQLException {
        boolean execute;
        if (execute = MainUtils.secConfirm(secRes, super.getOrgi(request), confirm)) {
            server.stop();
            MainContext.setIMServerStatus(false);
        }
        return request(super.createView("redirect:/admin/config/index.html?execute=" + execute));
    }

    @RequestMapping("/startentim")
    @Menu(type = "admin", subtype = "startentim", access = false, admin = true)
    public ModelAndView startentim(ModelMap map, HttpServletRequest request) throws SQLException {
        MainContext.enableModule(Constants.CSKEFU_MODULE_ENTIM);
        return request(super.createView("redirect:/admin/config/index.html"));
    }

    @RequestMapping("/stopentim")
    @Menu(type = "admin", subtype = "stopentim", access = false, admin = true)
    public ModelAndView stopentim(ModelMap map, HttpServletRequest request) throws SQLException {
        MainContext.removeModule(Constants.CSKEFU_MODULE_ENTIM);
        return request(super.createView("redirect:/admin/config/index.html"));
    }

    /**
     * 危险操作，请谨慎调用 ， WebLogic/WebSphere/Oracle等中间件服务器禁止调用
     *
     * @param map
     * @param request
     * @return
     * @throws SQLException
     */
    @RequestMapping("/stop")
    @Menu(type = "admin", subtype = "stop", access = false, admin = true)
    public ModelAndView stop(ModelMap map, HttpServletRequest request, @Valid String confirm) throws SQLException {
        boolean execute = false;
        if (execute = MainUtils.secConfirm(secRes, super.getOrgi(request), confirm)) {
            server.stop();
            MainContext.setIMServerStatus(false);
            System.exit(0);
        }
        return request(super.createView("redirect:/admin/config/index.html?execute=" + execute));
    }


    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "save", admin = true)
    public ModelAndView save(
            ModelMap map, HttpServletRequest request,
            @Valid SystemConfig config,
            BindingResult result,
            @RequestParam(value = "keyfile", required = false) MultipartFile keyfile,
            @RequestParam(value = "loginlogo", required = false) MultipartFile loginlogo,
            @RequestParam(value = "consolelogo", required = false) MultipartFile consolelogo,
            @RequestParam(value = "favlogo", required = false) MultipartFile favlogo,
            @Valid Secret secret) throws SQLException, IOException, NoSuchAlgorithmException {
    	/*SystemConfig systemConfig = systemConfigRes.findByOrgi(super.getOrgi(request)) ;
    	config.setOrgi(super.getOrgi(request));*/
        SystemConfig systemConfig = systemConfigRes.findByOrgi(Constants.SYSTEM_ORGI);
        config.setOrgi(Constants.SYSTEM_ORGI);
        String msg = "0";
        if (StringUtils.isBlank(config.getJkspassword())) {
            config.setJkspassword(null);
        }
        if (systemConfig == null) {
            config.setCreater(super.getUser(request).getId());
            config.setCreatetime(new Date());
            systemConfig = config;
        } else {
            MainUtils.copyProperties(config, systemConfig);
        }
        if (config.isEnablessl()) {
            if (keyfile != null && keyfile.getBytes() != null && keyfile.getBytes().length > 0 && keyfile.getOriginalFilename() != null && keyfile.getOriginalFilename().length() > 0) {
                FileUtils.writeByteArrayToFile(
                        new File(path, "ssl/" + keyfile.getOriginalFilename()), keyfile.getBytes());
                systemConfig.setJksfile(keyfile.getOriginalFilename());
                File sslFilePath = new File(path, "ssl/https.properties");
                if (!sslFilePath.getParentFile().exists()) {
                    sslFilePath.getParentFile().mkdirs();
                }
                Properties prop = new Properties();
                FileOutputStream oFile = new FileOutputStream(sslFilePath);//true表示追加打开
                prop.setProperty("key-store-password", MainUtils.encryption(systemConfig.getJkspassword()));
                prop.setProperty("key-store", systemConfig.getJksfile());
                prop.store(oFile, "SSL Properties File");
                oFile.close();
            }
        } else if (new File(path, "ssl").exists()) {
            File[] sslFiles = new File(path, "ssl").listFiles();
            for (File sslFile : sslFiles) {
                sslFile.delete();
            }
        }

        if (loginlogo != null && StringUtils.isNotBlank(
                loginlogo.getOriginalFilename()) && loginlogo.getOriginalFilename().lastIndexOf(".") > 0) {
            systemConfig.setLoginlogo(super.saveImageFileWithMultipart(loginlogo));
        }
        if (consolelogo != null && StringUtils.isNotBlank(
                consolelogo.getOriginalFilename()) && consolelogo.getOriginalFilename().lastIndexOf(".") > 0) {
            systemConfig.setConsolelogo(super.saveImageFileWithMultipart(consolelogo));
        }
        if (favlogo != null && StringUtils.isNotBlank(
                favlogo.getOriginalFilename()) && favlogo.getOriginalFilename().lastIndexOf(".") > 0) {
            systemConfig.setFavlogo(super.saveImageFileWithMultipart(favlogo));
        }

        if (secret != null && StringUtils.isNotBlank(secret.getPassword())) {
            List<Secret> secretConfig = secRes.findByOrgi(super.getOrgi(request));
            String repassword = request.getParameter("repassword");
            if (StringUtils.isNotBlank(repassword) && repassword.equals(secret.getPassword())) {
                if (secretConfig != null && secretConfig.size() > 0) {
                    Secret tempSecret = secretConfig.get(0);
                    String oldpass = request.getParameter("oldpass");
                    if (StringUtils.isNotBlank(oldpass) && MainUtils.md5(oldpass).equals(tempSecret.getPassword())) {
                        tempSecret.setPassword(MainUtils.md5(secret.getPassword()));
                        msg = "1";
                        tempSecret.setEnable(true);
                        secRes.save(tempSecret);
                    } else {
                        msg = "3";
                    }
                } else {
                    secret.setOrgi(super.getOrgi(request));
                    secret.setCreater(super.getUser(request).getId());
                    secret.setCreatetime(new Date());
                    secret.setPassword(MainUtils.md5(secret.getPassword()));
                    secret.setEnable(true);
                    msg = "1";
                    secRes.save(secret);
                }
            } else {
                msg = "2";
            }
            map.addAttribute("msg", msg);
        }

        // 设置用户体验计划开关
        redisCommand.put(UserExperiencePlanInterceptorHandler.FLAG_KEY, config.getUserExpTelemetrySetting() ? UserExperiencePlanInterceptorHandler.USER_EXP_PLAN_ON : UserExperiencePlanInterceptorHandler.USER_EXP_PLAN_OFF);

        // 保存到数据库
        systemConfigRes.save(systemConfig);

        MainContext.getCache().putSystemByIdAndOrgi("systemConfig", super.getOrgi(request), systemConfig);
        map.addAttribute("imServerStatus", MainContext.getIMServerStatus());

        return request(super.createView("redirect:/admin/config/index.html?msg=" + msg));
    }
}