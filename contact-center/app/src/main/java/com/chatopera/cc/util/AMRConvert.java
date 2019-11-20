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

import it.sauronsoftware.jave.*;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import java.io.File;

public class AMRConvert {

	public static void mp3(File source , File target) throws IllegalArgumentException, InputFormatException, EncoderException {
		AudioAttributes audio = new AudioAttributes();
		Encoder encoder = new Encoder();


		audio.setCodec("libmp3lame");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);

		encoder.encode(source, target, attrs);
	}
	
	public static int getMp3TrackLength(File mp3File) {  
	    try {  
	        MP3File f = (MP3File) AudioFileIO.read(mp3File);  
	        MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();  
	        return audioHeader.getTrackLength();  
	    } catch(Exception e) {  
	    	e.printStackTrace();
	        return 0;  
	    }  
	}  
}