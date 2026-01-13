package com.partyguham.user.account.reader;

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
public class UserReader {

    private final UserRepository userRepository;

    /**
     * 기본 조회: ID로 유저를 찾고, 없으면 전용 예외를 던집니다.
     */
    public User read(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 기본 조회: Nickname 으로 유저를 찾고, 없으면 전용 예외를 던집니다.
     */
    public User readByNickname(String nickname) {
        return userRepository.findByNicknameIgnoreCase(nickname)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 계정 상태 확인 포함 조회: 활성화된 유저인지까지 체크합니다.
     */
    public User readActiveUser(Long userId) {
        User user = read(userId);
        if (user.isDeleted()) { // 엔티티 메서드 활용
            throw new BusinessException(UserErrorCode.USER_ALREADY_WITHDRAWN);
        }
        return user;
    }

    /**
     * 이메일 중복 체크: 가입 로직 등에서 활용
     */
    public void validateEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    /**
     * 닉네임 중복 체크: 가입 로직 등에서 활용
     */
    public void validateNicknameDuplicate(String nickname) {
        if (userRepository.existsByNicknameIgnoreCase(nickname)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
