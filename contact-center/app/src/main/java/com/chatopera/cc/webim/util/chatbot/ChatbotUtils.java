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
package com.chatopera.cc.webim.util.chatbot;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashSet;

public class ChatbotUtils {
    public static final HashSet<String> VALID_LANGS = new HashSet<String>(Arrays.asList(new String[]{"zh_CN", "en_US"}));
    public static final String CHATBOT_FIRST = "客服机器人优先";
    public static final String HUMAN_FIRST = "人工客服优先";
    public static final HashSet<String> VALID_WORKMODELS = new HashSet<String>(Arrays.asList(new String[]{CHATBOT_FIRST, HUMAN_FIRST}));

    public static final String SNS_TYPE_WEBIM = "webim";


    /**
     * 使用snsid得到ChatbotID
     *
     * @param snsid
     * @return
     */
    public static String resolveChatbotIDWithSnsid(String snsid, String clientId) {
        return (clientId + "_" + snsid).toLowerCase();
    }

    /**
     * 使用chatbotID得到snsid
     *
     * @param chatbotID
     * @return
     */
    public static String resolveSnsidWithChatbotID(String chatbotID, String clientId) {
        return StringUtils.remove(chatbotID, clientId.toLowerCase() + "_");
    }
}
