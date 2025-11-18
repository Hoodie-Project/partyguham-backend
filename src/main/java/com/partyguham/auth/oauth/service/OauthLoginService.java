package com.partyguham.auth.oauth.service;

import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.auth.oauth.entity.OauthAccount;
import com.partyguham.auth.oauth.entity.Provider;
import com.partyguham.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.auth.ott.model.OttPayload;
import com.partyguham.auth.ott.model.OttType;
import com.partyguham.auth.ott.service.OttService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;


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
                    // 1) user가 아직 안 붙어 있는 경우 → 회원가입 필요 상태로 다시 OTT 발급
                    if (oa.getUser() == null) {
                        String ott = ottService.issue(
                                new OttPayload(OttType.SIGNUP, provider, externalId, email, image, null),
                                Duration.ofMinutes(15)
                        );
                        return Result.signupOtt(ott);
                    }

                    // 2) user가 이미 있는 경우 → 정상 로그인
                    Long userId = oa.getUser().getId();

                    // TODO: 여기서 user 상태 체크 (DELETED/INACTIVE 등) 필요
                    String at = jwtService.issueAccess(userId, "USER");
                    String rt = jwtService.issueRefresh(userId);
                    return Result.loggedIn(at, rt);
                })
                .orElseGet(() -> {
                    // 3) 완전 최초 로그인 → user=null 로 OAuthAccount만 먼저 저장
                    OauthAccount oa = OauthAccount.builder()
                            .provider(provider)
                            .externalId(externalId)
                            .user(null) // 가입 완료 때 setUser()
                            .build();
                    oauthRepo.save(oa);

                    String ott = ottService.issue(
                            new OttPayload(OttType.SIGNUP, provider, externalId, email, image, null),
                            Duration.ofMinutes(15)
                    );
                    return Result.signupOtt(ott);
                });
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