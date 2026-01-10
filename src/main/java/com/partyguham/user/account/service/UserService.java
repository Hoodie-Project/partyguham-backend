package com.partyguham.user.account.service;

import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.auth.oauth.entity.OauthAccount;
import com.partyguham.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.common.entity.Status;
import com.partyguham.user.account.dto.response.MyOauthAccountResponse;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.reader.UserReader;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReader userReader;
    private final OauthAccountRepository oauthAccountRepository;

    /**
     * 회원 탈퇴
     * @param userId
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userReader.read(userId);
        user.withdraw();
    }


    /**
     * 내가 연동한 소셜 계정 목록 조회
     */
    public List<MyOauthAccountResponse> getMyOauthAccounts(Long userId) {

        List<OauthAccount> accounts = oauthAccountRepository.findByUserId(userId);

        return accounts.stream()
                .map(a -> MyOauthAccountResponse.builder()
                        .provider(a.getProvider().name().toLowerCase())
                        .build()
                )
                .toList();
    }
}