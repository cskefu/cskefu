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
package com.chatopera.cc.basic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainContextTest extends TestCase {
    private final static Logger logger = LoggerFactory.getLogger(MainContextTest.class);

    public MainContextTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MainContextTest.class);
    }

    public void testChannelTypeEnum() {
        logger.info("[testChannelTypeEnum] toString {}", MainContext.ChannelType.WEBIM);
        logger.info("[testChannelTypeEnum] valueOf {} ", MainContext.ChannelType.toValue("webim"));

        assertEquals(MainContext.ChannelType.WEBIM.toString(), "webim");
        assertEquals(MainContext.ChannelType.toValue("webim"), MainContext.ChannelType.WEBIM);
    }

}
