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
package com.chatopera.cc.util;

import org.apache.commons.beanutils.converters.DateTimeConverter;

import java.util.Date;
  
public class DateConverter extends DateTimeConverter {  
  
    public DateConverter() {  
    }  
  
    public DateConverter(Object defaultValue) {  
        super(defaultValue);  
    }  
  
    /* (non-Javadoc) 
     * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType() 
     */  
    @SuppressWarnings("rawtypes")  
    protected Class getDefaultType() {  
        return Date.class;  
    }  
  
    /* 
     * (non-Javadoc) 
     * @see org.apache.commons.beanutils.converters.DateTimeConverter#convertToType(java.lang.Class, java.lang.Object) 
     */  
    @SuppressWarnings("rawtypes")  
    @Override  
    protected Object convertToType(Class arg0, Object arg1) throws Exception {  
        if (arg1 == null) {  
            return null;  
        }  
        String value = arg1.toString().trim();  
        if (value.length() == 0) {  
            return null;  
        }  
        return super.convertToType(arg0, arg1);  
    }  
}  