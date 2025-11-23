package com.partyguham.user.account.service;


import com.partyguham.common.entity.Status;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRecoverService {

    private final UserRepository userRepository;

    @Transactional
    public User recoverUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (user.getStatus() != Status.DELETED) {
            throw new IllegalStateException("복구 대상이 아닌 유저입니다.");
        }

        user.setStatus(Status.ACTIVE);
        // 필요하면 복구 로그 기록 등

        return user;
    }
}
