package com.partyguham.domain.user.profile.service;

import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.profile.dto.request.UserProfileUpdateRequest;
import com.partyguham.domain.user.profile.dto.response.UserProfileResponse;
import com.partyguham.domain.user.profile.reader.UserProfileReader;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileReader UserProfileReader;

    private final UserPersonalityService userPersonalityService;
    private final UserCareerService userCareerService;
    private final UserLocationService userLocationService;

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        User user = UserProfileReader.read(userId);

        return new UserProfileResponse(
                user.getEmail(),
                user.getNickname(),

                user.getProfile() != null ? user.getProfile().getBirth() : null,
                user.getProfile() != null && user.getProfile().isBirthVisible(),
                user.getProfile() != null ? user.getProfile().getGender() : null,
                user.getProfile() != null && user.getProfile().isGenderVisible(),
                user.getProfile() != null ? user.getProfile().getPortfolioTitle() : null,
                user.getProfile() != null ? user.getProfile().getPortfolio() : null,
                user.getProfile() != null ? user.getProfile().getImage() : null,
                user.getCreatedAt(),
                user.getUpdatedAt(),
                userPersonalityService.getMyAnswers(userId),
                userCareerService.getMyCareers(userId),
                userLocationService.getMyLocations(userId)
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByNickname(String nickname) {
        User user = UserProfileReader.readByNickname(nickname);

        Long userId = user.getId();

        // 2. 각 도메인 서비스로부터 부가 정보 조회
        var personalities = userPersonalityService.getMyAnswers(userId);
        var careers = userCareerService.getMyCareers(userId);
        var locations = userLocationService.getMyLocations(userId);

        // 3. DTO 팩토리 메서드로 조합하여 반환
        return UserProfileResponse.from(user, personalities, careers, locations);
    }

    @Transactional
    public void updateProfile(Long userId, UserProfileUpdateRequest req) {

        User user = UserProfileReader.read(userId);

        if (user.getProfile() == null) {
            throw new EntityNotFoundException("profile not found");
        }

        // gender
        if (req.getGender() != null) {
            user.getProfile().setGender(req.getGender());
        }

        if (req.getGenderVisible() != null) {
            user.getProfile().setGenderVisible(req.getGenderVisible());
        }

        // birth
        if (req.getBirth() != null) {
            user.getProfile().setBirth(req.getBirth());
        }

        if (req.getBirthVisible() != null) {
            user.getProfile().setBirthVisible(req.getBirthVisible());
        }

        // portfolio
        if (req.getPortfolioTitle() != null) {
            user.getProfile().setPortfolioTitle(req.getPortfolioTitle());
        }

        if (req.getPortfolio() != null) {
            user.getProfile().setPortfolio(req.getPortfolio());
        }
    }
}