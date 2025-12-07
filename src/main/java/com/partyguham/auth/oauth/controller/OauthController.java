package com.partyguham.auth.oauth.controller;

import com.partyguham.auth.oauth.LoginResult;
import com.partyguham.auth.oauth.UserErrorType;
import com.partyguham.auth.oauth.client.OAuthFlow;
import com.partyguham.auth.oauth.entity.Provider;
import com.partyguham.auth.oauth.client.OauthClient;
import com.partyguham.auth.oauth.dto.OauthUser;
import com.partyguham.auth.oauth.service.OauthStateService;
import com.partyguham.auth.oauth.service.OauthLoginService;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.config.DomainProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth 컨트롤러
 *
         * 엔드포인트
 * - (웹)  GET /api/v2/auth/oauth/{provider}/login      : authorizeUrl + state 발급
 * - (웹)  GET /api/v2/auth/oauth/{provider}/callback   : ?code&state → OTT/JWT
 * - (앱)  POST /api/v2/auth/oauth/{provider}/token-login {accessToken} → OTT/JWT
 *
         * 주의
 * - provider는 "KAKAO", "GOOGLE" 등의 bean 이름과 매칭됨(@Component("KAKAO"))
 * - 웹에서는 반드시 state를 저장/검증하여 CSRF 방지
 */

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/auth/oauth")
public class OauthController {

    private final Map<String, OauthClient> clients;
    private final OauthStateService oauthStateService;
    private final OauthLoginService oauthLoginService;
    private final DomainProperties domain;

    /**
     * (웹) 로그인 시작: state 저장 후 authorizeUrl 리다이렉트
     */
    @GetMapping("/{provider}/login")
    public void start(@PathVariable Provider provider, HttpServletResponse res) throws IOException {

        String state = UUID.randomUUID().toString();

        oauthStateService.save(provider.name(), state, Duration.ofMinutes(5));


        OauthClient client = clients.get(provider.name());
        if (client == null) {
            res.sendError(400, "unsupported_provider");
            return;
        }

        String url = client.buildAuthorizeUrl(state, OAuthFlow.LOGIN);
        res.sendRedirect(url);
    }

    /**
     * (웹) 콜백: state 검증 → code를 user 정보로 교환 → 비즈 로직 처리 → 응답
     * - platform=web 고정 응답. 필요 시 쿼리로 구분 가능
     */
    @GetMapping("/{provider}/login/callback")
    public void callback(@PathVariable Provider provider,
                         @RequestParam String code,
                         @RequestParam String state,
                         HttpServletResponse res) throws IOException {

        // 1) state 검증
        if (!oauthStateService.validateAndConsume(provider.name(), state)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid_state");
            return;
        }

        // 2) code → user
        OauthUser u = clients.get(provider.name()).fetchUserByCode(code, OAuthFlow.LOGIN);

        // 3) 비즈 로직
        LoginResult r = oauthLoginService.handleCallback(
                provider, u.externalId(), u.email(), u.image()
        );

        // 4) 결과 타입별 응답
        switch (r.type()) {
            case SIGNUP -> handleSignup(res, r);
            case LOGIN -> handleLogin(res, r);
            case RECOVER -> handleRecover(res, r);
            case ERROR -> handleErrorRedirect(res, r);
        }
    }

    /** 회원가입 필요: signupToken 쿠키 + /signup 으로 이동 */
    private void handleSignup(HttpServletResponse res, LoginResult r) throws IOException {
        ResponseCookie ott = ResponseCookie.from("signupToken", r.signupToken())
                .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(900).build();
        res.addHeader("Set-Cookie", ott.toString());
        res.sendRedirect(domain.signupUrl());
    }

    /** 정상 로그인: refreshToken 쿠키 + 메인으로 이동 */
    private void handleLogin(HttpServletResponse res, LoginResult r) throws IOException {
        ResponseCookie rt = ResponseCookie.from("refreshToken", r.refreshToken())
                .httpOnly(true).secure(true).sameSite("None").path("/").build();
        res.addHeader("Set-Cookie", rt.toString());
        res.sendRedirect(domain.getBase());
    }

    /** 복구 플로우: 쿼리 파라미터를 포함해 /home 으로 리다이렉트 */
    /** 복구 플로우: recoverToken은 쿠키, 나머지는 쿼리 파라미터로 /home 리다이렉트 */
    private void handleRecover(HttpServletResponse res, LoginResult r) throws IOException {
        // 1) recoverToken 쿠키로 내려주기
        ResponseCookie recover = ResponseCookie.from("recoverToken", r.recoverToken())
                .httpOnly(true)
                .secure(true)          // https 환경이 아니면 일단 false로 테스트해봐도 됨
                .sameSite("strict")      // cross-site면 None, 동일 도메인이면 Lax/Strict도 가능
                .path("/")
                .maxAge(900)           // 15분
                .build();
        res.addHeader("Set-Cookie", recover.toString());

        // 2) 쿼리 파라미터는 error/email/deletedAt만
        String url = UriComponentsBuilder.fromHttpUrl(domain.homeUrl())
                .queryParam("error", r.errorType().name()) // USER_DELETED_30D
                .queryParam("email", r.email())
                .queryParam("deletedAt", r.deletedAt())
                .build(true)
                .toUriString();

        // 3) 최종 리다이렉트
        res.sendRedirect(url);
    }

    /** 삭제/비활성 등 에러 상태: 에러 코드만 쿼리로 리다이렉트 */
    private void handleErrorRedirect(HttpServletResponse res, LoginResult r) throws IOException {
        String url = UriComponentsBuilder.fromHttpUrl(domain.homeUrl())
                .queryParam("error", r.errorType().name())
                .build(true)
                .toUriString();
        res.sendRedirect(url);
    }

    /**
     * (앱) provider access_token 직접 전달 → user 조회 → 비즈 → 응답(JSON)
     */
    @PostMapping("/{provider}/login")
    public ResponseEntity<?> appLogin(@PathVariable Provider provider,
                                      @RequestBody Map<String, String> body) {

        String providerAccessToken = body.get("accessToken");
        if (providerAccessToken == null || providerAccessToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "missing_access_token"));
        }

        // 1) access_token → user
        OauthUser u = clients.get(provider.name())
                .fetchUserByAccessToken(providerAccessToken);

        // 2) 비즈니스 로직
        LoginResult r = oauthLoginService.handleCallback(
                provider, u.externalId(), u.email(), u.image()
        );

        // 3) 결과 타입별 JSON 응답
        return switch (r.type()) {
            case SIGNUP -> ResponseEntity.ok(Map.of(
                    "type", "signup",
                    "signupToken", r.signupToken(),
                    "next", "app://signup"
            ));
            case LOGIN -> ResponseEntity.ok(Map.of(
                    "type", "login",
                    "accessToken", r.accessToken(),
                    "refreshToken", r.refreshToken()
            ));
            case RECOVER -> ResponseEntity.ok(Map.of(
                    "type", "recover",
                    "error", r.errorType().name(),              // USER_DELETED_30D
                    "recoverToken", r.recoverToken(),
                    "email", r.email(),
                    "deletedAt", r.deletedAt()
            ));
            case ERROR -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "type", "error",
                            "error", r.errorType().name()          // USER_DELETED, USER_FORBIDDEN_DISABLED
                    ));
        };
    }
}