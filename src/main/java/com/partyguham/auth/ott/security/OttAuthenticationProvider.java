package com.partyguham.auth.ott.security;

import com.partyguham.auth.ott.OttPayload;
import com.partyguham.auth.ott.OttService;
import com.partyguham.auth.ott.OttType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * ✅ Spring Security 인증 검증 로직
 * - Filter → OttAuthenticationToken 생성
 * - Provider는 Redis에서 OTT를 검증(OttService)
 * - 유효하면 OttAuthenticatedToken으로 변환 후 ROLE 부여
 */
@Component
@RequiredArgsConstructor
public class OttAuthenticationProvider implements AuthenticationProvider {

    private final OttService ottService;  // Redis 검증 서비스

    @Override
    public OttAuthenticatedToken authenticate(org.springframework.security.core.Authentication auth) {

        // 1) 필터에서 넘겨준 인증 전 토큰 캐스팅
        OttAuthenticationToken raw = (OttAuthenticationToken) auth;

        // principal에 저장된 OttType를 얻음
        OttType type = raw.getPrincipal() instanceof OttType t ? t : null;
        String token = (String) raw.getCredentials();

        // 2) Redis에서 OTT 검증 & 1회 사용 후 삭제
        OttPayload payload = ottService.consume(type, token);

        // 3) OTT 타입별 권한 부여
        String role = switch (payload.type()) {
            case SIGNUP -> "ROLE_SIGNUP";
            case RECOVER -> "ROLE_RECOVER";
            case LINK -> "ROLE_LINK";
        };

        // 4) 인증 완료된 객체 반환 → SecurityContext에 저장됨
        return new OttAuthenticatedToken(payload,
                List.of(new SimpleGrantedAuthority(role)));
    }

    // OttAuthenticationToken을 처리할 수 있는 Provider임을 명시
    @Override
    public boolean supports(Class<?> authentication) {
        return OttAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
