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
package com.chatopera.cc.controller.admin.callcenter;

import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.controller.Handler;
import com.chatopera.cc.model.Extention;
import com.chatopera.cc.model.PbxHost;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.CallcenterOutboundProxy;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.freeswitch.model.CallCenterAgent;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/callcenter")
public class CallCenterExtentionController extends Handler {

    @Autowired
    private PbxHostRepository pbxHostRes;

    @Autowired
    private ExtentionRepository extentionRes;

    @Autowired
    private SipTrunkRepository sipTrunkRes;

    @Autowired
    private MediaRepository mediaRes;

    @Autowired
    private ServiceAiRepository serviceAiRes;

    @Autowired
    private ProductRepository productRes;

    @Autowired
    private QueSurveyProcessRepository queSurveyProcessRes;

    @Autowired
    private Cache cache;


    @RequestMapping(value = "/extention")
    @Menu(type = "callcenter", subtype = "callcenterresource", access = false, admin = true)
    public ModelAndView extention(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        List<PbxHost> pbxHostList = pbxHostRes.findByOrgi(super.getOrgi(request));
        map.addAttribute("pbxHostList", pbxHostList);
        PbxHost pbxHost = null;
        if (pbxHostList.size() > 0) {
            map.addAttribute("pbxHost", pbxHost = getPbxHost(pbxHostList, hostid));
            map.addAttribute("extentionList", extentionRes.findByHostidAndOrgi(pbxHost.getId(), super.getOrgi(request)));
        }
        return request(super.createRequestPageTempletResponse("/admin/callcenter/extention/index"));
    }

    private PbxHost getPbxHost(List<PbxHost> pbxHostList, String hostid) {
        PbxHost pbxHost = pbxHostList.get(0);
        if (StringUtils.isNotBlank(hostid)) {
            for (PbxHost pbx : pbxHostList) {
                if (pbx.getId().equals(hostid)) {
                    pbxHost = pbx;
                    break;
                }
            }
        }
        return pbxHost;
    }

