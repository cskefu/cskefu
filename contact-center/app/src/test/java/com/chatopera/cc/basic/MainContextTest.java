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
package com.chatopera.cc.basic;

import com.cskefu.cc.basic.MainContext;
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
