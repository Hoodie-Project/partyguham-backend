package com.partyguham.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    /**
     * @Async 가 사용할 기본 쓰레드풀 설정
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);        // 기본 쓰레드 수
        executor.setMaxPoolSize(20);        // 최대 쓰레드 수
        executor.setQueueCapacity(100);     // 대기 큐 사이즈
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }

    /**
     * void 리턴 타입 @Async 메서드에서 발생한 예외를 처리
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(
                    Throwable ex,
                    Method method,
                    Object... params
            ) {
                log.error("[ASYNC-ERROR] method={}, params={}, message={}",
                        method.getName(),
                        Arrays.toString(params),
                        ex.getMessage(),
                        ex
                );

                // 필요하면 여기서
                // - 슬랙/문자/메일 알림
                // - Sentry, Logstash 등 외부 로그 수집
                // 등을 호출
            }
        };
    }
}