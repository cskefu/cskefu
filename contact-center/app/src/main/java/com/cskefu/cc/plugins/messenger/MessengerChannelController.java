/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

package com.cskefu.cc.plugins.messenger;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.model.FbMessenger;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.model.SNSAccount;
import com.cskefu.cc.persistence.repository.FbMessengerRepository;
import com.cskefu.cc.persistence.repository.OrganRepository;
import com.cskefu.cc.persistence.repository.SNSAccountRepository;
import com.cskefu.cc.proxy.OrganProxy;
import com.cskefu.cc.util.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/messenger")
public class MessengerChannelController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(MessengerChannelController.class);

    @Autowired
    private FbMessengerRepository fbMessengerRepository;

    @Autowired
    private OrganRepository organRepository;

    @Autowired
    private OrganProxy organProxy;

    @Autowired
    private SNSAccountRepository snsAccountRepository;

    private Map<String, Organ> getOwnOrgan(HttpServletRequest request) {
        return organProxy.findAllOrganByParentAndOrgi(super.getOrgan(request), super.getOrgi(request));

    }

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        Map<String, Organ> organs = getOwnOrgan(request);
        List<FbMessenger> fbMessengers = fbMessengerRepository.findByOrganIn(organs.keySet());
        Organ currentOrgan = super.getOrgan(request);
        map.addAttribute("fbMessengers", fbMessengers);
        map.addAttribute("organs", organs);
        map.addAttribute("organ", currentOrgan);
        return request(super.createView("/admin/channel/messenger/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        Organ currentOrgan = super.getOrgan(request);
        map.addAttribute("organ", currentOrgan);
        return request(super.createView("/admin/channel/messenger/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView save(ModelMap map, HttpServletRequest request, @Valid FbMessenger fbMessenger) {
        String msg = "save_ok";
        Organ currentOrgan = super.getOrgan(request);
        FbMessenger fbMessengerOne = fbMessengerRepository.findOneByPageId(fbMessenger.getPageId());
        if (fbMessengerOne != null) {
            msg = "save_no_PageId";
        } else {
            fbMessenger.setId(MainUtils.getUUID());
            fbMessenger.setOrgan(currentOrgan.getId());

            if (fbMessenger.getStatus() == null) {
                fbMessenger.setStatus("disabled");
            }
            fbMessenger.setCreatetime(new Date());
            fbMessenger.setUpdatetime(new Date());
            fbMessenger.setAiid(null);
            fbMessengerRepository.save(fbMessenger);

            SNSAccount snsAccount = new SNSAccount();
            snsAccount.setId(MainUtils.genID());
            snsAccount.setCreatetime(new Date());
            snsAccount.setOrgi(super.getOrgi(request));
            snsAccount.setName(fbMessenger.getName());
            snsAccount.setOrgan(currentOrgan.getId());
            snsAccount.setSnsid(fbMessenger.getPageId());
            snsAccount.setSnstype(MainContext.ChannelType.MESSENGER.toString());
            snsAccountRepository.save(snsAccount);
        }
        return request(super.createView("redirect:/admin/messenger/index.html?msg=" + msg));
    }

    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        FbMessenger fbMessenger = fbMessengerRepository.findOne(id);

        Organ fbOrgan = organRepository.getOne(fbMessenger.getOrgan());
        map.addAttribute("organ", fbOrgan);
        map.addAttribute("fb", fbMessenger);

        return request(super.createView("/admin/channel/messenger/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView update(ModelMap map, HttpServletRequest request, @Valid FbMessenger fbMessenger) {
        String msg = "update_ok";
        FbMessenger oldMessenger = fbMessengerRepository.findOne(fbMessenger.getId());
        oldMessenger.setName(fbMessenger.getName());
        if (fbMessenger.getStatus() != null) {
            oldMessenger.setStatus(fbMessenger.getStatus());
        } else {
            oldMessenger.setStatus(Constants.MESSENGER_CHANNEL_DISABLED);
        }

        oldMessenger.setToken(fbMessenger.getToken());
        oldMessenger.setVerifyToken(fbMessenger.getVerifyToken());
        oldMessenger.setUpdatetime(new Date());

        fbMessengerRepository.save(oldMessenger);

        return request(super.createView("redirect:/admin/messenger/index.html?msg=" + msg));
    }

    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView delete(ModelMap map, HttpServletRequest request, @Valid String id) {
        String msg = "delete_ok";
        FbMessenger fbMessenger = fbMessengerRepository.getOne(id);
        fbMessengerRepository.delete(id);

        snsAccountRepository.findBySnsid(fbMessenger.getPageId()).ifPresent(snsAccount -> {
            snsAccountRepository.delete(snsAccount);
        });

        return request(super.createView("redirect:/admin/messenger/index.html?msg=" + msg));
    }

    @RequestMapping("/setting")
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView setting(ModelMap map, HttpServletRequest request, @Valid String id) {
        FbMessenger fbMessenger = fbMessengerRepository.findOne(id);
        Organ fbOrgan = organRepository.getOne(fbMessenger.getOrgan());

        map.mergeAttributes(fbMessenger.parseConfigMap());
        map.addAttribute("organ", fbOrgan);
        map.addAttribute("fb", fbMessenger);

        return request(super.createView("/admin/channel/messenger/setting"));
    }

    @RequestMapping(value = "/setting/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Menu(type = "admin", subtype = "messenger")
    public ModelAndView saveSetting(ModelMap map, HttpServletRequest request, @Valid String id, @RequestBody MultiValueMap<String, String> formData) {
        String msg = "update_ok";

        FbMessenger fbMessenger = fbMessengerRepository.findOne(id);
        if (fbMessenger != null) {
            fbMessenger.setConfigMap(formData.toSingleValueMap());
            fbMessengerRepository.save(fbMessenger);
        }

        return request(super.createView("redirect:/admin/messenger/index.html?msg=" + msg));
    }

    @RequestMapping("/setStatus")
    @Menu(type = "admin", subtype = "messenger")
    @ResponseBody
    public String setStatus(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String status) {
        FbMessenger fbMessenger = fbMessengerRepository.findOne(id);
        fbMessenger.setStatus(status);
        fbMessengerRepository.save(fbMessenger);
        return "ok";
    }

}

