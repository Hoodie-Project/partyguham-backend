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
 * ✅ 카카오 OAuth Client
 *
 * - 웹:
 *   1) buildAuthorizeUrl(state, LOGIN/ LINK) → 카카오 로그인 화면
 *   2) callback 에서 code 받음
 *   3) fetchUserByCode(code, LOGIN/ LINK) → access_token
 *
 * - 앱:
 *   - SDK 등으로 받은 access_token 을 그대로 fetchUserByAccessToken 에 전달
 */
@Component("KAKAO")
@RequiredArgsConstructor
public class KakaoClient implements OauthClient {

    private final WebClient web;
    private final OauthProps props;

    private OauthProviderProps p() {
        return props.getKakao();
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
                .queryParam("scope", "account_email")
                .queryParam("state", state)
                .toUriString();
    }

    /** 웹: code → access_token 교환 → user 조회 */
    @Override
    public OauthUser fetchUserByCode(String code, OAuthFlow flow) {
        String accessToken = exchangeCodeForAccessToken(code, flow);
        return fetchUserByAccessToken(accessToken);
    }

    /** 앱: provider access_token → user 조회 */
    @Override
    public OauthUser fetchUserByAccessToken(String accessToken) {
        Map<String, Object> me = web.get()
                .uri(p().getUserinfoUri())
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException().flatMap(Mono::error))
                .bodyToMono(Types.MAP_STR_OBJ)
                .blockOptional().orElseThrow();

        String id = asString(me.get("id"));
        if (id == null) throw new IllegalStateException("kakao id null");

        Map<String, Object> acc = asMap(me.get("kakao_account"));
        String email = asString(acc.get("email"));

        Map<String, Object> prof = asMap(acc.get("profile"));
        String image = asString(prof.get("profile_image_url"));

        return new OauthUser(id, email, image);
    }

    /** code → access_token (flow별 redirectUri로 교환해야 함) */
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
        if (access == null) throw new IllegalStateException("kakao access_token null");
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
}