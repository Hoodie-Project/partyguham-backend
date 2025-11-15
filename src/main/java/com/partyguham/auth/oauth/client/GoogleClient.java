package com.partyguham.auth.oauth.client;

import com.partyguham.auth.oauth.dto.OauthUser;
import com.partyguham.auth.oauth.props.OauthProps;
import com.partyguham.auth.oauth.props.OauthProviderProps;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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
@RequiredArgsConstructor
public class GoogleClient implements OauthClient {

    private final WebClient web;
    private final OauthProps props;

    /** 로그인 시작 URL 생성 */
    @Override
    public String buildAuthorizeUrl(String state) {
        var p = p();
        return UriComponentsBuilder.fromHttpUrl(p.getAuthUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", p.getClientId())
                .queryParam("redirect_uri", p.getRedirectUri())
                .queryParam("scope", "openid email profile")
                .queryParam("access_type", "offline")          // refresh_token 발급 유도(구글)
                .queryParam("include_granted_scopes", "true")
                .queryParam("state", state)
                .toUriString();
    }

    /** 웹: code → access_token → 사용자정보 */
    @Override
    public OauthUser fetchUserByCode(String code, String state) {
        String accessToken = exchangeCodeForAccessToken(code);
        return fetchUserByAccessToken(accessToken);
    }

    /** 앱: provider access_token → 사용자정보 */
    @Override
    public OauthUser fetchUserByAccessToken(String accessToken) {
        Map<String, Object> me = web.get()
                .uri(p().getUserinfoUri()) // ex) https://www.googleapis.com/oauth2/v3/userinfo
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException().flatMap(Mono::error))
                .bodyToMono(Types.MAP_STR_OBJ)
                .blockOptional().orElseThrow();

        // 구글 고유 ID는 "sub"
        String id = asString(me.get("sub"));
        if (id == null) throw new IllegalStateException("google sub null");

        String email = asString(me.get("email"));
        String image = asString(me.get("picture"));

        return new OauthUser(id, email, image);
    }

    /** code → access_token 교환 */
    private String exchangeCodeForAccessToken(String code) {
        var p = p();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", p.getClientId());
        form.add("client_secret", p.getClientSecret());
        form.add("redirect_uri", p.getRedirectUri());
        form.add("code", code);

        Map<String, Object> token = web.post()
                .uri(p.getTokenUri()) // ex) https://oauth2.googleapis.com/token
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

    /** google provider 설정 단축 접근자 */
    private OauthProviderProps p() { return props.getGoogle(); }

    // ====== 안전 헬퍼 ======
    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object o) {
        return (o instanceof Map) ? (Map<String, Object>) o : java.util.Map.of();
    }
    private static String asString(Object o) {
        return (o == null) ? null : Objects.toString(o, null);
    }
    /** WebClient 제네릭 역직렬화 타입 지정 */
    private static final class Types {
        static final org.springframework.core.ParameterizedTypeReference<Map<String, Object>> MAP_STR_OBJ =
                new org.springframework.core.ParameterizedTypeReference<>() {};
    }
}