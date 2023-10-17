package com.cskefu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@RefreshScope
public class CskefuWebGatewayApplication {
    @Value("${xxx}")
    private String xxx;
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuWebGatewayApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping({"", "/"})
    public String index() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        return localHost.getHostAddress() + " " + xxx;
    }
}