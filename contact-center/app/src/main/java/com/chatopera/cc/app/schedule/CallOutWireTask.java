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
package com.chatopera.cc.app.schedule;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Constants;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.exchange.CallOutWireEvent;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.exception.CallOutRuntimeException;
import com.chatopera.cc.exception.FreeSwitchException;
import com.chatopera.cc.util.mobile.MobileAddress;
import com.chatopera.cc.util.mobile.MobileNumberUtils;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.persistence.es.ContactsRepository;
import com.chatopera.cc.app.persistence.repository.*;
import com.chatopera.cc.util.OnlineUserUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 外呼系统接线任务处理
 */
@Component
public class CallOutWireTask implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(CallOutWireTask.class);

    @Autowired
    private CallOutDialplanRepository callOutDialplanRes;

    @Autowired
    private UserRepository userRes;

    @Autowired
    OnlineUserRepository onlineUserRes;

    @Autowired
    AgentUserRepository agentUserRes;

    @Autowired
    ConsultInviteRepository consultInviteRes;

    @Autowired
    AgentServiceRepository agentServiceRes;

    @Autowired
    ContactsRepository contactsRes;

    @Autowired
    AgentUserContactsRepository agentUserContactsRes;

    @Autowired
    AgentStatusRepository agentStatusRes;


    @Autowired
    StatusEventRepository statusEventRes;

    @Autowired
    OrganRepository organRes;

    @Autowired
    private StringRedisTemplate redis;


    protected AgentStatus getAgentStatus(final String agentId, final String orgi) {
        List<AgentStatus> x = agentStatusRes.findByAgentnoAndOrgi(agentId, MainContext.SYSTEM_ORGI);
        if (x.size() > 0)
            return x.get(0);
        return null;
    }

    /**
     * 绑定服务记录
     *
     * @return
     */
    private String bindAgentService(final AgentUser agentUser,
                                    final String orgi,
                                    final String channel,
                                    final String organ,
                                    final String organid,
                                    final CallOutDialplan dp,
                                    final String code,
                                    final String isp,
                                    final String caller,
                                    final String called,
                                    final String sip,
                                    final Date createtime,
                                    final String callid,
                                    final Contacts lxr) {
        final String statusEventId = MainUtils.getUUID();
        final String serviceId = MainUtils.getUUID();

        // 通话记录
        StatusEvent statusEvent = new StatusEvent();
        statusEvent.setId(statusEventId);
        statusEvent.setCalltype(MainContext.CallCenterCallTypeEnum.OUTSIDELINE.toString());
        statusEvent.setDirection(MainContext.CallTypeEnum.OUT.toString());
        statusEvent.setServiceid(serviceId);
        statusEvent.setCode(code);
        statusEvent.setOrgi(orgi);
        statusEvent.setOrgan(organ);
        statusEvent.setOrganid(organid);
        statusEvent.setCountry(agentUser.getCountry());
        statusEvent.setProvince(agentUser.getProvince());
        statusEvent.setCity(agentUser.getCity());
        statusEvent.setAgent(agentUser.getAgent());


        // 设定联系人ID
        if(lxr != null) {
            statusEvent.setContactsid(lxr.getId());
        }

        // 设定坐席名字
        User agent = userRes.findById(agentUser.getAgentno());
        if (agent != null)
            statusEvent.setAgentname(agent.getUname());

        Date now = new Date();
        statusEvent.setUpdatetime(now);
        statusEvent.setIsp(isp);
        statusEvent.setRecord(true); // 开启录音
        statusEvent.setStatus(MainContext.CallServiceStatus.INCALL.toString());
        statusEvent.setCaller(caller); // 呼叫发起号码
        statusEvent.setDiscaller(caller);
        statusEvent.setCalled(called); // 被叫号码
        statusEvent.setDiscalled(called);
        if (dp != null) // 自动外呼
            statusEvent.setDialplan(dp.getId());
        statusEvent.setVoicechannel(channel);
        statusEvent.setCallid(callid);
        statusEvent.setName(agentUser.getName()); // 坐席的名字
        // 坐席通话日期（yyyy-MM-dd）用于每小时通话数量折线图
        statusEvent.setDatestr(new SimpleDateFormat("yyyy-MM-dd").format(createtime));
        // 坐席通话时间小时（HH）用于每小时通话数量折线图
        statusEvent.setHourstr(new SimpleDateFormat("HH").format(createtime));
        statusEvent.setLocaldatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createtime));
        statusEvent.setStarttime(createtime);
        statusEvent.setSiptrunk(sip);

        statusEventRes.save(statusEvent);


        // 服务记录
        AgentService as = new AgentService();
        as.setOrgi(orgi);
        as.setId(serviceId);
        MainUtils.copyProperties(agentUser, as);
        as.setAgentuserid(agentUser.getId());
        as.setAgentserviceid(serviceId);
        as.setQualitystatus(MainContext.QualityStatus.NO.toString()); // 不做质检
        as.setInitiator(MainContext.ChatInitiatorType.USER.toString());
        as.setName(agentUser.getUsername());
        as.setDataid(agentUser.getUserid());
        as.setSessiontype(MainContext.AgentUserStatusEnum.INSERVICE.toString());
        as.setAgentusername(agentUser.getUsername());
        as.setOwner(statusEvent.getId());
        as.setAppid(channel);

        if(lxr != null){
            // 创建联系人，坐席服务关联
            AgentUserContacts auc = new AgentUserContacts();
            auc.setId(MainUtils.getUUID());
            auc.setContactsid(lxr.getId());
            auc.setAppid(channel);
            auc.setCreatetime(now);
            auc.setOrgi(orgi);
            auc.setUserid(agentUser.getUserid());
            auc.setUsername(agentUser.getUsername());
            agentUserContactsRes.save(auc);
        }

        agentServiceRes.save(as);

        return as.getId();
    }


    private boolean validatePhoneNumber(String visitorPhoneNumber) {
        return StringUtils.length(visitorPhoneNumber) == 11;
    }

    /**
     * 访客上线，创建咨询记录，分配坐席
     *
     * @param visitorPhoneNumber
     * @param agentId
     * @return
     */
    private AgentUser online(final String visitorPhoneNumber,
                             final String agentId,
                             final String channel,
                             final String organ,
                             final String organid,
                             final CallOutDialplan dp,
                             final String caller,
                             final String called,
                             final String sip,
                             final Date createtime,
                             final String callid) throws CSKefuException {
        // Define source
        MobileAddress ma = MobileNumberUtils.getAddress(visitorPhoneNumber);

        // 创建在线访客
        OnlineUser onlineUser = onlineUserRes.findByPhoneAndOrgi(visitorPhoneNumber, MainContext.SYSTEM_ORGI);
        if (onlineUser == null) {
            onlineUser = new OnlineUser();
            onlineUser.setId(MainUtils.getUUID());
            onlineUser.setUserid(onlineUser.getId());
            onlineUser.setUsertype(MainContext.OnlineUserTypeStatus.TELECOM.toString());
            onlineUser.setUsername(MainContext.GUEST_USER + "_" + MainUtils.genIDByKey(onlineUser.getId()));
            onlineUser.setOrgi(MainContext.SYSTEM_ORGI);
            onlineUser.setMobile("0");  // 不是 移动客户端
            onlineUser.setPhone(visitorPhoneNumber);
            onlineUser.setCountry(ma.getCountry());
            onlineUser.setProvince(ma.getProvince());
            onlineUser.setCity(ma.getCity());
            onlineUser.setIsp(ma.getIsp());
            onlineUser.setSessionid(onlineUser.getId());
            onlineUser.setDatestr(new SimpleDateFormat("yyyyMMdd").format(createtime));
            onlineUser.setChannel(MainContext.ChannelTypeEnum.PHONE.toString()); // telcom
            onlineUser.setInvitetimes(0);
            onlineUser.setCreater(onlineUser.getId());
            onlineUser.setCreatetime(createtime);
            onlineUser.setUpdatetime(createtime);
            onlineUser.setOptype(MainContext.OptTypeEnum.HUMAN.toString());
            onlineUser.setAppid(channel);
        } else {
            onlineUser.setOlduser("1"); // 不是 老访客
            onlineUser.setInvitetimes(onlineUser.getInvitetimes() + 1);
        }

        onlineUser.setLogintime(createtime);
        onlineUser.setUpdatetime(createtime);
        onlineUser.setUpdateuser(onlineUser.getUsername());
        onlineUser.setSessionid(onlineUser.getId());

        // 获取该访客对应的联系人
        List<Contacts> lxrs = contactsRes.findOneByDatastatusIsFalseAndPhoneAndOrgi(visitorPhoneNumber, MainContext.SYSTEM_ORGI);
        Contacts lxr = null;
        if (lxrs.size() >= 1) {
            lxr = lxrs.get(0);
            onlineUser.setContactsid(lxr.getId());
        }

        onlineUser.setStatus(MainContext.OnlineUserOperatorStatus.ONLINE.toString());

        // save and cache
        logger.info("[callout wire] save and cache onlineUser: id [{}]", onlineUser.getId());
        onlineUserRes.save(onlineUser);
        CacheHelper.getOnlineUserCacheBean().put(onlineUser.getId(), onlineUser, MainContext.SYSTEM_ORGI);


        // 在线客服状态
//        AgentStatus agentStatus = getAgentStatus(agentId, MainContext.SYSTEM_ORGI);
//
//        if (agentStatus == null) {
//            throw new CSKefuException(String.format("[callout wire] 客服人员处于离线状态，id [%s]", agentId));
//        }
//
//        if (agentStatus.isBusy()) {
//            throw new CSKefuException(String.format("[callout wire] 客服人员在线忙碌，不进行委派，名字 [%s] id [%s]", agentStatus.getUsername(), agentId));
//        }

        // 在线客服访客咨询记录
        AgentUser agentUser = new AgentUser(onlineUser.getId(),
                MainContext.ChannelTypeEnum.PHONE.toString(), // callout
                onlineUser.getId(),
                onlineUser.getUsername(),
                MainContext.SYSTEM_ORGI,
                channel);
        agentUser.setNickname(onlineUser.getUsername());
        agentUser.setCountry(ma.getCountry()); // set source
        agentUser.setProvince(ma.getProvince());
        agentUser.setCity(ma.getCity());
        agentUser.setPhone(visitorPhoneNumber);
        agentUser.setRegion(String.format("%s [%s]", ma.getProvince(), visitorPhoneNumber));
        agentUser.setLogindate(createtime);
        agentUser.setLastgetmessage(createtime);
        agentUser.setAgent(agentId); // set Agent
        agentUser.setAgentno(agentId);
        agentUser.setStatus(MainContext.AgentUserStatusEnum.INSERVICE.toString());
        agentUser.setOnline(true);
        agentUser.setSessionid(onlineUser.getId());
        agentUser.setServicetime(new Date());

        // bind user service
        agentUser.setAgentserviceid(bindAgentService(agentUser,
                MainContext.SYSTEM_ORGI,
                channel,
                organ,
                organid,
                dp,
                ma.getCode(),
                ma.getIsp(),
                caller,
                called,
                sip,
                createtime,
                callid,
                lxr));

        agentUserRes.save(agentUser); // save and cache
        CacheHelper.getAgentUserCacheBean().put(agentUser.getId(), agentUser, MainContext.SYSTEM_ORGI);
        return agentUser;
    }

    /**
     * 访客下线
     * 设置访客离线，清缓存
     *
     */
    public void offline(final CallOutWireEvent event) throws Exception {
        OnlineUser onlineUser = onlineUserRes.findByPhoneAndOrgiAndStatus(event.getTo(),
                MainContext.SYSTEM_ORGI,
                MainContext.OnlineUserOperatorStatus.ONLINE.toString());
        Date dt = new Date();

        if (onlineUser != null) { // 已经接通的用户
            // update Online user
            CacheHelper.getOnlineUserCacheBean().delete(onlineUser.getId(), MainContext.SYSTEM_ORGI);
            onlineUser.setStatus(MainContext.OnlineUserOperatorStatus.OFFLINE.toString());
            onlineUser.setUpdatetime(dt);
            onlineUserRes.save(onlineUser);

            // there should only have one record,
            // unless bad things happen.
            List<AgentUser> agentUsers = agentUserRes.findByUseridAndStatus(onlineUser.getId(),
                    MainContext.AgentUserStatusEnum.INSERVICE.toString());
            for (AgentUser au : agentUsers) {
                CacheHelper.getAgentUserCacheBean().delete(au.getId(), MainContext.SYSTEM_ORGI);
                AutomaticServiceDist.serviceFinish(au, MainContext.SYSTEM_ORGI);
                // update Status Events，更新通话记录
                if (StringUtils.isNotBlank(au.getAgentserviceid()))
                    closeStatusEvent(au, event.getCreatetime(), event.getRecord());
            }
        } else {
            logger.info("[callout wire] 手动外呼挂断处理 callOutFail");
            callOutFail(event);
        }
    }

    /**
     * 关闭通话记录票据
     *
     * @param au
     * @param recordingfile
     */
    private void closeStatusEvent(AgentUser au, final Date endtime, final String recordingfile) {
        AgentService as = agentServiceRes.findByIdAndOrgi(au.getAgentserviceid(), au.getOrgi());
        if (as == null)
            return;

        StatusEvent se = statusEventRes.findById(as.getOwner());
        if (se != null) {
            se.setStatus(MainContext.CallServiceStatus.HANGUP.toString());
            se.setEndtime(endtime);
            se.setDuration((int) (endtime.getTime() - se.getStarttime().getTime()) / 1000);
            se.setRecordingfile(recordingfile);
            // 设置录音文件位置
            statusEventRes.save(se);
        }
    }

    public void callOutConnect(final CallOutWireEvent event) throws FreeSwitchException, CSKefuException, CallOutRuntimeException {
        if (!((event.getFrom() != null)
                && (event.getTo() != null)
                && (event.getUuid() != null)))
            throw new FreeSwitchException("[callout wire] invalid payload in callOutConnect data.");

        final String sip = event.getFrom();
        final String visitorPhoneNumber = event.getTo();
        final String channel = event.getChannel();
        // 呼叫计划，手动外呼：dialplan为null，自动外呼：dialplan不是null
        final String dialplan = event.getDialplan();
        final String callid = event.getUuid(); // FreeSwitch 连线的唯一ID, 用于监听等
        final String caller = null; // 主叫号码
        final Date createtime = event.getCreatetime();
        CallOutDialplan dp = null;
        if (dialplan != null) {
            dp = callOutDialplanRes.findOne(dialplan);
            if (dp == null)
                throw new CallOutRuntimeException(String.format("不存在该呼叫计划。 %s", dialplan));
        }

        logger.info("[callout wire] bridge to sip account: {}, phone {}, localdatetime {}.", sip, visitorPhoneNumber, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createtime));
        if (!validatePhoneNumber(visitorPhoneNumber)) {
            throw new FreeSwitchException(String.format("[callout wire] Invalid phone number [%s], should be 11 length.", visitorPhoneNumber));
        }

        // 验证语音渠道ID的有效性
        if (StringUtils.isBlank(channel))
            throw new FreeSwitchException("[callout wire] channel is blank string.");

        CousultInvite invite = OnlineUserUtils.cousult(channel, MainContext.SYSTEM_ORGI, consultInviteRes);
        if (invite == null) {
            throw new FreeSwitchException(String.format("[callout wire] Invalid channel [%s]", channel));
        }

        if (StringUtils.isNotBlank(sip)) { // 发送通知给浏览器客户端
            logger.info("[callout wire] push exchange to sip account {} ...", sip);
            List<User> users = userRes.findBySipaccountAndDatastatus(sip, false);
            if (users.size() == 0) {
                throw new FreeSwitchException(String.format("[callout wire] User does not exist for Sip Account [%s]", sip));
            } else if (users.size() > 1) {
                throw new FreeSwitchException(String.format("[callout wire] Get multi users for Sip Account [%s]", sip));
            }

            User agent = users.get(0);
            Organ organ = null;
            String organid = null;
            String organname = null;
            if (agent.getOrgan() != null) {
                organ = organRes.findOne(agent.getOrgan());
                if (organ != null)
                    organname = organ.getName();
            }

            logger.info("[callout wire] Resolve Sip Account {}: {}", sip, agent.getUsername());

            // 设置上线
            AgentUser au = online(visitorPhoneNumber,
                    agent.getId(),
                    channel,
                    organname,
                    agent.getOrgi(),
                    dp,
                    caller,
                    visitorPhoneNumber,
                    sip,
                    createtime,
                    callid);

            JsonObject payload = new JsonObject();
            payload.addProperty("type", Constants.FS_BRIDGE_CONNECT);
            payload.addProperty("phone", visitorPhoneNumber);
            payload.addProperty("userid", au.getUserid());
            payload.addProperty("username", au.getUsername());
            payload.addProperty("usession", au.getUserid());
            payload.addProperty("touser", au.getUserid());
            payload.addProperty("orgi", au.getOrgi());
            payload.addProperty("calltype", MainContext.CallTypeEnum.OUT.toString());
            payload.addProperty("channel", au.getChannel());

            /**
             * 其他情况，存在一个有效账号
             * 发送事件到浏览器
             */
            NettyClients.getInstance()
                    .sendCalloutEventMessage(agent.getId(),
                            MainContext.MessageTypeEnum.NEW.toString(),
                            payload.toString());
        } else {
            throw new FreeSwitchException(String.format("[callout wire] Sip Accout Not Found in data. %s", event.toJson().toString()));
        }
    }

    /**
     * 失败任务
     * 通话记录中，创建呼叫失败
     */
    public void callOutFail(final CallOutWireEvent event) throws CallOutRuntimeException {
        StatusEvent se = new StatusEvent();
        se.setId(MainUtils.getUUID());
        se.setStatus(event.getStatus());
        se.setDuration(0);
        se.setDirection(event.getDirection());
        se.setCalltype(event.getDirection());
        se.setVoicechannel(event.getChannel());

        // 没有呼叫计划外呼的通话被认为是手动外呼
        if (event.isDialplan()) {
            final String dialplan = event.getDialplan();
            se.setDialplan(dialplan);
            CallOutDialplan dp = callOutDialplanRes.findOne(dialplan);
            se.setOrganid(dp.getOrgan().getId());
            se.setOrgan(dp.getOrgan().getName());
            se.setVoicechannel(dp.getVoicechannel().getBaseURL());
        }

        se.setCallid(event.getUuid()); // 呼叫标识
        se.setRecordingfile(event.getRecord()); // 呼叫文件

        if (event.getFrom() != null) { // 手动外呼
            List<User> users = userRes.findBySipaccountAndDatastatus(event.getFrom(), false);
            if (users.size() == 0) {
                throw new CallOutRuntimeException(String.format("[callout wire] User does not exist for Sip Account [%s]", event.getFrom()));
            } else if (users.size() > 1) {
                throw new CallOutRuntimeException(String.format("[callout wire] Get multi users for Sip Account [%s]", event.getFrom()));
            } else {
                User u = users.get(0);
                se.setAgentname(u.getUname());
                se.setAgent(u.getId());
            }
        }

        // 被叫号码
        if (event.getTo() != null) {
            final String phone = event.getTo();
            se.setCalled(phone);
            MobileAddress ma = MobileNumberUtils.getAddress(phone);
            if (ma != null) {
                se.setProvince(ma.getProvince());
                se.setCode(ma.getCode());
                se.setCity(ma.getCity());
                se.setCountry(ma.getCountry());
                se.setIsp(ma.getIsp());
            }
        }

        se.setStarttime(event.getCreatetime());
        se.setEndtime(event.getCreatetime());
        se.setLocaldatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getCreatetime()));
        statusEventRes.save(se);
    }


    public void callOutDisconnect(final CallOutWireEvent event) throws Exception {
        String sip = event.getFrom();
        String visitorPhoneNumber = event.getTo();
        String recordingfile = event.getRecord();

        logger.info("[callout wire] callOutDisconnect sip account: {}, phone {}.", sip, visitorPhoneNumber);
        if (!validatePhoneNumber(visitorPhoneNumber)) {
            throw new FreeSwitchException(String.format("Invalid phone number [%s], should be 11 length.", visitorPhoneNumber));
        }

        if (StringUtils.isNotBlank(sip)) { // 发送通知给浏览器客户端
            List<User> users = userRes.findBySipaccountAndDatastatus(sip, false);
            if (users.size() == 0) {
                throw new FreeSwitchException(String.format("User does not exist for Sip Account [%s]", sip));
            } else if (users.size() > 1) {
                throw new FreeSwitchException(String.format("Get multi users for Sip Account [%s]", sip));
            }

            User agent = users.get(0);
            logger.info("[callout wire] callOutDisconnect resolve sip {} account {}", sip, agent.getUsername());

            // 设置离线
            offline(event);
        }

    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        logger.info("[callout wire] onMessage {}", message);
        String payload = new String(message.getBody());
        JsonParser parser = new JsonParser();
        JsonObject j = parser.parse(payload).getAsJsonObject();
        // validate message
        if (!(j.has("type")
                && j.has("to")
                && j.has("ops")
                && j.has("channel")
                && j.has("createtime"))) {
            logger.error(String.format("[callout wire] 接线数据格式不对, %s", payload));
        } else {
            try {
                CallOutWireEvent event = CallOutWireEvent.parse(j);
                switch (event.getEventType()) {
                    case 1: // 自动外呼接通
                        logger.info("[callout wire] 自动外呼接通 {}", j.toString());
                        callOutConnect(event);
                        break;
                    case 2: // 自动外呼挂断
                        logger.info("[callout wire] 自动外呼挂断 {}", j.toString());
                        callOutDisconnect(event);
                        break;
                    case 3: // 自动外呼失败
                        logger.info("[callout wire] 自动外呼失败 {}", j.toString());
                        callOutFail(event);
                        break;
                    case 4: // 手动外呼接通
                        logger.info("[callout wire] 手动外呼接通 {}", j.toString());
                        callOutConnect(event);
                        break;
                    case 5: // 手动外呼挂断
                        logger.info("[callout wire] 手动外呼挂断 {}", j.toString());
                        callOutDisconnect(event);
                        break;
                    case 6: // 手动外呼失败
                        logger.info("[callout wire] 手动外呼失败 {}", j.toString());
                        callOutFail(event);
                        break;
                    case 7: // 呼入接通
                        logger.info("[callin wire] 呼入接通    {}", j.toString());
                        break;
                    case 8: // 呼入挂断
                        logger.info("[callin wire] 呼入挂断    {}", j.toString());
                        break;
                    case 9: // 呼入失败
                        logger.info("[callin wire] 呼入失败    {}", j.toString());
                        break;
                }
            } catch (Exception e) {
                logger.error("[callout wire] ", e);
            }
        }
    }
}
