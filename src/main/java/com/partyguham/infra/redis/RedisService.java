package com.partyguham.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisUtils redisUtils;

    // Refresh Token 저장
    public void saveRefreshToken(Long userId, String token, Duration duration) {
        String key = "RT:" + userId;
        redisUtils.setData(key, token, duration);
    }

    // 토큰 검증 시 조회
    public String getRefreshToken(Long userId) {
        return redisUtils.getData("RT:" + userId);
    }

    // 로그아웃 시 삭제
    public void deleteRefreshToken(Long userId) {
        redisUtils.deleteData("RT:" + userId);
    }
}