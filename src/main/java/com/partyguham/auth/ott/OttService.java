package com.partyguham.auth.ott;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * OTT 발급 및 검증 서비스
 * - issue(): UUID 기반 토큰 생성 -> Redis에 payload(JSON)와 TTL 저장 -> 토큰 반환
 * - consume(): Redis에서 payload 조회 -> 없으면 실패 -> 있으면 삭제(1회용) 후 OttPayload 반환
 */
@Service
@RequiredArgsConstructor
public class OttService {
    private final OttTokenRepository repo;
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 발급
     *
     * @param payload OTT에 저장할 데이터
     * @param ttl     토큰 유효기간 (예: Duration.ofMinutes(10))
     * @return 발급된 토큰 문자열(UUID)
     */
    public String issue(OttPayload payload, Duration ttl) {
        String token = UUID.randomUUID().toString();
        try {
            repo.save(payload.type().name(), token, om.writeValueAsString(payload), ttl);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("OTT issue failed", e);
        }
    }

    /**
     * 검증 + 소거 (1회용)
     *
     * @param type  기대하는 OTT 타입 (SIGNUP/RECOVER/LINK)
     * @param token 클라이언트가 보낸 토큰
     * @return OttPayload (기록된 페이로드)
     * @throws BadCredentialsException 토큰이 없거나 파싱 실패 시
     */
    public OttPayload consume(OttType type, String token) {
        var jsonOpt = repo.get(type.name(), token);
        if (jsonOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid or expired OTT");
        }

        // 1회 사용: 즉시 삭제
        repo.delete(type.name(), token);

        try {
            return om.readValue(jsonOpt.get(), OttPayload.class);
        } catch (Exception e) {
            throw new BadCredentialsException("OTT parse error");
        }
    }


    /**
     * 토큰 조회(검증하지 않고 내용만 확인, 소거하지 않음) 필요 시 제공
     */
    public OttPayload peek(OttType type, String token) {
        var jsonOpt = repo.get(type.name(), token);
        if (jsonOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid or expired OTT");
        }
        try {
            return om.readValue(jsonOpt.get(), OttPayload.class);
        } catch (Exception e) {
            throw new BadCredentialsException("OTT parse error");
        }
    }
}