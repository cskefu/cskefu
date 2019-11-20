package com.chatopera.cc.acd;

import com.chatopera.cc.acd.basic.ACDComposeContext;
import com.chatopera.cc.model.AgentUser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ACDComposeContextTest extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(ACDComposeContextTest.class);

    public ACDComposeContextTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ACDComposeContextTest.class);
    }

    public void testSetAndGet(){
        ACDComposeContext ctx = new ACDComposeContext();
        ctx.setAgentUser(new AgentUser());
        ctx.getAgentUser().setOrgi("foo");
        assertEquals(ctx.getAgentUser().getOrgi(), "foo");
    }

}