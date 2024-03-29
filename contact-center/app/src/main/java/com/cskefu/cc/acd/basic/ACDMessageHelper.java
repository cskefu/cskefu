/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.cskefu.cc.acd.basic;

import com.cskefu.cc.acd.ACDPolicyService;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.AgentService;
import com.cskefu.cc.model.AgentUser;
import com.cskefu.cc.model.SessionConfig;
import com.cskefu.cc.util.IP;
import com.cskefu.cc.util.IPTools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ACDMessageHelper {
    private final static Logger logger = LoggerFactory.getLogger(ACDMessageHelper.class);

    @Autowired
    private ACDPolicyService acdPolicyService;


    /**
     * 通过 AgentUser获得ComposeContext
     *
     * @param agentUser
     * @param isInvite
     * @param initiator
     * @return
     */
    public ACDComposeContext getComposeContextWithAgentUser(final AgentUser agentUser, final boolean isInvite, final String initiator) {
        ACDComposeContext ctx = new ACDComposeContext();
        ctx.setOnlineUserId(agentUser.getUserid());
        ctx.setOnlineUserNickname(agentUser.getNickname());
        ctx.setOrganid(agentUser.getSkill());
        ctx.setChannelType(agentUser.getChanneltype());
        ctx.setAgentno(agentUser.getAgentno());
        ctx.setBrowser(agentUser.getBrowser());
        ctx.setOsname(agentUser.getOsname());
        ctx.setAppid(agentUser.getAppid());
        ctx.setTitle(agentUser.getTitle());
        ctx.setSessionid(agentUser.getSessionid());
        ctx.setUrl(agentUser.getUrl());
        ctx.setOwnerid(agentUser.getOwner());

        if (StringUtils.isNotBlank(agentUser.getIpaddr())) {
            ctx.setIp(agentUser.getIpaddr());
            // TODO set IP Data
            ctx.setIpdata(IPTools.getInstance().findGeography(agentUser.getIpaddr()));
        }

        ctx.setInvite(isInvite);
        ctx.setInitiator(initiator);

        return ctx;
    }

    /**
     * 通知消息内容：分配到坐席
     *
     * @param agentService
     * @param channel
     * @return
     */
    public String getSuccessMessage(AgentService agentService, String channel) {
        String queneTip = "<span id='agentno'>" + agentService.getAgentusername() + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = agentService.getAgentusername();
        }
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(agentService.getSkill());
        String successMsg = "坐席分配成功，" + queneTip + "为您服务。";
        if (StringUtils.isNotBlank(sessionConfig.getSuccessmsg())) {
            successMsg = sessionConfig.getSuccessmsg().replaceAll("\\{agent\\}", queneTip);
        }
        return successMsg;
    }

    /**
     * 通知消息内容：和坐席断开
     *
     * @param channel
     * @return
     */
    public String getServiceFinishMessage(String channel, String organid) {
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(organid);
        String queneTip = "坐席已断开和您的对话";
        if (StringUtils.isNotBlank(sessionConfig.getFinessmsg())) {
            queneTip = sessionConfig.getFinessmsg();
        }
        return queneTip;
    }


    /**
     * 通知消息内容：和坐席断开，刷新页面
     *
     * @param channel
     * @return
     */
    public String getServiceOffMessage(String channel, String organid) {
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(organid);
        String queneTip = "坐席已断开和您的对话，刷新页面为您分配新的坐席";
        if (StringUtils.isNotBlank(sessionConfig.getFinessmsg())) {
            queneTip = sessionConfig.getFinessmsg();
        }
        return queneTip;
    }

    public String getNoAgentMessage(int queneIndex, String channel, String organid) {
        if (queneIndex < 0) {
            queneIndex = 0;
        }
        String queneTip = "<span id='queneindex'>" + queneIndex + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = String.valueOf(queneIndex);
        }
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(organid);
        String noAgentTipMsg = "坐席全忙，已进入等待队列，您也可以在其他时间再来咨询。";
        if (StringUtils.isNotBlank(sessionConfig.getNoagentmsg())) {
            noAgentTipMsg = sessionConfig.getNoagentmsg().replaceAll("\\{num\\}", queneTip);
        }
        return noAgentTipMsg;
    }

    public String getQueneMessage(int queneIndex, String channel, String organid) {

        String queneTip = "<span id='queneindex'>" + queneIndex + "</span>";
        if (!MainContext.ChannelType.WEBIM.toString().equals(channel)) {
            queneTip = String.valueOf(queneIndex);
        }
        SessionConfig sessionConfig = acdPolicyService.initSessionConfig(organid);
        String agentBusyTipMsg = "正在排队，请稍候,在您之前，还有  " + queneTip + " 位等待用户。";
        if (StringUtils.isNotBlank(sessionConfig.getAgentbusymsg())) {
            agentBusyTipMsg = sessionConfig.getAgentbusymsg().replaceAll("\\{num\\}", queneTip);
        }
        return agentBusyTipMsg;
    }


    /**
     * 构建WebIM分发的Context
     *
     * @param onlineUserId
     * @param nickname
     * @param session
     * @param appid
     * @param ip
     * @param osname
     * @param browser
     * @param headimg
     * @param ipdata
     * @param channel
     * @param skill
     * @param agent
     * @param title
     * @param url
     * @param traceid
     * @param ownerid
     * @param isInvite
     * @param initiator
     * @return
     */
    public static ACDComposeContext getWebIMComposeContext(
            final String onlineUserId,
            final String nickname,
            final String session,
            final String appid,
            final String ip,
            final String osname,
            final String browser,
            final String headimg,
            final IP ipdata,
            final String channel,
            final String skill,
            final String agent,
            final String title,
            final String url,
            final String traceid,
            final String ownerid,
            final boolean isInvite,
            final String initiator) {
        logger.info(
                "[enqueueVisitor] user {}, appid {}, agent {}, skill {}, nickname {}, initiator {}, isInvite {}",
                onlineUserId,
                appid,
                agent,
                skill,
                nickname, initiator, isInvite);

        // 坐席服务请求，分配 坐席
        final ACDComposeContext ctx = new ACDComposeContext();
        ctx.setOnlineUserId(onlineUserId);
        ctx.setOnlineUserNickname(nickname);
        ctx.setOrganid(skill);
        ctx.setChannelType(channel);
        ctx.setAgentno(agent);
        ctx.setBrowser(browser);
        ctx.setOsname(osname);
        ctx.setAppid(appid);
        ctx.setTitle(title);
        ctx.setSessionid(session);
        ctx.setUrl(url);
        ctx.setOnlineUserHeadimgUrl(headimg);
        ctx.setTraceid(traceid);
        ctx.setOwnerid(ownerid);
        ctx.setInitiator(initiator);
        ctx.setIpdata(ipdata);
        ctx.setIp(ip);
        ctx.setInvite(isInvite);
        return ctx;
    }

}
