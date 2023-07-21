/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.util;

import it.sauronsoftware.jave.*;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import java.io.File;

public class AMRConvert {

	public static void mp3(File source , File target) throws IllegalArgumentException, EncoderException {
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