package com.partyguham.auth.ott.security;

import com.partyguham.auth.ott.model.OttPayload;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * ✅ OTT 검증 성공 후 생성되는 인증된 객체
 * - ROLE_SIGNUP / ROLE_RECOVER / ROLE_LINK 같은 권한 부여
 * - 컨트롤러에서는 @AuthenticationPrincipal 로 OttPayload를 받을 수 있음
 */
public class OttAuthenticatedToken extends AbstractAuthenticationToken {

    private final OttPayload payload;  // OAuth 정보, email, image 등 회원가입에 필요한 정보

    public OttAuthenticatedToken(OttPayload payload,
                                 Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.payload = payload;
        setAuthenticated(true);  // 인증 완료 표시
    }

    // 인증 후에는 Credential(토큰)을 실제로 사용할 필요가 없음
    @Override
    public Object getCredentials() {
        return "";
    }

    // principal = 인증 완료된 사용자 정보(payload)
    @Override
    public Object getPrincipal() {
        return payload;
    }
}