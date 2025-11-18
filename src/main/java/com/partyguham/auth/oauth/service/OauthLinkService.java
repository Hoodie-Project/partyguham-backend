package com.partyguham.auth.oauth.service;

import com.partyguham.auth.oauth.entity.OauthAccount;
import com.partyguham.auth.oauth.entity.Provider;
import com.partyguham.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🔗 OAuth 계정 연동 서비스
 *
 * - 로그인 된 유저(userId)에 대해
 *   특정 Provider(KAKAO/GOOGLE) + externalId 계정을 묶어준다.
 * - 중복/충돌 검사를 포함.
 */
@Service
@RequiredArgsConstructor
public class OauthLinkService {

    private final OauthAccountRepository oauthAccountRepository;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 유저(userId)에 provider + externalId 계정을 연동
     */
    @Transactional
    public void linkAccount(Long userId,
                            Provider provider,
                            String externalId) {

        // 1) 현재 로그인한 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        // 2) 이 externalId가 이미 다른 유저에 연결되어 있는지 확인
        oauthAccountRepository.findByProviderAndExternalId(provider, externalId)
                .ifPresent(existing -> {
                    // 이미 연동된 계정인데, 주인이 내가 아니면 막기
                    if (!existing.getUser().getId().equals(userId)) {
                        throw new IllegalStateException("이미 다른 계정에 연결된 OAuth 계정입니다.");
                    }
                    // 주인이 나면 그대로 두고 그냥 성공으로 간주 (멱등성 보장)
                });

        // 3) 이 유저가 같은 provider를 이미 연동했는지 체크 (카카오 두 번 연동 방지 등)
        if (oauthAccountRepository.existsByUserAndProvider(user, provider)) {
            throw new IllegalStateException("이미 해당 OAuth 제공자가 연동되어 있습니다.");
        }

        // 4) 실제 OauthAccount 생성 후 저장
        OauthAccount oa = OauthAccount.builder()
                .user(user)
                .provider(provider)
                .externalId(externalId)
                // accessToken 저장이 필요하면 필드 추가해서 여기서 넣으면 됨
                .build();

        oauthAccountRepository.save(oa);
    }
}