package com.partyguham.auth.oauth.service;

import com.partyguham.auth.jwt.JwtService;
import com.partyguham.auth.oauth.entity.Provider;
import com.partyguham.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.auth.ott.model.OttPayload;
import com.partyguham.auth.ott.model.OttType;
import com.partyguham.auth.ott.service.OttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;


import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;

/**
 * OAuth 로그인 공통 비즈니스
 * - (provider, externalId)로 소셜 계정 조회
 * - 있으면: JWT(AT/RT) 발급
 * - 없으면: 회원가입용 OTT 발급(짧은 TTL)
 *
 * 컨트롤러는 platform(web/app)에 따라 이 서비스의 결과를
 * 쿠키/바디 등으로 "어떻게" 반환할지만 결정한다.
 *
 * 여기서는 편의상 응답 포맷까지 만들어 주는 respond(...) 도 제공.
 */
@Service
@RequiredArgsConstructor
public class OauthLoginService {

    private final OauthAccountRepository oauthRepo;
    private final JwtService jwtService;   // AT/RT 발급
    private final OttService ottService;   // OTT 발급(예: Redis 저장)

    /**
     * 콜백 핵심 로직: 가입자/신규 판정 + 발급
     */
    public Result handleCallback(Provider provider, String externalId, String email, String image) {
        return oauthRepo.findByProviderAndExternalId(provider, externalId)
                .map(oa -> {
                    Long userId = oa.getUser().getId();
                    String at = jwtService.issueAccess(userId, "USER");
                    String rt = jwtService.issueRefresh(userId);
                    return Result.loggedIn(at, rt);
                })
                .orElseGet(() -> {
                    String ott = ottService.issue(
                            new OttPayload(OttType.SIGNUP, provider, externalId, email, image, null),
                            Duration.ofMinutes(10)
                    );
                    return Result.signupOtt(ott);
                });
    }

    /**
     * 편의 메서드: platform(web/app)에 따라 응답 포맷 구성
     * - web: RT는 HttpOnly 쿠키, AT는 바디
     * - app: AT/RT 모두 JSON 바디
     * - signup(신규): web은 signupToken 쿠키, app은 바디
     */
    public ResponseEntity<?> respond(String provider, Result r, String platform, HttpServletResponse res) {
        if (r.signupOtt() != null) {
            if ("web".equalsIgnoreCase(platform)) {
                ResponseCookie ott = ResponseCookie.from("signupToken", r.signupOtt())
                        .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(600).build();
                res.addHeader("Set-Cookie", ott.toString());
                return ResponseEntity.ok(Map.of("type", "signup", "next", "/signup"));
            } else {
                return ResponseEntity.ok(Map.of("type", "signup", "signupToken", r.signupOtt(), "next", "app://signup"));
            }
        } else {
            if ("web".equalsIgnoreCase(platform)) {
                ResponseCookie rt = ResponseCookie.from("refreshToken", r.refreshToken())
                        .httpOnly(true).secure(true).sameSite("None").path("/").build();
                res.addHeader("Set-Cookie", rt.toString());
                return ResponseEntity.ok(Map.of("type", "login", "accessToken", r.accessToken()));
            } else {
                return ResponseEntity.ok(Map.of("type", "login", "accessToken", r.accessToken(), "refreshToken", r.refreshToken()));
            }
        }
    }


    /**
     * 컨트롤러-서비스 간 결과 DTO
     */
    public record Result(String accessToken, String refreshToken, String signupOtt) {
        public static Result loggedIn(String at, String rt) { return new Result(at, rt, null); }
        public static Result signupOtt(String ott) { return new Result(null, null, ott); }
        public String accessToken() { return accessToken; }
        public String refreshToken() { return refreshToken; }
        public String signupOtt() { return signupOtt; }
        public boolean isSignup() { return signupOtt != null; }
    }
}