    @RequestMapping(value = "/extention/add")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentionadd(ModelMap map, HttpServletRequest request, @Valid String hostid) {
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));

        map.addAttribute("sipTrunkListList", sipTrunkRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));

        map.put("mediaList", mediaRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));
        return request(super.createRequestPageTempletResponse("/admin/callcenter/extention/add"));
    }

    @RequestMapping(value = "/extention/save")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentionsave(ModelMap map, HttpServletRequest request, @Valid Extention extention) {
        if (StringUtils.isNotBlank(extention.getExtention()) && StringUtils.isNotBlank(extention.getPassword())) {
            String[] extstr = extention.getExtention().split("[,， ]");
            int extnum = 0;
            for (String ext : extstr) {
                if (ext.matches("[\\d]{3,8}")) {    //分机号码最少3位数字
                    createNewExtention(ext, super.getUser(request), extention.getHostid(), extention.getPassword(), super.getOrgi(request), extention);
                } else {
                    String[] ph = ext.split("[~-]");
                    if (ph.length == 2 && ph[0].matches("[\\d]{3,8}") && ph[1].matches("[\\d]{3,8}") && ph[0].length() == ph[1].length()) {
                        int start = Integer.parseInt(ph[0]);
                        int end = Integer.parseInt(ph[1]);

                        for (int i = start; i <= end && extnum < 100; i++) {    //最大一次批量生产的 分机号不超过100个
                            createNewExtention(String.valueOf(i), super.getUser(request), extention.getHostid(), extention.getPassword(), super.getOrgi(request), extention);
                        }
                    }
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/extention.html?hostid=" + extention.getHostid()));
    }

    private Extention createNewExtention(String num, User user, String hostid, String password, String orgi, Extention src) {
        Extention extno = new Extention();
        extno.setExtention(num);
        extno.setOrgi(orgi);
        extno.setCreater(user.getId());
        extno.setHostid(hostid);
        extno.setPassword(password);

        extno.setPlaynum(src.isPlaynum());
        extno.setCallout(src.isCallout());
        extno.setRecord(src.isRecord());
        extno.setExtype(src.getExtype());
        extno.setMediapath(src.getMediapath());

        extno.setSiptrunk(src.getSiptrunk());
        extno.setEnablewebrtc(src.isEnablewebrtc());
        int count = extentionRes.countByExtentionAndHostidAndOrgi(extno.getExtention(), hostid, orgi);
        if (count == 0) {
            extentionRes.save(extno);
        }
        return extno;
    }

    @RequestMapping(value = "/extention/edit")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentionedit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String hostid) {
        map.addAttribute("extention", extentionRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
        map.put("mediaList", mediaRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));
        map.addAttribute("sipTrunkListList", sipTrunkRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));

        return request(super.createRequestPageTempletResponse("/admin/callcenter/extention/edit"));
    }

    @RequestMapping(value = "/extention/update")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentionupdate(ModelMap map, HttpServletRequest request, @Valid Extention extention) {
        if (StringUtils.isNotBlank(extention.getId())) {
            Extention ext = extentionRes.findByIdAndOrgi(extention.getId(), super.getOrgi(request));
            if (ext != null) {
//				ext.setExtention(extention.getExtention());//分机号不能修改
                if (StringUtils.isNotBlank(extention.getPassword())) {
                    ext.setPassword(extention.getPassword());
                }
                ext.setPlaynum(extention.isPlaynum());
                ext.setCallout(extention.isCallout());
                ext.setRecord(extention.isRecord());
                ext.setExtype(extention.getExtype());
                ext.setSubtype(extention.getSubtype());
                ext.setDescription(extention.getDescription());

                ext.setMediapath(extention.getMediapath());

                ext.setSiptrunk(extention.getSiptrunk());
                ext.setEnablewebrtc(extention.isEnablewebrtc());

                ext.setUpdatetime(new Date());
                extentionRes.save(ext);

                List<CallCenterAgent> callOutAgentList = CallcenterOutboundProxy.extention(ext.getExtention());
                for (CallCenterAgent callOutAgent : callOutAgentList) {
                    callOutAgent.setSiptrunk(ext.getSiptrunk());
                    cache.putCallCenterAgentByIdAndOrgi(callOutAgent.getUserid(), callOutAgent.getOrgi(), callOutAgent);
                }
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/extention.html?hostid=" + extention.getHostid()));
    }

    @RequestMapping(value = "/extention/ivr")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView ivr(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String hostid) {
        map.addAttribute("extention", extentionRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.put("pbxHost", pbxHostRes.findByIdAndOrgi(hostid, super.getOrgi(request)));
        map.put("mediaList", mediaRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));
        map.addAttribute("sipTrunkListList", sipTrunkRes.findByHostidAndOrgi(hostid, super.getOrgi(request)));

        map.put("serviceAiList", serviceAiRes.findByOrgi(super.getOrgi(request)));
        map.put("queList", queSurveyProcessRes.findByOrgi(super.getOrgi(request)));
        map.put("productList", productRes.findByOrgi(super.getOrgi(request)));

        return request(super.createRequestPageTempletResponse("/admin/callcenter/extention/ivr"));
    }

    @RequestMapping(value = "/extention/ivr/update")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView ivrupdate(ModelMap map, HttpServletRequest request, @Valid Extention extention) {
        if (StringUtils.isNotBlank(extention.getId())) {
            Extention ext = extentionRes.findByIdAndOrgi(extention.getId(), super.getOrgi(request));
            if (ext != null) {

                ext.setEnableai(extention.getEnableai());
                ext.setAiid(extention.getAiid());
                ext.setSceneid(extention.getSceneid());
                ext.setWelcomemsg(extention.getWelcomemsg());
                ext.setWaitmsg(extention.getWaitmsg());
                ext.setTipmessage(extention.getTipmessage());

                ext.setAitype(extention.getAitype());
                ext.setBustype(extention.getBustype());
                ext.setProid(extention.getProid());
                ext.setQueid(extention.getQueid());

                extentionRes.save(ext);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/extention.html?hostid=" + extention.getHostid()));
    }

    @RequestMapping(value = "/extention/delete")
    @Menu(type = "callcenter", subtype = "extention", access = false, admin = true)
    public ModelAndView extentiondelete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String hostid) {
        if (StringUtils.isNotBlank(id)) {
            extentionRes.delete(id);
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callcenter/extention.html?hostid=" + hostid));
    }
}
