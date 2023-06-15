/*
 * Copyright (C) 2019-2020 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.proxy;

import com.cskefu.cc.model.LeaveMsg;
import com.cskefu.cc.model.Channel;
import com.cskefu.cc.persistence.repository.ChannelRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveMsgProxy {
    private final static Logger logger = LoggerFactory.getLogger(LeaveMsgProxy.class);

    @Autowired
    private ChannelRepository snsAccountRes;

    /**
     * 根据snsId获得渠道信息
     *
     * @param leaveMsg
     */
    public void resolveChannelBySnsid(final LeaveMsg leaveMsg) {
        if (StringUtils.isNotBlank(leaveMsg.getSnsId())) {
            snsAccountRes.findBySnsid(leaveMsg.getSnsId()).ifPresent(leaveMsg::setChannel);
        } else {
            leaveMsg.setChannel(new Channel());
        }

        if (leaveMsg.getChannel() != null && StringUtils.isNotBlank(leaveMsg.getChannel().getName())) {
        } else {
        }
    }
}
