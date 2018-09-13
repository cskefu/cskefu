/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.chatbot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Unit test for simple App.
 */
public class ChatbotAPITest
        extends TestCase {
    private ChatbotAPI cb;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ChatbotAPITest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ChatbotAPITest.class);
    }

    public void setUp() {
        this.cb = new ChatbotAPI("http", "lhc-dev", 8003, "v1");
    }

    /**
     * Rigourous Test :-)
     */
    public void testChatbot() {
        assertEquals(this.cb.getPort(), 8003);
    }

    public void testGetChatbot() {
        try {
            JSONObject resp = this.cb.getChatbot("co_bot_1");
            System.out.println("[testGetChatbot] " + resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testGetChatbots() {
        try {
            JSONObject resp = this.cb.getChatbots("name chatbotID", null, 0, 10);
            System.out.println("[testGetChatbots] resp " + resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testConversation() {
        try {
            JSONObject resp = this.cb.conversation("co_bot_1", "sdktest", "华夏春松在哪里", false);
            System.out.println("[testConversation] resp " + resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testFaq() {
        try {
            JSONObject resp = this.cb.faq("co_bot_1", "sdktest", "华夏春松在哪里", false);
            System.out.print("[testFaq] resp " + resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testParseUrl() {
        try {
            ChatbotAPI c = new ChatbotAPI("https://local:8000/");
            System.out.println("chatbot baseUrl " + c.getBaseUrl());
            assertEquals("https://local:8000/api/v1", c.getBaseUrl());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void testExists() {
        JSONObject profile = null;
        try {
            assertTrue(this.cb.exists("co_bot_1"));
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testCreateBot() {
        try {
             JSONObject j = this.cb.createBot("cc_bot_2",
                    "小云2",
                    "zh_CN",
                    "我不了解。",
                    "小云机器人",
                    "你好，我是小云。");
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }


}


