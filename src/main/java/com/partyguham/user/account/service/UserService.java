package com.partyguham.user.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원가입 로직 (간단한 스텁)
 * - 실제 구현은 UserRepository, OauthAccountRepo, JwtService(Access/Refresh 발급) 등을 호출
 */
@Service
@RequiredArgsConstructor
public class UserService {

    public Object createUserAndLogin(Long oauthId, String email, String image,
                                     String nickname, String gender, String birth) {
        // 1) oauthId 기반으로 User가 있는지 확인 (없으면 새로 생성)
        // 2) UserProfile 생성/저장
        // 3) OauthAccount 연동
        // 4) AccessToken 및 RefreshToken 발급 (JwtService)
        // 5) RefreshToken은 HttpOnly Cookie로 세팅(컨트롤러에서)
        // 현재 스텁은 토큰/유저 객체 대신 간단한 응답 반환
        return java.util.Map.of("status", "ok", "accessToken", "dummy-access-token");
    }
}