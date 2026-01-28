package com.partyguham.domain.auth.oauth.service;

import com.partyguham.domain.auth.jwt.service.JwtService;
import com.partyguham.domain.auth.oauth.LoginResult;
import com.partyguham.domain.auth.oauth.UserErrorType;
import com.partyguham.domain.auth.oauth.entity.OauthAccount;
import com.partyguham.domain.auth.oauth.entity.Provider;
import com.partyguham.domain.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.domain.auth.ott.model.OttPayload;
import com.partyguham.domain.auth.ott.model.OttType;
import com.partyguham.domain.auth.ott.service.OttService;
import com.partyguham.global.entity.Status;
import com.partyguham.domain.user.account.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class OauthLoginService {

    private final OauthAccountRepository oauthRepo;
    private final JwtService jwtService;   // AT/RT 발급
    private final OttService ottService;   // OTT 발급(예: Redis 저장)

    @Transactional
    public LoginResult handleCallback(Provider provider, String externalId, String email, String image) {
        return oauthRepo.findByProviderAndExternalId(provider, externalId)
                .map(oa -> handleExistingAccount(oa, provider, externalId, email, image))
                .orElseGet(() -> handleFirstLogin(provider, externalId, email, image));
    }

    /**
     * 이미 OAuthAccount 가 존재하는 경우 처리
     */
    private LoginResult handleExistingAccount(OauthAccount oa,
                                              Provider provider, String externalId,
                                              String email, String image) {

        // user가 아직 안 붙은 경우 → 가입 필요
        if (oa.getUser() == null) {
            String ott = ottService.issue(
                    new OttPayload(OttType.SIGNUP, provider, externalId, email, image, null),
                    Duration.ofMinutes(15)
            );
            return LoginResult.signup(ott);
        }

        User user = oa.getUser();
        Status status = user.getStatus();

        // 삭제 계정
        if (status == Status.DELETED) {
            return LoginResult.error(UserErrorType.USER_DELETED);
        }

        // 탈퇴 후 30일 이내, 복구 가능
        if (status == Status.INACTIVE) {
            String recoverToken = ottService.issue(
                    new OttPayload(OttType.RECOVER, provider, externalId, email, image, user.getId()),
                    Duration.ofDays(30)
            );
            return LoginResult.recover(recoverToken, user.getEmail(), user.getUpdatedAt());
        }

        // ACTIVE 외 다른 상태
        if (status != Status.ACTIVE) {
            return LoginResult.error(UserErrorType.USER_FORBIDDEN_DISABLED);
        }

        // 정상 로그인
        String at = jwtService.issueAccess(user.getId(), "USER");
        String rt = jwtService.issueRefresh(user.getId());
        return LoginResult.login(at, rt);
    }

    /**
     * 최초 로그인 (OauthAccount 새로 생성)
     */
    private LoginResult handleFirstLogin(Provider provider, String externalId,
                                         String email, String image) {

        OauthAccount oa = OauthAccount.builder()
                .provider(provider)
                .externalId(externalId)
                .user(null)
                .build();
        oauthRepo.save(oa);

        String ott = ottService.issue(
                new OttPayload(OttType.SIGNUP, provider, externalId, email, image, null),
                Duration.ofMinutes(15)
        );
        return LoginResult.signup(ott);
    }
}