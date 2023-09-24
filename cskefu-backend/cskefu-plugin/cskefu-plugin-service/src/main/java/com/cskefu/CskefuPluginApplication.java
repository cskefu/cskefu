package com.cskefu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableDiscoveryClient
public class CskefuPluginApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuPluginApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
