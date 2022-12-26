package com.cskefu.serving.api.config;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "plugins")
@Getter
@Setter
public class PluginsConfigurer {

    private Map<String, String> config;

//    @PostConstruct
//    public void print() {
//        for (Map.Entry<String, String> entry : config.entrySet()) {
//            System.out.println(entry.getKey() + ":" + entry.getValue());
//        }
//    }

}
