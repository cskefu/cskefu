package com.cskefu;

import com.cskefu.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class CskefuAuthApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(CskefuAuthApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter authenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    UserDetailsService userDetailsService() {
        UserDetails laowang = User.withUsername("admin")
                .password(passwordEncoder().encode("123456"))
                .roles("admin")
                .authorities("add")
                .build();
        UserDetails zhangsan = User.withUsername("zhangsan")
                .password(passwordEncoder().encode("123456"))
                .roles("admin", "hr")
                .authorities("view")
                .build();
        return new InMemoryUserDetailsManager(laowang, zhangsan);
    }
}
