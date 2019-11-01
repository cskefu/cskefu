package com.chatopera.cc.config;

import com.chatopera.cc.cache.RedisKey;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * maxInactiveIntervalInSeconds: 设置 Session 失效时间，
 * 使用 Redis Session 之后，原 Spring Boot 的 server.session.timeout 属性不再生效。
 * http://www.ityouknow.com/springboot/2016/03/06/spring-boot-redis.html
 * 86400 代表一天
 * maxInactiveIntervalInSeconds = 86400 * 30
 */

@Configuration
@EnableRedisHttpSession()
public class WebServerSessionConfigure {

    /**
     * spring在多长时间后强制使redis中的session失效,默认是1800.(单位/秒)
     */
    @Value("${server.session-timeout}")
    private int maxInactiveIntervalInSeconds;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String pass;

    @Value("${spring.redis.session.db}")
    private int db;

    @Primary
    @Bean
    public RedisOperationsSessionRepository sessionRepository(RedisTemplate<Object, Object> sessionRedisTemplate) {
        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(sessionRedisTemplate);
        sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
        sessionRepository.setRedisFlushMode(RedisFlushMode.IMMEDIATE);
        sessionRepository.setRedisKeyNamespace(RedisKey.CACHE_SESSIONS);
        return sessionRepository;
    }

    @Bean
    public RedisTemplate<Object, Object> sessionRedisTemplate() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setDatabase(db);
        if (StringUtils.isNotBlank(pass)) {
            factory.setPassword(pass);
        }
        factory.afterPropertiesSet();
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(factory);
        return template;
    }
}
