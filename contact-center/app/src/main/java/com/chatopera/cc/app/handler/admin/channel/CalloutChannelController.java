/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app.handler.admin.channel;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.app.persistence.repository.ConsultInviteRepository;
import com.chatopera.cc.app.persistence.repository.SNSAccountRepository;
import com.chatopera.cc.app.persistence.repository.SecretRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.CousultInvite;
import com.chatopera.cc.app.model.SNSAccount;
import com.chatopera.cc.app.model.Secret;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/callout")
public class CalloutChannelController extends Handler {
    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private ConsultInviteRepository invite;

    @Autowired
    private SecretRepository secRes ;

    @RequestMapping("/index")
    @Menu(type = "callout" , subtype = "channel" , access = false ,admin = true)
    public ModelAndView index(ModelMap map , HttpServletRequest request , @Valid String execute) {
        map.addAttribute("snsAccountList", snsAccountRes.findBySnstypeAndOrgi( MainContext.ChannelTypeEnum.PHONE.toString() , super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request)))) ;
        List<Secret> secretConfig = secRes.findByOrgi(super.getOrgi(request)) ;
        if(secretConfig!=null && secretConfig.size() > 0){
            map.addAttribute("secret", secretConfig.get(0)) ;
        }
        if(!StringUtils.isBlank(execute) && execute.equals("false")){
            map.addAttribute("execute", execute) ;
        }
        return request(super.createAdminTempletResponse("/admin/channel/callout/index"));
    }

    @RequestMapping("/add")
    @Menu(type = "callout" , subtype = "channel" , access = false ,admin = true)
    public ModelAndView add(ModelMap map , HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/channel/callout/add"));
    }

    @RequestMapping("/save")
    @Menu(type = "callout" , subtype = "channel")
    public ModelAndView save(HttpServletRequest request ,@Valid SNSAccount snsAccount) throws NoSuchAlgorithmException, CSKefuException {
        if(!StringUtils.isBlank(snsAccount.getBaseURL())){
            snsAccount.setSnsid(snsAccount.getBaseURL()); // set sns ID the same SNSAccount
            int count = snsAccountRes.countBySnsidAndOrgi(snsAccount.getSnsid() , super.getOrgi(request)) ;
            if(count == 0){
                snsAccount.setOrgi(super.getOrgi(request));
                snsAccount.setSnstype(MainContext.ChannelTypeEnum.PHONE.toString());
                snsAccount.setCreatetime(new Date());
                snsAccountRes.save(snsAccount) ;

                /**
                 * 同时创建CousultInvite 记录
                 */
                CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(snsAccount.getSnsid(), super.getOrgi(request)) ;
                if(coultInvite ==null){
                    coultInvite = new CousultInvite() ;
                    coultInvite.setSnsaccountid(snsAccount.getSnsid());
                    coultInvite.setCreate_time(new Date());
                    coultInvite.setOrgi(super.getOrgi(request));
                    coultInvite.setName(snsAccount.getName());
                    invite.save(coultInvite) ;
                }
            } else {
                throw new CSKefuException("该语音渠道标识已经存在，创建失败。");
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callout/index.html"));
    }

    @RequestMapping("/delete")
    @Menu(type = "callout" , subtype = "delete")
    public ModelAndView delete(ModelMap map , HttpServletRequest request , @Valid String id  , @Valid String confirm) {
        boolean execute = false ;
        if(execute = MainUtils.secConfirm(secRes, super.getOrgi(request), confirm)){
            SNSAccount snsAccount = snsAccountRes.findByIdAndOrgi(id , super.getOrgi(request)) ;
            if(snsAccountRes!=null){
                snsAccountRes.delete(snsAccount);
                CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(snsAccount.getSnsid(), super.getOrgi(request)) ;
                if(coultInvite != null){
                    invite.delete(coultInvite);
                }
            }
        }

        return request(super.createRequestPageTempletResponse("redirect:/admin/callout/index.html?execute="+execute));
    }

    @RequestMapping("/edit")
    @Menu(type = "callout" , subtype = "channel" , access = false ,admin = true)
    public ModelAndView edit(ModelMap map , HttpServletRequest request , @Valid String id) {
        map.addAttribute("snsAccount", snsAccountRes.findByIdAndOrgi(id , super.getOrgi(request))) ;
        return request(super.createRequestPageTempletResponse("/admin/channel/callout/edit"));
    }

    @RequestMapping("/update")
    @Menu(type = "callout" , subtype = "channel" , access = false ,admin = true)
    public ModelAndView update(HttpServletRequest request ,@Valid SNSAccount snsAccount) throws NoSuchAlgorithmException {
        SNSAccount oldSnsAccount = snsAccountRes.findByIdAndOrgi(snsAccount.getId() , super.getOrgi(request));
        if(oldSnsAccount!=null){
            oldSnsAccount.setName(snsAccount.getName());
            oldSnsAccount.setBaseURL(snsAccount.getBaseURL());
            oldSnsAccount.setUpdatetime(new Date());
            /**
             * SNSID如果有变更，需要同时变更 CoultInvite 表的 记录
             */
            if(!StringUtils.isBlank(oldSnsAccount.getSnsid())){
                CousultInvite coultInvite = invite.findBySnsaccountidAndOrgi(oldSnsAccount.getSnsid(), super.getOrgi(request)) ;
                if(coultInvite ==null){
                    /**
                     * 同时创建CousultInvite 记录
                     */
                    coultInvite = new CousultInvite() ;
                    coultInvite.setSnsaccountid(oldSnsAccount.getSnsid());
                    coultInvite.setCreate_time(new Date());
                    coultInvite.setOrgi(super.getOrgi(request));
                    coultInvite.setName(snsAccount.getName());
                    invite.save(coultInvite) ;
                }
            }

            oldSnsAccount.setSnstype(MainContext.ChannelTypeEnum.PHONE.toString());
            snsAccountRes.save(oldSnsAccount) ;
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/callout/index.html"));
    }
}
