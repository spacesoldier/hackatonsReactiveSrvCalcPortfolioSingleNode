package com.spacesoldat.hackathons.implementation.execution.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTemplateConfig {
    // @Autowired @Qualifier("redisConnFactory")
    private JedisConnectionFactory redisConnectionFactory;

    @Bean(name="redisConnFactory")
    public RedisTemplate<String, SecurityProperties.User> redisTemplate() {
        RedisTemplate<String, SecurityProperties.User> redisTemplate = new RedisTemplate<String , SecurityProperties.User>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
