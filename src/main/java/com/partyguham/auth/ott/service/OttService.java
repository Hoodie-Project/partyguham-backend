package com.partyguham.auth.ott.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyguham.auth.exception.AuthErrorCode;
import com.partyguham.auth.ott.repository.OttTokenRepository;
import com.partyguham.auth.ott.model.OttPayload;
import com.partyguham.auth.ott.model.OttType;
import com.partyguham.common.error.CommonErrorCode;
import com.partyguham.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * OTT 발급 및 검증 서비스
 * - issue(): UUID 기반 토큰 생성 -> Redis에 payload(JSON)와 TTL 저장 -> 토큰 반환
 * - consume(): Redis에서 payload 조회 -> 없으면 실패 -> 있으면 삭제(1회용) 후 OttPayload 반환
 */
@Slf4j
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
        } catch (JsonProcessingException e) {
            // 페이로드 직렬화 실패는 시스템 에러이므로 로그를 남기고 알 수 없는 예외로 던짐, @SneakyThrows 사용 고려
            log.error("OTT 발급 중 JSON 직렬화 실패 | Payload: {}", payload);
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
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

        // 토큰이 없거나 만료된 경우 (이미 사용된 경우 포함)
        if (jsonOpt.isEmpty()) {
            throw new BusinessException(AuthErrorCode.EXPIRED_OTT_TOKEN);
        }

        // 1회 사용: 즉시 삭제
        repo.delete(type.name(), token);

        try {
            return om.readValue(jsonOpt.get(), OttPayload.class);
        } catch (JsonProcessingException e) {
            // 저장된 데이터가 깨졌거나 형식이 맞지 않는 경우
            throw new BusinessException(AuthErrorCode.INVALID_OTT_TYPE);
        }
    }


    /**
     * 토큰 조회 (소거하지 않음)
     */
    public OttPayload peek(OttType type, String token) {
        var jsonOpt = repo.get(type.name(), token);

        if (jsonOpt.isEmpty()) {
            throw new BusinessException(AuthErrorCode.EXPIRED_OTT_TOKEN);
        }

        try {
            return om.readValue(jsonOpt.get(), OttPayload.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(AuthErrorCode.INVALID_OTT_TYPE);
        }
    }
}