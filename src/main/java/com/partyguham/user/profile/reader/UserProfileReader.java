package com.partyguham.user.profile.reader;

import com.partyguham.common.exception.BusinessException;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileReader {

    private final UserRepository userRepository;

    /**
     * 기본 조회: ID로 유저를 찾고, 없으면 전용 예외를 던집니다.
     */
    public User read(Long userId) {
        return userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    public User readByNickname(String nickname) {
        return userRepository.findByNicknameWithProfile(nickname)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }



}
