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

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;


public class CronTools {
    /**
     * @param crontabExp
     * @return
     * @throws ParseException
     */

    public static CronExpression getFireTime(String crontabExp) throws ParseException {
        return new CronExpression(crontabExp);

    }

    /**
     * @param crontabExp
     * @return
     * @throws ParseException
     */

    public static Date getFinalFireTime(String crontabExp, Date date) throws ParseException {
        CronExpression expression = new CronExpression(crontabExp);
        return expression.getNextValidTimeAfter(date != null ? date : new Date());

    }
}
