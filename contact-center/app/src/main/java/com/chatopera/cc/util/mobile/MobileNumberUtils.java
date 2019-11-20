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
package com.chatopera.cc.util.mobile;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MobileNumberUtils {
	private static final Logger logger = LoggerFactory.getLogger(MobileNumberUtils.class);
	private static Map<String , MobileAddress> mobileAddressMap  = new HashMap<String ,MobileAddress>();
	private static boolean isInited = false;
	
	public static int init() throws IOException{
		File file = new File( MobileNumberUtils.class.getResource("/config/mobile.data").getFile());
        logger.info("init with file [{}]", file.getAbsolutePath());
		if(file.exists()){
			FileInputStream reader = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(reader , "UTF-8");
			BufferedReader bf = new BufferedReader(isr);
			try{
				String data = null ;
				while((data = bf.readLine()) != null){
					String[] group = data.split("[\t ]") ;
					MobileAddress address = null ;
					if(group.length == 5){
						address = new MobileAddress(group[0], group[1], group[2], group[3],group[4]) ;
					}else if(group.length == 4){
						address = new MobileAddress(group[0], group[1], group[2], group[2],group[3]) ;
					}
					if(address!=null){
						if(mobileAddressMap.get(address.getCode()) == null){
							mobileAddressMap.put(address.getCode(), address) ;
						}
						if(mobileAddressMap.get(address.getAreacode()) == null){
							mobileAddressMap.put(address.getAreacode(), address) ;
						}
					}
				}
				isInited = true;
				logger.info("inited successfully, map size [{}]", mobileAddressMap.size());
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				bf.close();
				isr.close();
				reader.close();
			}
		}
		return mobileAddressMap.size() ;
	}
	/**
	 * 根据呼入号码 找到对应 城市 , 需要传入的号码是 手机号 或者 固话号码，位数为 11位
	 * @param phoneNumber
	 * @return
	 */
	public static MobileAddress getAddress(String phoneNumber){
	    if(!isInited){
            try {
                MobileNumberUtils.init();
            } catch (IOException e) {
                logger.error("getAddress error: ", e);
                e.printStackTrace();
            }
        }

		String code = "";
		if(!StringUtils.isBlank(phoneNumber) && phoneNumber.length() > 10){
			if(phoneNumber.startsWith("0")){
				code = phoneNumber.substring(0 ,  4) ;
			}else if(phoneNumber.startsWith("1")){
				code = phoneNumber.substring(0 , 7) ;
			}
		}
		return mobileAddressMap.get(code) ;
	}
}
