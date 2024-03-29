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
package com.cskefu.cc.socketio.message;

import com.cskefu.cc.basic.MainContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**
 * 坐席会话监控消息
 */
public class InterventMessage implements java.io.Serializable {

    private String supervisorid;      // 监控者用户ID
    private String agentuserid;       // 访客坐席会话ID
    private MainContext.InterventMessageType msgtype;           // 消息类型
    private String content;
    private JsonObject extra;
    private String session;           // 登录会话ID

    public String getSupervisorid() {
        return supervisorid;
    }

    public void setSupervisorid(String supervisorid) {
        this.supervisorid = supervisorid;
    }

    public String getAgentuserid() {
        return agentuserid;
    }

    public void setAgentuserid(String agentuserid) {
        this.agentuserid = agentuserid;
    }

    public MainContext.InterventMessageType getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(MainContext.InterventMessageType msgtype) {
        this.msgtype = msgtype;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JsonObject getExtra() {
        return extra;
    }

    public void setExtra(JsonObject extra) {
        this.extra = extra;
    }

    /**
     * 检查必填项
     *
     * @return
     */
    public boolean valid() {
        if (StringUtils.isBlank(this.agentuserid)) {
            return false;
        }

        if (StringUtils.isBlank(this.supervisorid)) {
            return false;
        }

        if (StringUtils.isBlank(this.session)) {
            return false;
        }

        if (this.msgtype == null) {
            return false;
        }

        switch (this.msgtype) {
            case TEXT:
                if (StringUtils.isBlank(this.content)) {
                    return false;
                }
        }

        return true;
    }

    /**
     * 转为Json String
     *
     * @return
     * @throws JsonProcessingException
     */
    public JsonObject toJsonObject() throws JsonProcessingException {
        // Creating Object of ObjectMapper define in Jakson Api
        ObjectMapper mapper = new ObjectMapper();
        return (new JsonParser()).parse(mapper.writeValueAsString(this)).getAsJsonObject();
    }

    public MainContext.MediaType toMediaType() {
        switch (this.msgtype) {
            case TEXT:
                return MainContext.MediaType.TEXT;
            case STATUS:
                return MainContext.MediaType.COOPERATION;
            case FILE:
                return MainContext.MediaType.FILE;
        }
        throw new IllegalArgumentException("Not found matched media type");
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
