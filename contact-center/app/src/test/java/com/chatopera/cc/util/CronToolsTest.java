/* 
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-2022 Chatopera Inc, <https://www.chatopera.com>, 
 * Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.chatopera.cc.util;

import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.util.CronTools;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class CronToolsTest {

    @Test
    public void getFinalFireTime() {
        try {
            System.out.println(MainUtils.dateFormate.format(CronTools.getFinalFireTime("0 0/40 0/1 * * ?",new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}