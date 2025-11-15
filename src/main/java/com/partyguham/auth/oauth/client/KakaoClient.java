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
 * ✅ 카카오 OAuth Client (전략 구현)
 *
 * 지원 시나리오
 *  - 웹: buildAuthorizeUrl() → 사용자 로그인 → callback(code) → fetchUserByCode()
 *  - 앱: SDK 등으로 받은 provider access_token → fetchUserByAccessToken()
 *
 * 구현 포인트
 *  - code→access_token 교환 로직을 exchangeCodeForAccessToken()로 분리하고
 *    fetchUserByCode()가 fetchUserByAccessToken()을 재사용하여 중복 제거
 *  - 응답 JSON은 동적이므로 Map<String,Object>로 역직렬화
 *  - asMap()/asString() 헬퍼로 NPE/캐스팅 안전화
 *  - onStatus(...)로 HTTP 오류 → 예외 전파
 */
@Component("KAKAO")
@RequiredArgsConstructor
public class KakaoClient implements OauthClient {

    private final WebClient web;   // 외부 API 호출
    private final OauthProps props; // application.yml 바인딩

    /** 로그인 시작 URL 생성 (프런트가 여기로 리다이렉트) */
    @Override
    public String buildAuthorizeUrl(String state) {
        var p = p(); // kakao 설정 단축
        return UriComponentsBuilder.fromHttpUrl(p.getAuthUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", p.getClientId())
                .queryParam("redirect_uri", p.getRedirectUri())
                .queryParam("scope", "account_email")
                .queryParam("state", state) // CSRF 방지용
                .toUriString();
    }

    /** 웹: code → access_token 교환 후 사용자정보 조회 */
    @Override
    public OauthUser fetchUserByCode(String code, String state) {
        String accessToken = exchangeCodeForAccessToken(code);
        return fetchUserByAccessToken(accessToken); // 공통 경로 재사용
    }

    /** 앱: provider access_token을 직접 받아 사용자정보 조회 */
    @Override
    public OauthUser fetchUserByAccessToken(String accessToken) {
        Map<String, Object> me = web.get()
                .uri(p().getUserinfoUri()) // ex) https://kapi.kakao.com/v2/user/me
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException().flatMap(Mono::error))
                .bodyToMono(Types.MAP_STR_OBJ)
                .blockOptional().orElseThrow();

        // 카카오 고유 사용자 식별자
        String id = asString(me.get("id"));
        if (id == null) throw new IllegalStateException("kakao id null");

        // kakao_account.email
        Map<String, Object> acc = asMap(me.get("kakao_account"));
        String email = asString(acc.get("email"));

        // kakao_account.profile.profile_image_url
        Map<String, Object> prof = asMap(acc.get("profile"));
        String image = asString(prof.get("profile_image_url"));

        return new OauthUser(id, email, image);
    }

    /** code → access_token 교환 (x-www-form-urlencoded) */
    private String exchangeCodeForAccessToken(String code) {
        var p = p();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", p.getClientId());
        form.add("client_secret", p.getClientSecret());
        form.add("redirect_uri", p.getRedirectUri());
        form.add("code", code);

        Map<String, Object> token = web.post()
                .uri(p.getTokenUri()) // ex) https://kauth.kakao.com/oauth/token
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException().flatMap(Mono::error))
                .bodyToMono(Types.MAP_STR_OBJ)
                .blockOptional().orElseThrow();

        String access = asString(token.get("access_token"));
        if (access == null) throw new IllegalStateException("kakao access_token null");
        return access;
    }

    /** kakao provider 설정 단축 접근자 */
    private OauthProviderProps p() { return props.getKakao(); }

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