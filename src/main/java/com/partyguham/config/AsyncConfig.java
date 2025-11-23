package com.partyguham.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // 필요하면 Custom Executor 설정 가능
    // 지금은 Spring 기본 Executor 사용
}