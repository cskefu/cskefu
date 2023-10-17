package com.cskefu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class CskefuPluginApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuPluginApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
