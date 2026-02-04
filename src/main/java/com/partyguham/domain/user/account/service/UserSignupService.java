package com.partyguham.domain.user.account.service;

import com.partyguham.domain.auth.jwt.service.JwtService;
import com.partyguham.domain.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.user.account.dto.request.SignUpRequest;
import com.partyguham.domain.user.account.dto.response.SignUpResponse;
import com.partyguham.domain.user.account.reader.UserReader;
import com.partyguham.domain.user.account.repository.UserRepository;
import com.partyguham.domain.user.profile.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.partyguham.domain.auth.oauth.entity.OauthAccount;
import com.partyguham.domain.auth.oauth.entity.Provider;
import com.partyguham.domain.auth.ott.model.OttPayload;
import com.partyguham.domain.user.account.entity.User;

import java.util.UUID;

import static com.partyguham.domain.auth.oauth.exception.OauthAccountErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserSignupService {

    private final UserReader userReader;
    private final UserRepository userRepository;
    private final OauthAccountRepository oauthAccountRepository;
    private final JwtService jwtService;

    /**
     * OTT 기반 필수 회원가입
     * - OttPayload 안에 있는 provider/externalId/email/image 사용
     * - Request DTO 로 nickname, gender, birth 받음
     * - User 생성 + OAuthAccount.user 연결 + JWT 발급
     */
    @Transactional
    public SignUpResponse signUpWithOtt(OttPayload ott, SignUpRequest req) {

        Provider provider = ott.provider();
        String externalId = ott.externalId();
        String email = ott.email();
        String image = ott.image();
        String nickname = req.getNickname();

        userReader.validateNicknameDuplicate(nickname);

        // 2) 아직 연결 안 된 OAuthAccount 찾아오기 (없으면 에러)
        OauthAccount oauthAccount = oauthAccountRepository
                .findByProviderAndExternalId(provider, externalId)
                .orElseThrow(() -> new BusinessException(USER_OAUTH_DATA_NOT_FOUND));

        if (oauthAccount.getUser() != null) {
            // 이미 유저가 연결된 상태인데 다시 가입 시도 → 비정상 흐름
            throw new BusinessException(OAUTH_CONFLICT);
        }

        // 3) User 생성
        User user = User.builder()
                .externalId(UUID.randomUUID().toString())
                .email(email)
                .nickname(req.getNickname())
                .build();


        // 4) Profile 생성 후 User에 attach
        UserProfile profile = new UserProfile();
        profile.setBirth(req.getBirth());
        profile.setGender(req.getGender());          // Gender enum
        profile.setImage(image);

        // 양방향 연관관계 메서드 사용 (User에 만들어둔 것)
        user.attachProfile(profile);

        userRepository.save(user);

        // 3) OAuthAccount 찾기 (provider + externalId)
        OauthAccount oauth = oauthAccountRepository
                .findByProviderAndExternalId(provider, externalId)
                .orElseThrow(() -> new BusinessException(USER_OAUTH_DATA_NOT_FOUND));

        // 4) OAuthAccount 와 User 연결
        oauth.setUser(user);
        oauthAccountRepository.save(oauth);   // dirty checking 으로도 되지만 명시적으로 저장

        // 5) JWT 발급
        String accessToken = jwtService.issueAccess(user.getId(), "USER");
        String refreshToken = jwtService.issueRefresh(user.getId());

        return new SignUpResponse(accessToken, refreshToken);
    }
}