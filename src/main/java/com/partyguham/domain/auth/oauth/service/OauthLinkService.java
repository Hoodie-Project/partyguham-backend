package com.partyguham.domain.auth.oauth.service;

import com.partyguham.domain.auth.oauth.entity.OauthAccount;
import com.partyguham.domain.auth.oauth.entity.Provider;
import com.partyguham.domain.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.account.reader.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.partyguham.domain.auth.oauth.exception.OauthAccountErrorCode.OAUTH_LINK_CONFLICT;

/**
 * 🔗 OAuth 계정 연동 서비스
 * <p>
 * - 로그인 된 유저(userId)에 대해
 * 특정 Provider(KAKAO/GOOGLE) + externalId 계정을 묶어준다.
 * - 중복/충돌 검사를 포함.
 */
@Service
@RequiredArgsConstructor
public class OauthLinkService {

    private final UserReader userReader;

    private final OauthAccountRepository oauthAccountRepository;

    /**
     * 현재 로그인한 유저(userId)에 provider + externalId 계정을 연동
     */
    @Transactional
    public void linkAccount(Long userId,
                            Provider provider,
                            String externalId) {

        User user = userReader.read(userId);

        // 2) 이 OauthId가 이미 다른 유저에 연결되어 있는지 확인
        OauthAccount oa = oauthAccountRepository.findByProviderAndOauthId(provider, externalId)
                .map(existing -> {
                    // 이미 다른 유저에 연결된 경우 차단
                    if (existing.getUser() != null && !existing.getUser().getId().equals(userId)) {
                        throw new BusinessException(OAUTH_LINK_CONFLICT);
                    }

                    if (oauthAccountRepository.existsByUserAndProvider(user, provider)) {
                        throw new BusinessException(OAUTH_LINK_CONFLICT);
                    }

                    // 아직 user가 안 붙어있으면(회원가입 전 저장된 케이스) 지금 유저를 연결
                    if (existing.getUser() == null) {
                        existing.setUser(user); // ✅ update 대상
                    }

                    // 이미 내 user면 멱등 처리(그냥 통과)
                    return existing;
                })
                .orElseGet(() -> {
                    // 2) 완전 최초: row 자체가 없으면 생성
                    return OauthAccount.builder()
                            .provider(provider)
                            .oauthId(externalId)
                            .user(user)
                            .build();
                });

        oauthAccountRepository.save(oa);
    }
}