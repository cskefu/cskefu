/*
 * Copyright (C) 2022 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */
package com.cskefu.cc.basic;

import javax.annotation.PreDestroy;

import com.chatopera.cc.BlessingAndUnblessing;

public class TerminateBean {
	@PreDestroy
	public void onDestroy() throws Exception {
		BlessingAndUnblessing.print();
	}
}
