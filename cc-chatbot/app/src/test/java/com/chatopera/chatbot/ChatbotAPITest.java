package com.chatopera.chatbot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Unit test for simple App.
 */
public class ChatbotAPITest
        extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotAPITest.class);
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
            logger.info("[testGetChatbot] {}", resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testGetChatbots() {
        try {
            JSONObject resp = this.cb.getChatbots("name chatbotID", null, 0, 10);
            logger.info("[testGetChatbots] resp {}", resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testConversation() {
        try {
            JSONObject resp = this.cb.conversation("co_bot_1", "sdktest", "华夏春松在哪里", false);
            logger.info("[testConversation] resp {}", resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testFaq() {
        try {
            JSONObject resp = this.cb.faq("co_bot_1", "sdktest", "华夏春松在哪里", false);
            logger.info("[testFaq] resp {}", resp.toString());
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testParseUrl() {
        try {
            ChatbotAPI c = new ChatbotAPI("https://local:8000/");
            logger.info("chatbot baseUrl {}", c.getBaseUrl());
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
            logger.info("[testCreateBot] {}", j);
        } catch (ChatbotAPIRuntimeException e) {
            e.printStackTrace();
        }
    }


}


