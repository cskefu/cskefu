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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ViewsInterceptorHandler implements HandlerInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(ViewsInterceptorHandler.class);

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse response, Object arg2,
                           ModelAndView view) throws Exception {
        if ((view != null) && !StringUtils.equals(view.getViewName(), "redirect:/")) {
            view.addObject("models", MainContext.getModules());
        }
    }
}