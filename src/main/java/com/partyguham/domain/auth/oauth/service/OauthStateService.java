package com.partyguham.domain.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.UUID;


/**
 * OAuth state 관리 서비스
 * - 로그인 시작 시 state를 저장(짧은 TTL)
 * - 콜백에서 동일 state인지 검증 후 1회성으로 폐기
 */
@Service
@RequiredArgsConstructor
public class OauthStateService {

    private final StringRedisTemplate redis;

    /**
     * 🌟 state 저장
     *
     * @param provider - "KAKAO" / "GOOGLE"
     * @param state - 랜덤 문자열(UUID 등)
     * @param ttl - 유효시간 (보통 5분)
     *
     * Redis 에 다음처럼 저장됨:
     *  key = oauth:state:KAKAO:8d9e1c...
     *  value = "1"
     *  ttl = 300초
     *
     * value 로 "1" 을 넣는 이유:
     *  - state는 실제 데이터가 필요 없음 → 존재 여부만 체크하면 됨
     *  - placeholder 형태로 "1" 넣는건 실무에서 흔히 쓰는 방식
     */
    public void save(String provider, String state, Duration ttl) {
        //.set(key, value, ttl)
        redis.opsForValue().set(key(provider, state), "1", ttl);
    }

    public void save(String provider, String state, Long userId, Duration ttl) {
        redis.opsForValue().set(key(provider, state), userId.toString(), ttl);
    }


    /**
     * 🌟 state 검증 + 1회성 소비(consuming)
     *
     * OAuth 콜백에서 다음 절차로 사용됨:
     *
     *  1) 프론트가 redirect 된 URL에서 state 를 들고 옴
     *  2) Redis 에 state 가 있는지 확인
     *  3) 있다면 → 정상 요청 → 바로 삭제 (1회성)
     *  4) 없다면 → 재사용 or 조작 공격 → 오류 처리
     */
    public boolean validateAndConsume(String provider, String state) {
        String k = key(provider, state);
        Boolean exists = redis.hasKey(k);
        if (Boolean.TRUE.equals(exists)) {
            redis.delete(k); // 1회성
            return true;
        }
        return false;
    }

    // userId 추가 저장
    public boolean validateAndConsume(String provider, String state, Long userId) {
        String k = key(provider, state);

        String savedUserId = redis.opsForValue().get(k);
        if (savedUserId == null) return false;

        // userId 검증
        if (!savedUserId.equals(userId.toString())) return false;

        redis.delete(k); // 1회성 소비
        return true;
    }

    // 예: key = "oauth:link:{provider}:{state}", value = userId
    public void saveForLink(String provider, Long userId, Duration ttl) {
        String state = UUID.randomUUID().toString();
        redis.opsForValue().set("oauth:link:%s:%s".formatted(provider, state),
                String.valueOf(userId),
                ttl);
        // state는 컨트롤러에서 리턴
    }

    /**
     * 🌟 Redis key 생성 규칙
     *
     * oauth:state:{provider}:{state}
     *
     * 예:
     *   oauth:state:KAKAO:7f34b8c1-879e-4da2-a8bb-...
     *
     * 이런 규칙을 쓰면:
     *   - redis 데이터 보기 편함
     *   - provider 별로 그룹핑됨
     *   - 나중에 모니터링/필터링 쉬움
     */
    private String key(String p, String s) {
        return "oauth:state:" + p + ":" + s;
    }
}