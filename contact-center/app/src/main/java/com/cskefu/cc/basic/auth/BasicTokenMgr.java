/**
 * Copyright 2023 Chatopera Inc. <https://www.chatopera.com>. All rights reserved.
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
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
