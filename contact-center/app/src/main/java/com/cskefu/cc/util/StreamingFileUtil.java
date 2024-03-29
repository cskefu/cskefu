/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.cskefu.cc.util;

import com.cskefu.cc.basic.Constants;

import java.util.Arrays;
import java.util.HashMap;

public class StreamingFileUtil {

    private static final StreamingFileUtil singleton = new StreamingFileUtil();

    private final HashMap<String, String> extMap = new HashMap<>();

    private StreamingFileUtil() {
        extMap.put(Constants.ATTACHMENT_TYPE_IMAGE, "gif,jpg,jpeg,png,bmp");
        extMap.put(Constants.ATTACHMENT_TYPE_FILE, "pdf,doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,c66");
        extMap.put("flash", "swf,flv");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
    }

    public static StreamingFileUtil getInstance() {
        return singleton;
    }

    /**
     * Validate file format
     * @param type
     * @param filename
     * @return
     */
    public String validate(final String type, final String filename) {
        final String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.<String>asList(extMap.get(type).split(",")).contains(ext)) {
            return "上传文件扩展名是不允许的扩展名。只允许" + extMap.get(type) + "格式。";
        }
        return null;
    }

}
