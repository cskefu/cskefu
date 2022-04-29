/*
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.interceptor;

import com.chatopera.cc.basic.MainContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewsInterceptorHandler extends HandlerInterceptorAdapter {
    private final static Logger logger = LoggerFactory.getLogger(ViewsInterceptorHandler.class);

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse response, Object arg2,
                           ModelAndView view) throws Exception {
        if ((view != null) && !StringUtils.equals(view.getViewName(), "redirect:/")) {
            view.addObject("models", MainContext.getModules());
        }
    }
}