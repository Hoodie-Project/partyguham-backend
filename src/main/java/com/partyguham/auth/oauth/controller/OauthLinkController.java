package com.partyguham.auth.oauth.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.auth.oauth.client.OAuthFlow;
import com.partyguham.auth.oauth.client.OauthClient;
import com.partyguham.auth.oauth.dto.OauthUser;
import com.partyguham.auth.oauth.entity.Provider;
import com.partyguham.auth.oauth.service.OauthLinkService;
import com.partyguham.auth.oauth.service.OauthStateService;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.config.DomainProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * 🔗 OAuth 계정 연동 컨트롤러
 *
 * - 전제: 이미 JWT 로그인 된 상태
 * - 웹:
 *   GET  /api/v2/auth/oauth/link/{provider}           → provider 로그인 페이지로 리다이렉트
 *   GET  /api/v2/auth/oauth/link/{provider}/callback  → code 받아서 연동 후 프론트로 리다이렉트
 * - 앱:
 *   POST /api/v2/auth/oauth/link/{provider}/token-link
 *        → provider access_token 으로 바로 연동
 */
@ApiV2Controller
@RequiredArgsConstructor// → /api/v2 prefix 부여하는 커스텀 애노테이션
@RequestMapping("/auth/oauth")
public class OauthLinkController {

    // "KAKAO", "GOOGLE" 이름으로 등록된 OauthClient 빈들을 주입받음
    private final Map<String, OauthClient> clients;

    // state ↔ (provider, userId) 저장용 서비스 (Redis)
    private final OauthStateService oauthStateService;

    private final OauthLinkService oauthLinkService;

    // 프론트 도메인 정보 (ex: https://partyguham.com)
    private final DomainProperties domain;

    // ===== 1) 웹: 연동 시작 =====

    /**
     * 🔹 웹 연동 시작
     * - 현재 로그인 유저 기준으로 state 생성 후
     *   카카오/구글 authorize URL 로 리다이렉트
     */
    @GetMapping("/{provider}/link")
    public void startLink(
            @PathVariable Provider provider,
            @AuthenticationPrincipal UserPrincipal user, // JWT에서 온 로그인 유저
            HttpServletResponse res
    ) throws IOException {

        if (user == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "login_required");
            return;
        }

        // 1) state 생성 후 Redis에 (provider, state, userId) 저장
        String state = UUID.randomUUID().toString();
        oauthStateService.save(provider.name(), state, user.getId(), Duration.ofMinutes(5));

        // 2) 각 provider client가 authorize URL 생성
        OauthClient client = clients.get(provider.name());
        if (client == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "unsupported_provider");
            return;
        }

        String url = client.buildAuthorizeUrl(state, OAuthFlow.LOGIN);
        res.sendRedirect(url);
    }

    // ===== 2) 웹: 연동 콜백 =====

    /**
     * 🔹 웹 연동 콜백
     * - provider 가 code + state 를 가지고 리다이렉트 해 줌
     * - state 검증 → code 로 OauthUser 조회 → 현재 로그인 유저와 연동
     */
    @GetMapping("/{provider}/link/callback")
    public void callbackLink(
            @PathVariable Provider provider,
            @RequestParam String code,
            @RequestParam String state,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletResponse res
    ) throws IOException {

        if (user == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "login_required");
            return;
        }

        // 1) state 검증 (provider, state, userId 매칭 확인 후 1회성 삭제)
        boolean ok = oauthStateService.validateAndConsume(provider.name(), state, user.getId());
        if (!ok) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid_state");
            return;
        }

        // 2) code → accessToken → OauthUser
        OauthClient client = clients.get(provider.name());
        if (client == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "unsupported_provider");
            return;
        }

        OauthUser ou = client.fetchUserByCode(code, OAuthFlow.LOGIN);

        // 3) 현재 로그인한 유저 기준으로 연동
        oauthLinkService.linkAccount(
                user.getId(),
                Provider.valueOf(provider.name()),
                ou.externalId()
        );

        // 4) 연동 완료 후 프론트 마이페이지로 리다이렉트
        String redirectUrl = domain.getBase() + "/my/account";
        res.sendRedirect(redirectUrl);
    }

    // ===== 3) 앱: 토큰으로 연동 =====

    /**
     * 🔹 앱 연동
     * - 앱에서 이미 provider access_token 을 들고 있는 경우
     *   → 백엔드에 토큰을 넘겨서 연동
     */
    @PostMapping("/{provider}/link")
    public Map<String, Object> linkAccount(
            @PathVariable Provider provider,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody Map<String, String> body
    ) {
        String providerToken = body.get("accessToken");
        if (providerToken == null) {
            throw new IllegalArgumentException("accessToken is required");
        }

        // 1) provider access_token 으로 사용자 정보 조회
        OauthClient client = clients.get(provider.name());
        if (client == null) {
            throw new IllegalArgumentException("unsupported provider: " + provider);
        }

        OauthUser u = client.fetchUserByAccessToken(providerToken);

        // 2) 현재 로그인한 userId와 OAuth 계정 연결
        oauthLinkService.linkAccount(
                user.getId(),
                Provider.valueOf(provider.name()),
                u.externalId()
        );

        return Map.of(
                "linked", true,
                "provider", provider.name(),
                "externalId", u.externalId()
        );
    }
}