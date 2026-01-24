package com.partyguham.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password:}") // 값이 없으면 빈 문자열로 설정됨
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 1. Redis 접속 정보 설정 (호스트, 포트)
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);

        // 2. 비밀번호가 있을 경우에만 설정
        if (password != null && !password.isBlank()) {
            redisConfig.setPassword(password);
        }

        // 3. 설정을 들고 연결 공장 생성
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory f){
        return new StringRedisTemplate(f);
    }
}