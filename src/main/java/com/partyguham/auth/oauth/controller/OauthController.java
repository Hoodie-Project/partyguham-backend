package com.partyguham.auth.oauth.controller;

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
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequiredArgsConstructor
@ApiV2Controller
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

        // 1) state 검증(1회성)
        boolean ok = oauthStateService.validateAndConsume(provider.name(), state);
        if (!ok) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid_state");
            return;
        }
        // 2) code → user
        OauthUser u = clients.get(provider.name()).fetchUserByCode(code, OAuthFlow.LOGIN);

        // 3) 비즈: 가입자? JWT : 신규? OTT
        var r = oauthLoginService.handleCallback(
                Provider.valueOf(provider.name()),
                u.externalId(), u.email(), u.image()
        );

        if (r.isSignup()) {
            // 회원가입 필요 → signupToken 쿠키 + 회원가입 페이지로 리다이렉트
            ResponseCookie ott = ResponseCookie.from("signupToken", r.signupOtt())
                    .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(900).build();
            res.addHeader("Set-Cookie", ott.toString());

            res.sendRedirect(domain.signupUrl());
        } else {
            // 로그인 완료 → refreshToken 쿠키 + 메인 페이지로 리다이렉트
            ResponseCookie rt = ResponseCookie.from("refreshToken", r.refreshToken())
                    .httpOnly(true).secure(true).sameSite("None").path("/").build();
            res.addHeader("Set-Cookie", rt.toString());

            res.sendRedirect(domain.getBase());
        }
    }

    /**
     * (앱) provider access_token 직접 전달 → user 조회 → 비즈 → 응답(JSON)
     */
    @PostMapping("/{provider}/login")
    public ResponseEntity<?> appLogin(@PathVariable Provider provider,
                                      @RequestBody Map<String, String> body) {
        String providerAccessToken = body.get("accessToken");
        if (providerAccessToken == null || providerAccessToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "missing_access_token"));
        }

        // 1) access_token → user
        OauthUser u = clients.get(provider.name()).fetchUserByAccessToken(providerAccessToken);

        // 2) 비즈: 가입자/신규
        var r = oauthLoginService.handleCallback(
                Provider.valueOf(provider.name()),
                u.externalId(), u.email(), u.image()
        );

        // 3) 앱 응답 포맷(JSON)
        if (r.isSignup()) {
            return ResponseEntity.ok(Map.of(
                    "type", "signup",
                    "signupToken", r.signupOtt(),
                    "next", "app://signup"
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "type", "login",
                    "accessToken", r.accessToken(),
                    "refreshToken", r.refreshToken()
            ));
        }
    }
}