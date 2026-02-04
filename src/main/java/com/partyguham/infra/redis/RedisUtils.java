package com.partyguham.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate redis;

    /**
     * 데이터 저장 (TTL 설정 포함)
     * @param key 저장할 키 (예: "RT:1")
     * @param value 저장할 값 (토큰 등)
     * @param timeout 유효 시간 (밀리초 단위)
     */
    public void setData(String key, String value, Duration timeout) {
        redis.opsForValue().set(key, value, timeout);
    }

    /**
     * 데이터 조회
     * @param key 조회할 키
     * @return 저장된 값 (없으면 null)
     */
    public String getData(String key) {
        return redis.opsForValue().get(key);
    }

    /**
     * 데이터 삭제
     * @param key 삭제할 키
     */
    public void deleteData(String key) {
        redis.delete(key);
    }

    /**
     * 키 존재 여부 확인
     * @param key 확인할 키
     * @return 존재하면 true
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redis.hasKey(key));
    }
}