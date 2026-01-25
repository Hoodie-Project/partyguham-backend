package com.partyguham.domain.user.profile.reader;

import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.account.repository.UserRepository;
import com.partyguham.domain.user.profile.entity.CareerType;
import com.partyguham.domain.user.profile.entity.UserCareer;
import com.partyguham.domain.user.profile.entity.UserLocation;
import com.partyguham.domain.user.profile.entity.UserPersonality;
import com.partyguham.domain.user.profile.repository.UserCareerRepository;
import com.partyguham.domain.user.profile.repository.UserLocationRepository;
import com.partyguham.domain.user.profile.repository.UserPersonalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.partyguham.domain.user.exception.UserErrorCode.*;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileReader {

    private final UserRepository userRepository;
    private final UserLocationRepository userLocationRepository;
    private final UserCareerRepository userCareerRepository;
    private final UserPersonalityRepository userPersonalityRepository;

    /**
     * 기본 조회: ID로 유저를 찾고, 없으면 전용 예외를 던집니다.
     * @param userId 유저 고유 ID
     * @return 조회된 User 엔티티
     * @throws BusinessException USER_NOT_FOUND (404)
     */
    public User read(Long userId) {
        return userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

    /**
     * 닉네임으로 유저 조회: 프로필 정보와 함께 유저를 조회합니다.
     * @param nickname 유저 닉네임
     * @return 조회된 User 엔티티
     * @throws BusinessException USER_NOT_FOUND (404)
     */
    public User readByNickname(String nickname) {
        return userRepository.findByNicknameWithProfile(nickname)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

    /**
     * 유저별 관심 지역 목록 조회
     * @param userId 유저 고유 ID
     * @return 해당 유저의 UserLocation 엔티티 리스트
     */
    public List<UserLocation> readLocationByUserId(Long userId) {
        return userLocationRepository.findByUserId(userId);
    }

    /**
     * 관심 지역 단건 조회
     * @param userLocationId 관심 지역 고유 ID
     * @return 조회된 UserLocation 엔티티
     * @throws BusinessException USER_LOCATION_NOT_FOUND (404)
     */
    public UserLocation readLocationById(Long userLocationId) {
        return userLocationRepository.findById(userLocationId)
                .orElseThrow(() -> new BusinessException(USER_LOCATION_NOT_FOUND));
    }

    /**
     * 관심 지역 조회 및 소유권 검증:
     * 특정 관심 지역이 해당 유저의 것인지 확인합니다.
     * @param userId 유저 고유 ID
     * @param userLocationId 관심 지역 고유 ID
     * @return 검증된 UserLocation 엔티티
     * @throws BusinessException USER_LOCATION_ACCESS_DENIED (403)
     */
    public UserLocation readLocationAndValidateOwner(Long userId, Long userLocationId) {
        UserLocation ul = readLocationById(userLocationId);
        if (!ul.getUser().getId().equals(userId)) {
            throw new BusinessException(USER_LOCATION_ACCESS_DENIED);
        }
        return ul;
    }

    /**
     * 유저의 모든 경력 목록 조회
     * @param userId 유저 고유 ID
     * @return 해당 유저의 UserCareer 엔티티 리스트
     */
    public List<UserCareer> readCareersByUserId(Long userId) {
        return userCareerRepository.findByUserId(userId);
    }

    /**
     * 경력 단건 조회
     * @param careerId 경력 고유 ID
     * @return 조회된 UserCareer 엔티티
     * @throws BusinessException USER_CAREER_NOT_FOUND (404)
     */
    public UserCareer readCareerById(Long careerId) {
        return userCareerRepository.findById(careerId)
                .orElseThrow(() -> new BusinessException(USER_CAREER_NOT_FOUND));
    }

    /**
     * 특정 경력 타입(대표/부경력)을 선택적으로 조회합니다.
     * 데이터의 존재 여부에 따라 분기 처리가 필요한 비즈니스 로직(Upsert 등)에서 사용합니다.
     *
     * @param userId 유저 고유 ID
     * @param type   경력 타입 (PRIMARY, SECONDARY 등)
     * @return {@link UserCareer}를 포함하거나 비어있는 {@link Optional} 객체
     */
    public Optional<UserCareer> readCareerByType(Long userId, CareerType type) {
        return userCareerRepository.findByUserIdAndCareerType(userId, type);
    }

    /**
     * 특정 경력 타입(대표/부경력)을 필수 조회합니다.
     * 데이터가 반드시 존재해야 하는 비즈니스 로직(조회, 삭제 등)에서 사용합니다.
     *
     * @param userId 유저 고유 ID
     * @param type   경력 타입 (PRIMARY, SECONDARY 등)
     * @return 조회된 {@link UserCareer} 엔티티
     * @throws BusinessException USER_CAREER_NOT_FOUND (404) - 해당 경력 정보가 없을 경우
     */
    public UserCareer getCareerByType(Long userId, CareerType type) {
        return readCareerByType(userId, type)
                .orElseThrow(() -> new BusinessException(USER_CAREER_NOT_FOUND));
    }

    /**
     * 경력 조회 및 소유권 검증:
     * 특정 경력 정보가 해당 유저의 것인지 확인합니다.
     * @param careerId 경력 고유 ID
     * @param userId 유저 고유 ID
     * @return 검증된 UserCareer 엔티티
     * @throws BusinessException USER_CAREER_ACCESS_DENIED (403)
     */
    public UserCareer readCareerAndValidateOwner(Long careerId, Long userId) {
        UserCareer uc = readCareerById(careerId);
        if (!uc.getUser().getId().equals(userId)) {
            throw new BusinessException(USER_CAREER_ACCESS_DENIED);
        }
        return uc;
    }

    /**
     * 유저의 모든 성향 응답 조회 (질문 정보 포함)
     * @param userId 유저 고유 ID
     * @return UserPersonality 리스트
     */
    public List<UserPersonality> readPersonalitiesByUserId(Long userId) {
        return userPersonalityRepository.findByUserId(userId);
    }

    /**
     * 특정 질문들에 대한 유저의 기존 응답 존재 여부 확인 (필요 시)
     */
    public List<UserPersonality> readPersonalitiesByUserIdAndQuestionIds(Long userId, List<Long> questionIds) {
        return userPersonalityRepository.findByUserIdAndQuestionIdIn(userId, questionIds);
    }


}