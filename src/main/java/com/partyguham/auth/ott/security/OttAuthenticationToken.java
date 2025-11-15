package com.partyguham.auth.ott.security;

import com.partyguham.auth.ott.model.OttType;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * ✅ 인증 “전”의 OTT 토큰을 담는 객체
 * - Filter가 요청에서 OTT를 추출하면 이 객체를 생성해 AuthenticationProvider로 넘김
 * - 아직 인증이 안 된 상태이므로 authenticated=false
 */
public class OttAuthenticationToken extends AbstractAuthenticationToken {

    private final OttType type;   // SIGNUP / RECOVER / LINK
    private final String token;   // Redis에 저장된 1회용 토큰 문자열(UUID)

    public OttAuthenticationToken(OttType type, String token) {
        super(null);              // 권한 없음
        this.type = type;
        this.token = token;
        setAuthenticated(false);  // 아직 인증되지 않은 상태
    }

    // 인증 전 토큰 → Credential은 원본 토큰 문자열
    @Override
    public Object getCredentials() {
        return token;
    }

    // Principal은 "어떤 종류의 OTT인지" 정보만 가지고 있음
    @Override
    public Object getPrincipal() {
        return type;
    }
}