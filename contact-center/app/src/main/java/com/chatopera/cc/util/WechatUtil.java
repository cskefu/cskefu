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


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 感谢网友提供 微信表情转换代码
 * @author admin
 *
 */
public class WechatUtil {

	private static Logger logger = LoggerFactory.getLogger(WechatUtil.class);
	private static String regex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::*|/:@x|/:8*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
	private static Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	
	private static Pattern patternFaces = Pattern.compile("(<img[\\S\\s]*?src=\"[\\S\\s]*?/emoticons/images/([\\d]{1,}).gif\"[\\s\\S]*?/>)", Pattern.CASE_INSENSITIVE);
	
	private static Map<String,String> emotions = new HashMap<>();
	private static Map<String,String> faces = new HashMap<>();
	static{
		emotions.put("/::)","0");
		emotions.put("/::~","1");
		emotions.put("/::B","2");
		emotions.put("/::|","3");
		emotions.put("/:8-)","4");
		emotions.put("/::<","5");
		emotions.put("/::$","6");
		emotions.put("/::X","7");
		emotions.put("/::Z","8");
		emotions.put("/::'(","9");
		emotions.put("/::-|","10");
		emotions.put("/::@","11");
		emotions.put("/::P","12");
		emotions.put("/::D","13");
		emotions.put("/::O","14");
		emotions.put("/::(","15");
		emotions.put("/::+","16");
		emotions.put("/:--b","17");
		emotions.put("/::Q","18");
		emotions.put("/::T","19");
		emotions.put("/:,@P","20");
		emotions.put("/:,@-D","21");
		emotions.put("/::d","22");
		emotions.put("/:,@o","23");
		emotions.put("/::g","24");
		emotions.put("/:|-)","25");
		emotions.put("/::!","26");
		emotions.put("/::L","27");
		emotions.put("/::>","28");
		emotions.put("/::,@","29");
		emotions.put("/:,@f","30");
		emotions.put("/::-S","31");
		emotions.put("/:?","32");
		emotions.put("/:,@x","33");
		emotions.put("/:,@@","34");
		emotions.put("/::8","35");
		emotions.put("/:,@!","36");
		emotions.put("/:!!!","37");
		emotions.put("/:xx","38");
		emotions.put("/:bye","39");
		emotions.put("/:wipe","40");
		emotions.put("/:dig","41");
		emotions.put("/:handclap","42");
		emotions.put("/:&-(","43");
		emotions.put("/:B-)","44");
		emotions.put("/:<@","45");
		emotions.put("/:@>","46");
		emotions.put("/::-O","47");
		emotions.put("/:>-|","48");
		emotions.put("/:P-(","49");
		emotions.put("/::'|","50");
		emotions.put("/:X-)","51");
		emotions.put("/::*","52");
		emotions.put("/:@x","53");
		emotions.put("/:8*","54");
		emotions.put("/:pd","55");
		emotions.put("/:<W>","56");
		emotions.put("/:beer","57");
		emotions.put("/:basketb","58");
		emotions.put("/:oo","59");
		emotions.put("/:coffee","60");
		emotions.put("/:eat","61");
		emotions.put("/:pig","62");
		emotions.put("/:rose","63");
		emotions.put("/:fade","64");
		emotions.put("/:showlove","65");
		emotions.put("/:heart","66");
		emotions.put("/:break","67");
		emotions.put("/:cake","68");
		emotions.put("/:li","69");
		emotions.put("/:bome","70");
		emotions.put("/:kn","71");
		emotions.put("/:footb","72");
		emotions.put("/:ladybug","73");
		emotions.put("/:shit","74");
		emotions.put("/:moon","75");
		emotions.put("/:sun","76");
		emotions.put("/:gift","77");
		emotions.put("/:hug","78");
		emotions.put("/:strong","79");
		emotions.put("/:weak","80");
		emotions.put("/:share","81");
		emotions.put("/:v","82");
		emotions.put("/:@)","83");
		emotions.put("/:jj","84");
		emotions.put("/:@@","85");
		emotions.put("/:bad","86");
		emotions.put("/:lvu","87");
		emotions.put("/:no","88");
		emotions.put("/:ok","89");
		emotions.put("/:love","90");
		emotions.put("/:<L>","91");
		emotions.put("/:jump","92");
		emotions.put("/:shake","93");
		emotions.put("/:<O>","94");
		emotions.put("/:circle","95");
		emotions.put("/:kotow","96");
		emotions.put("/:turn","97");
		emotions.put("/:skip","98");
		emotions.put("/:oY","99");
		emotions.put("/:#-0","100");
		emotions.put("/:hiphot","101");
		emotions.put("/:kiss","102");
		emotions.put("/:<&","103");
		emotions.put("/:&>","104");
		
		for (Map.Entry<String, String> entry : emotions.entrySet()) {
			faces.put(entry.getValue(), entry.getKey());
		}

	
	}
	public static String setTagIds(Integer[] tagIds){
		if(tagIds==null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Integer integer : tagIds) {
			sb.append(integer.toString());
			sb.append(",");
		}
		return sb.toString();
	}
	
	public static Integer[] getTagIds(String tagIds){
		if(tagIds==null || StringUtils.isBlank(tagIds)){
			return null;
		}
		String[] tags = tagIds.split(",");
		Integer[] tagarr = new Integer[tags.length];
		for (int i = 0; i < tagarr.length; i++) {
			tagarr[i] = NumberUtils.toInt(tags[i]);
		}
		return tagarr;
	}
	
	/**
	 * 微信表情内容转成LayIM表情格式
	 * @param content
	 * @return
	 */
	public static String wechatToUCKeFuface(String content){
		if(content==null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Matcher m =  pattern.matcher(content);
		while(m.find()){
			//此里面的可以替换为配置
			
			String face = emotions.get(m.group());
			if(face!=null){
				m.appendReplacement(sb, "<img src=\"/js/kindeditor/plugins/emoticons/images/"+emotions.get(m.group())+".gif\" border=\"0\" alt=\"\" />");
			}else{
				logger.warn("wechatToUCKeFuface not find:{}",m.group());
			}
			
			
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	
	public static String ucKeFufaceTowechat(String content){
		if(content==null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Matcher m =  patternFaces.matcher(content);
		while(m.find()){
			String face = faces.get(m.group(2));
			if(face!=null){
				m.appendReplacement(sb, faces.get(m.group(2)));
			}else{
				logger.warn("ucKeFufaceTowechat not find:{}",m.group());
			}
		}
		m.appendTail(sb);
		return sb.toString().replaceAll("<br[ ]{1,}/>|&nbsp;", "");
	}
}
