package com.partyguham.user.account.service;

import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.auth.oauth.repository.OauthAccountRepository;
import com.partyguham.common.entity.Status;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OauthAccountRepository oauthAccountRepository;
    private final JwtService jwtService;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 소프트 삭제 방식
        user.setStatus(Status.DELETED); // ACTIVE → DELETED

        // 또는 pull out personal info
        user.setEmail("deleted_" + user.getId());
        user.setNickname("탈퇴유저#" + user.getId());
    }
}