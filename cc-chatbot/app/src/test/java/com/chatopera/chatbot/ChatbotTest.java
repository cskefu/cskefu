package com.chatopera.chatbot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
public class ChatbotTest
        extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotTest.class);
    private Chatbot cb;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ChatbotTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ChatbotTest.class);
    }

    public void setUp() {
        this.cb = new Chatbot("http", "lhc-dev", 8003, "v1");
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
        } catch (ChatbotRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testGetChatbots() {
        try {
            JSONObject resp = this.cb.getChatbots("name chatbotID", null, 0, 10);
            logger.info("[testGetChatbots] resp {}", resp.toString());
        } catch (ChatbotRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testConversation(){
        try {
            JSONObject resp = this.cb.conversation("co_bot_1", "sdktest", "华夏春松在哪里", false);
            logger.info("[testConversation] resp {}", resp.toString());
        } catch (ChatbotRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testFaq(){
        try {
            JSONObject resp = this.cb.faq("co_bot_1", "sdktest", "华夏春松在哪里", false);
            logger.info("[testFaq] resp {}", resp.toString());
        } catch (ChatbotRuntimeException e) {
            e.printStackTrace();
        }
    }

}
