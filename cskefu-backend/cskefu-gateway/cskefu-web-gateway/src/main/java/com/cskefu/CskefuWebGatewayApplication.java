package com.cskefu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication


public class CskefuWebGatewayApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuWebGatewayApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
