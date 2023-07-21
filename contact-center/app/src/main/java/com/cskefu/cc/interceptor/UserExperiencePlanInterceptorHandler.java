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
package com.cskefu.cc.interceptor;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.cache.RedisCommand;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户体验计划
 */
public class UserExperiencePlanInterceptorHandler implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(UserExperiencePlanInterceptorHandler.class);
    public final static String FLAG_KEY = "cskefu:global:user-experience-plan";
    private static RedisCommand redis;
    public static final String USER_EXP_PLAN_ON = "on";
    public static final String USER_EXP_PLAN_OFF = "off";
    private static final String USER_EXP_TELEMETRY = "userExpTelemetry";

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object arg2,
            ModelAndView view) {

        if (!request.getMethod().equals("GET")) {
            return;
        }

        if (view == null) {
            return;
        }

        String flag = getRedis().get(FLAG_KEY);
        if (StringUtils.isEmpty(flag)) {
            // flag is empty, add init value
            flag = USER_EXP_PLAN_ON;
            getRedis().put(FLAG_KEY, USER_EXP_PLAN_ON);
        }

//        logger.info("flag {}", flag);
        if (StringUtils.equalsIgnoreCase(USER_EXP_PLAN_OFF, flag)) {
            view.addObject(USER_EXP_TELEMETRY, USER_EXP_PLAN_OFF);
        } else {
            view.addObject(USER_EXP_TELEMETRY, USER_EXP_PLAN_ON);
        }
    }

    /**
     * Get redis bean
     *
     * @return
     */
    private static RedisCommand getRedis() {
        if (redis == null) {
            redis = MainContext.getContext().getBean(RedisCommand.class);
        }
        return redis;
    }

}
