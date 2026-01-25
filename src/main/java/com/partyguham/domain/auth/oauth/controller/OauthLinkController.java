package com.partyguham.domain.auth.oauth.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.domain.auth.oauth.client.OAuthFlow;
import com.partyguham.domain.auth.oauth.client.OauthClient;
import com.partyguham.domain.auth.oauth.dto.OauthUser;
import com.partyguham.domain.auth.oauth.dto.request.AppCodeLoginRequest;
import com.partyguham.domain.auth.oauth.entity.Provider;
import com.partyguham.domain.auth.oauth.service.OauthLinkService;
import com.partyguham.domain.auth.oauth.service.OauthStateService;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.global.config.DomainProperties;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * 🔗 OAuth 계정 연동 컨트롤러
 * <p>
 * - 전제: 이미 JWT 로그인 된 상태
 * - 웹:
 * GET  /api/v2/auth/oauth/link/{provider}           → provider 로그인 페이지로 리다이렉트
 * GET  /api/v2/auth/oauth/link/{provider}/callback  → code 받아서 연동 후 프론트로 리다이렉트
 * - 앱:
 * POST /api/v2/auth/oauth/link/{provider}/link
 * → provider access_token, id_token 으로 바로 연동
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
    private final DomainProperties domain;

    // ===== 1) 웹: 연동 시작 =====

    /**
     * 🔹 웹 연동 시작
     * - 현재 로그인 유저 기준으로 state 생성 후
     * 카카오/구글 authorize URL 로 리다이렉트
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
     * → 백엔드에 토큰을 넘겨서 연동
     */
    @PostMapping("/{provider}/link")
    public ResponseEntity<?> linkAccount(
            @PathVariable Provider provider,
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody AppCodeLoginRequest req
    ) {
        OauthClient client = clients.get(provider.name());
        if (client == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "type", "error",
                    "error", "unsupported_provider"
            ));
        }

        OauthUser u;
        try {
            u = switch (provider) {
                case GOOGLE -> client.fetchUserByIdToken(req.token());       // id_token
                case KAKAO -> client.fetchUserByAccessToken(req.token());   // access_token
                default -> throw new IllegalArgumentException("unsupported provider");
            };
        } catch (Exception e) {
            // 토큰 검증/조회 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "type", "error",
                    "error", "invalid_token"
            ));
        }
        // 2) 현재 로그인한 userId와 OAuth 계정 연결
        oauthLinkService.linkAccount(
                user.getId(),
                Provider.valueOf(provider.name()),
                u.externalId()
        );

        return ResponseEntity.ok(Map.of(
                "linked", true,
                "provider", provider.name()
        ));
    }
}