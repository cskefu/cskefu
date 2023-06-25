/**
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright Jun. 2023 Chatopera Inc. <https://www.chatopera.com>. All rights reserved.
 */
package com.cskefu.cc.basic.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BasicTokenMgr {
    final static Logger logger = LoggerFactory.getLogger(BasicTokenMgr.class);

    /**
     * Generate basic token with username and password
     *
     * @param username
     * @param password
     * @return
     */
    public String generate(final String username, final String password) {
        return null;
    }
}
