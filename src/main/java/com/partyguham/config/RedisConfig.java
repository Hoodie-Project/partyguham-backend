package com.partyguham.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(); // host/port는 yml
    }
    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory f){
        return new StringRedisTemplate(f);
    }
}