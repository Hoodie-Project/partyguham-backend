package com.partyguham.domain.auth.oauth.client;

import com.partyguham.domain.auth.oauth.dto.OauthUser;
import com.partyguham.domain.auth.oauth.props.OauthProps;
import com.partyguham.domain.auth.oauth.props.OauthProviderProps;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * ✅ 구글 OAuth Client (전략 구현)
 *
 * 지원 시나리오
 *  - 웹: buildAuthorizeUrl() → callback(code) → fetchUserByCode()
 *  - 앱: provider access_token → fetchUserByAccessToken()
 *
 * 차이점(카카오 대비)
 *  - 고유 ID:  "sub"
 *  - 이메일:   "email"
 *  - 프로필:   "picture"
 *  - scope:    openid email profile (권장)
 */
@Component("GOOGLE")

public class GoogleClient implements OauthClient {


    private final WebClient web;
    private final OauthProps props;
    private final JwtDecoder googleJwtDecoder;

    public GoogleClient(
            WebClient web,
            OauthProps props,
            @Qualifier("googleJwtDecoder") JwtDecoder googleJwtDecoder
    ) {
        this.web = web;
        this.props = props;
        this.googleJwtDecoder = googleJwtDecoder;
    }

    private OauthProviderProps p() {
        return props.getGoogle();
    }


    /** 로그인/연동 시작 URL 생성 */
    @Override
    public String buildAuthorizeUrl(String state, OAuthFlow flow) {
        var p = p();
        String redirectUri = (flow == OAuthFlow.LOGIN)
                ? p.getRedirect().getLogin()
                : p.getRedirect().getLink();

        return UriComponentsBuilder.fromHttpUrl(p.getAuthUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", p.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "openid email profile")
                .queryParam("access_type", "offline")
                .queryParam("include_granted_scopes", "true")
                .queryParam("state", state)
                .toUriString();
    }

    /** 웹: code → access_token → user */
    @Override
    public OauthUser fetchUserByCode(String code, OAuthFlow flow) {
        String accessToken = exchangeCodeForAccessToken(code, flow);
        return fetchUserByAccessToken(accessToken);
    }

    /** 앱: provider access_token → user */
    @Override
    public OauthUser fetchUserByAccessToken(String accessToken) {
        Map<String, Object> me = web.get()
                .uri(p().getUserinfoUri())
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException().flatMap(Mono::error))
                .bodyToMono(Types.MAP_STR_OBJ)
                .blockOptional().orElseThrow();

        String id = asString(me.get("sub"));
        if (id == null) throw new IllegalStateException("google sub null");

        String email = asString(me.get("email"));
        String image = asString(me.get("picture"));

        return new OauthUser(id, email, image);
    }

    /** code → access_token (flow별 redirectUri 사용) */
    private String exchangeCodeForAccessToken(String code, OAuthFlow flow) {
        var p = p();
        String redirectUri = (flow == OAuthFlow.LOGIN)
                ? p.getRedirect().getLogin()
                : p.getRedirect().getLink();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", p.getClientId());
        form.add("client_secret", p.getClientSecret());
        form.add("redirect_uri", redirectUri);
        form.add("code", code);

        Map<String, Object> token = web.post()
                .uri(p.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException().flatMap(Mono::error))
                .bodyToMono(Types.MAP_STR_OBJ)
                .blockOptional().orElseThrow();

        String access = asString(token.get("access_token"));
        if (access == null) throw new IllegalStateException("google access_token null");
        return access;
    }

    // ===== 공통 헬퍼 =====
    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object o) {
        return (o instanceof Map) ? (Map<String, Object>) o : Map.of();
    }
    private static String asString(Object o) {
        return (o == null) ? null : Objects.toString(o, null);
    }
    private static final class Types {
        static final org.springframework.core.ParameterizedTypeReference<Map<String, Object>> MAP_STR_OBJ =
                new org.springframework.core.ParameterizedTypeReference<>() {};
    }

    @Override
    public OauthUser fetchUserByIdToken(String idToken) {
        // 1. JWT 검증 + 파싱 (서명, exp, iss 등 자동 검증)
        Jwt jwt = googleJwtDecoder.decode(idToken);

        // 2. 필수 클레임 추출
        String sub = jwt.getSubject();              // 유저 고유 ID
        String email = jwt.getClaim("email");
        String picture = jwt.getClaim("picture");

        if (sub == null) {
            throw new IllegalStateException("google sub missing");
        }

        // 3. 우리 도메인 유저 객체로 변환
        return new OauthUser(sub, email, picture);
    }
}