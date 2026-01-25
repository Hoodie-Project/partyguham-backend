package com.partyguham.domain.auth.ott.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis에 OTT를 저장/조회/삭제하는 단순한 레포지토리
 * - 키 패턴: ott:{type}:{token}
 * - 값: JSON 문자열 (OttPayload 직렬화)
 */
@Repository
@RequiredArgsConstructor
public class OttTokenRepository {

    private final StringRedisTemplate redis;

    private String key(String type, String token) {
        return "ott:" + type + ":" + token;
    }

    public void save(String type, String token, String json, Duration ttl) {
        redis.opsForValue().set(key(type, token), json, ttl); // 값과 TTL을 함께 저장
    }

    public Optional<String> get(String type, String token) {
        return Optional.ofNullable(redis.opsForValue().get(key(type, token)));
    }

    public void delete(String type, String token) {
        redis.delete(key(type, token));
    }
}