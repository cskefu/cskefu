package com.cskefu.security;

import com.cskefu.mvc.HttpResponseUtils;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CskefuSecurityConfig {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private JwtAuthenticationFilter authenticationFilter;

    public static final String[] SECURITY_IGNORE_PATTERNS = {
            "/favicon.ico",
            "/doc.html",
            "/webjars/**",
            "/smart-doc/**",
            "/configuration/ui",
            "/configuration/security",
            "/auth/login"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(registry -> registry.requestMatchers(SECURITY_IGNORE_PATTERNS).permitAll().anyRequest().authenticated());
        http.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.cacheControl(HeadersConfigurer.CacheControlConfig::disable));
        http.exceptionHandling(configurer -> configurer.accessDeniedHandler((request, response, accessDeniedException) -> {
            HttpResponseUtils.unauthorizedResponse(response, "权限不足，请联系管理员！");
        }).authenticationEntryPoint((request, response, authException) -> {
            HttpResponseUtils.unauthorizedResponse(response, "您尚未登录或登录信息已过期，请重新登录！");
        }));
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
//            corsConfiguration.setAllowedHeaders(Arrays.asList(""));
            corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTION"));
            corsConfiguration.setAllowedOriginPatterns(List.of("/**"));
            corsConfiguration.setMaxAge(3600L);
            return corsConfiguration;
        }));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.userDetailsService(userDetailsService);
        return http.build();
    }
}