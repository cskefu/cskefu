/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.util;

import com.cskefu.cc.model.PropertiesEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PropertiesEventUtil {
	public static List<PropertiesEvent> processPropertiesModify(HttpServletRequest request , Object newobj , Object oldobj,String... ignoreProperties){
		List<PropertiesEvent> events = new ArrayList<PropertiesEvent>() ;
		//
		String[] fields = new String[]{"id" , "orgi" , "creater" ,"createtime" , "updatetime"} ; 
		List<String> ignoreList = new ArrayList<String>(); 
		ignoreList.addAll(Arrays.asList(fields)) ;
		if((ignoreProperties != null)){
			ignoreList.addAll(Arrays.asList(ignoreProperties));
		}
		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(newobj.getClass());
		for (PropertyDescriptor targetPd : targetPds) {  
	        Method newReadMethod = targetPd.getReadMethod();  
	        if (oldobj!=null && newReadMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {  
	            PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(oldobj.getClass(), targetPd.getName());  
	            if (sourcePd != null && !targetPd.getName().equalsIgnoreCase("id")) {  
	                Method readMethod = sourcePd.getReadMethod();  
	                if (readMethod != null) {  
	                    try {  
	                    	Object newValue = readMethod.invoke(newobj);
	                        Object oldValue = readMethod.invoke(oldobj);
	                        
	                        if(newValue != null && !newValue.equals(oldValue)){
	                        	PropertiesEvent event = new PropertiesEvent();
	                        	event.setField(targetPd.getName());
	                        	event.setCreatetime(new Date());
	                        	event.setName(targetPd.getName());
	                        	event.setPropertity(targetPd.getName());
	                        	event.setOldvalue(oldValue!=null && oldValue.toString().length()<100 ? oldValue.toString() : null);
	                        	event.setNewvalue(newValue!=null && newValue.toString().length()<100 ? newValue.toString() : null);
	                        	if(request!=null && !StringUtils.isBlank(request.getParameter(targetPd.getName()+".text"))){
	                        		event.setTextvalue(request.getParameter(targetPd.getName()+".text"));
	                        	}
	                        	events.add(event) ;
	                        }
	                    }  
	                    catch (Throwable ex) {  
	                        throw new FatalBeanException(  
	                                "Could not copy property '" + targetPd.getName() + "' from source to target", ex);  
	                    }  
	                }  
	            }  
	        }  
	    }  
		return events ;
	}
}
