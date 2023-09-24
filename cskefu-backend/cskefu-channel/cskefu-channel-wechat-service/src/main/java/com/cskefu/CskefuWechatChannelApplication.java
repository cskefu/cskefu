package com.cskefu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
//@EnableDiscoveryClient
public class CskefuWechatChannelApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuWechatChannelApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
