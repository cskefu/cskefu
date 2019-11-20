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
package com.chatopera.cc.basic.resource;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.JobDetail;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * @author jaddy0302 Rivulet Resource.java 2010-3-6
 * 
 */
public abstract class Resource {
	
	public static Logger log = Logger.getLogger(Resource.class.getName()) ;

	public abstract void begin()  throws Exception;
	
	
	public abstract void end(boolean clear)  throws Exception;
	/**
	 * Re connection
	 */
	public abstract JobDetail getJob();
	
	/**
	 * Re connection
	 */
	public abstract void process(OutputTextFormat meta , JobDetail job)throws Exception;

	/**
	 * synchronized
	 * Single-mode single-threaded access to records under a record
	 * 
	 * @return
	 */
	public abstract  OutputTextFormat next() throws Exception;

	/**
	 * 
	 * @return
	 */
	public abstract boolean isAvailable() ;
	
	
	/**
	 * 
	 * @return
	 */
	public abstract OutputTextFormat getText(OutputTextFormat object) throws Exception;
	
	/**
	 * 
	 */
	public abstract void rmResource() ;
	
	/**
	 * 
	 */
	public abstract void updateTask()throws Exception ;
	
	
	/**
	 * 
	 * @param job
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public static Resource getResource(JobDetail job)
			throws Exception{
		return job != null
				&& MainContext.getResource(job.getTasktype()) != null ? (Resource) MainContext
				.getResource(job.getTasktype()).getConstructor(
						new Class[] { JobDetail.class }).newInstance(
						new Object[] { job })
				: null;

	}
	
		
	/**
	 * Filter
	 * @param file
	 * @param netFile
	 * @return
	 */
	public boolean val(String inputFile , String acceptDocType){
		String file = inputFile!=null ? inputFile.toLowerCase() :null ;
		return file!=null && acceptDocType!=null && ((acceptDocType.indexOf(file.substring(file.lastIndexOf(".")+1))>=0||acceptDocType.indexOf("all")>=0)) ;
	}
	
}
