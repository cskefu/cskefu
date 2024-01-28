package com.cskefu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class CskefuWechatChannelApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuWechatChannelApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
