package com.cskefu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class CskefuManagerApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuManagerApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
