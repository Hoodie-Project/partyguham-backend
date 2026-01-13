package com.partyguham.user.profile.reader;

import com.partyguham.common.exception.BusinessException;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.entity.UserLocation;
import com.partyguham.user.profile.repository.UserLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.partyguham.user.exception.UserErrorCode.*;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileReader {

    private final UserRepository userRepository;
    private final UserLocationRepository userLocationRepository;
    /**
     * 기본 조회: ID로 유저를 찾고, 없으면 전용 예외를 던집니다.
     */
    public User read(Long userId) {
        return userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

    public User readByNickname(String nickname) {
        return userRepository.findByNicknameWithProfile(nickname)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

    public List<UserLocation> readLocationByUserId(Long userId) {
        return userLocationRepository.findByUserId(userId);
    }

    public UserLocation readLocationById(Long userLocationId) {
        return userLocationRepository.findById(userLocationId)
                .orElseThrow(() -> new BusinessException(USER_LOCATION_NOT_FOUND));
    }

    public UserLocation readLocationAndValidateOwner(Long userId, Long userLocationId) {
        UserLocation ul = readLocationById(userLocationId);
        if (!ul.getUser().getId().equals(userId)) {
            throw new BusinessException(USER_LOCATION_ACCESS_DENIED);
        }
        return ul;
    }

}
