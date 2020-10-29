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
package com.chatopera.cc.controller.api.request;

import org.springframework.http.HttpHeaders;

public class RestUtils {
    public final static String RESP_KEY_RC = "rc";
    public final static String RESP_KEY_MSG = "msg";
    public final static String RESP_KEY_DATA = "data";
    public final static String RESP_KEY_ERROR = "error";
    public final static int RESP_RC_SUCC = 0;
    public final static int RESP_RC_FAIL_1 = 1;
    public final static int RESP_RC_FAIL_2 = 2;
    public final static int RESP_RC_FAIL_3 = 3;
    public final static int RESP_RC_FAIL_4 = 4;
    public final static int RESP_RC_FAIL_5 = 5;
    public final static int RESP_RC_FAIL_6 = 6;
    public final static int RESP_RC_FAIL_7 = 7;
    public final static int RESP_RC_FAIL_8 = 8;
    public final static int RESP_RC_FAIL_9 = 9;
    public final static int RESP_RC_FAIL_10 = 10;
    public final static int RESP_RC_FAIL_11 = 11;
    public final static int RESP_RC_FAIL_12 = 12;
    public final static int RESP_RC_FAIL_13 = 13;
    public final static int RESP_RC_FAIL_14 = 14;
    public final static int RESP_RC_FAIL_15 = 15;
    public final static int RESP_RC_FAIL_16 = 16;
    public final static int RESP_RC_FAIL_17 = 17;
    public final static int RESP_RC_FAIL_18 = 18;
    public final static int RESP_RC_FAIL_19 = 19;
    public final static int RESP_RC_FAIL_20 = 20;

//    /**
//     * 复制请求处理结果
//     * @param source
//     * @param target
//     */
//    public static Map<String, String> pack(Map<String, String> source, Map<String, String> target){
//        if(source.get(RESP_KEY_RC).equals(RESP_RC_SUCC)){
//            target.put("rc", RESP_RC_SUCC);
//            target.put("msg", source.get(RESP_KEY_MSG));
//        } else if(StringUtils.isNotBlank(source.get(RESP_KEY_RC))) {
//            target.put("rc", source.get(RESP_KEY_RC));
//            target.put("error", source.get(RESP_KEY_ERROR));
//        }
//        return target;
//    }

    public static HttpHeaders header() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}
