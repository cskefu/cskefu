/*
 * Copyright (C) 2019 Chatopera Inc, <https://www.chatopera.com>
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

import com.chatopera.cc.basic.MainUtils;
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