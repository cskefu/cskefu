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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class FFmpegCmdExecuter {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FFmpegCmdExecuter.class);
	
	public static String FFMPEG = "ffmpeg";
	
	/**
	 * mp3 to amr
	 * @param src
	 * @param dest
	 */
	public static void mp3ToAmr(String src ,String dest){
		logger.info("mp3 to amr:" + dest);
		List<String> cmd = new ArrayList<String>();
		cmd.add(FFMPEG);
		cmd.add("-i");
		cmd.add(src);
		cmd.add("-ac");
		cmd.add("1");
		cmd.add("-ar");
		cmd.add("8000");
		cmd.add(dest);
		exec(cmd);
	}
	/**
	 * wav to mp3
	 * @param src
	 * @param dest
	 */
	public static void wavToMp3(String src,String dest){
		logger.info("wav to mp3:"+dest);
		List<String> cmd = new ArrayList<String>();
		cmd.add(FFMPEG);
		cmd.add("-i");
		cmd.add(src);
		cmd.add("-acodec");
		cmd.add("libmp3lame");
		cmd.add(dest);
		exec(cmd);
	}
	
	/**
	 * amr to mp3
	 * @param src
	 * @param dest
	 */
	public static void amrToMp3(String src ,String dest){
		logger.info("amr to mp3:"+dest);
		List<String> cmd = new ArrayList<String>();
		cmd.add(FFMPEG);
		cmd.add("-i");
		cmd.add(src);
		cmd.add(dest);
		exec(cmd);
	}
	
	/**
	 * ִ��ָ��
	 * @param cmd ִ��ָ��
	 */
	public static void exec( List<String> cmd){
		BufferedReader stdout = null;
		try {
			ProcessBuilder builder = new ProcessBuilder();	
	        builder.command(cmd);
	        builder.redirectErrorStream(true);
	        Process proc = builder.start();
	        stdout = new BufferedReader(
	        		new InputStreamReader(proc.getInputStream()));
	        String line;
	        while ((line = stdout.readLine()) != null) {
	        	logger.debug(line);
			}
	        proc.waitFor();   
		} catch (Exception e) {
            e.printStackTrace();
        }finally{
        	IOUtils.closeQuietly(stdout);
        }
	}
	
	
	
	
	/**
	 * ���ˮӡͼƬ����Ƶ��ˮӡ
	 * @param src
	 * @param dest
	 * @param water
	 * @param wmPosition
	 * @param alpha
	 * @param wmPosition
	 * @return
	 */
     public static void videoWater(String src,String dest,String water,int wmPosition, float alpha,String platform) {  
    	List<String> cmd = new ArrayList<String>();
 		cmd.add(FFMPEG);
 		cmd.add("-i");
 		cmd.add(src);
 		cmd.add("-i");
 		cmd.add(water);
 		cmd.add("-filter_complex");
 		cmd.add("''overlay=main_w-overlay_w:main_h-overlay_h''");
 		cmd.add("-strict");
 		cmd.add("-2");
 		if(NumberUtils.toInt(platform, 0)==3){
 			cmd.add("-ar");
 			cmd.add("8000");
 		}
		cmd.add("-b");
 		cmd.add("877k");
 		cmd.add("-qscale");
 		cmd.add("0.01");
 		//ios�����
 		cmd.add("-movflags");
		cmd.add("faststart");
 		cmd.add(dest);
 		exec(cmd);
     }  
     
     /**
      * ���ˮӡͼƬ����Ƶ��ˮӡ
      * @param src
      * @param dest
      */
      public static void videoPic(String src,String dest) {  
     	List<String> cmd = new ArrayList<String>();
  		cmd.add(FFMPEG);
  		cmd.add("-i");
  		cmd.add(src);
  		cmd.add("-ss");
  		cmd.add("-00:00:01");
  		cmd.add("-vframes");
  		cmd.add("1");
  		cmd.add(dest);
  		exec(cmd);
      }  
}





