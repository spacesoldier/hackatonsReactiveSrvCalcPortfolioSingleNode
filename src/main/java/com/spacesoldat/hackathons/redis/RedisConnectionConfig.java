package com.spacesoldat.hackathons.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionConfig {

    @Value("${spring.redis.host}")
    private String redisHostName;

    @Value("${spring.redis.port}")
    private int redisPortNumber;


    @Bean(name="redisConnFactory")
    public JedisConnectionFactory setupJedisConnFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisHostName);
        jedisConnectionFactory.setPort(redisPortNumber);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }



}
