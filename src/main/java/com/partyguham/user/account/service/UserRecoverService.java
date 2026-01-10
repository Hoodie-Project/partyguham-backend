package com.partyguham.user.account.service;


import com.partyguham.common.entity.Status;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.reader.UserReader;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRecoverService {

    private final UserReader userReader;

    @Transactional
    public User recoverUser(Long userId) {
        User user = userReader.read(userId);

        user.restore();
        // 필요하면 복구 로그 기록 등

        return user;
    }
}
