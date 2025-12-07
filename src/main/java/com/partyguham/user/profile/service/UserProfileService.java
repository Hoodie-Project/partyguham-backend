package com.partyguham.user.profile.service;

import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.dto.request.UserProfileUpdateRequest;
import com.partyguham.user.profile.dto.response.UserProfileResponse;
import com.partyguham.user.profile.entity.Gender;
import com.partyguham.user.profile.entity.UserProfile;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserPersonalityService userPersonalityService;
    private final UserCareerService userCareerService;
    private final UserLocationService userLocationService;

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        UserProfile profile = user.getProfile(); // null 가능

        return new UserProfileResponse(
                user.getEmail(),
                user.getNickname(),
                profile != null ? profile.getBirth() : null,
                profile != null && profile.isBirthVisible(),
                profile != null ? profile.getGender() : null,
                profile != null && profile.isGenderVisible(),
                profile != null ? profile.getPortfolioTitle() : null,
                profile != null ? profile.getPortfolio() : null,
                profile != null ? profile.getImage() : null,
                user.getCreatedAt(),
                user.getUpdatedAt(),
                userPersonalityService.getMyAnswers(userId),
                userCareerService.getMyCareers(userId),
                userLocationService.getMyLocations(userId)
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        UserProfile profile = user.getProfile(); // null 가능

        return new UserProfileResponse(
                user.getEmail(),
                user.getNickname(),
                profile != null ? profile.getBirth() : null,
                profile != null && profile.isBirthVisible(),
                profile != null ? profile.getGender() : null,
                profile != null && profile.isGenderVisible(),
                profile != null ? profile.getPortfolioTitle() : null,
                profile != null ? profile.getPortfolio() : null,
                profile != null ? profile.getImage() : null,
                user.getCreatedAt(),
                user.getUpdatedAt(),
                userPersonalityService.getMyAnswers(user.getId()),
                userCareerService.getMyCareers(user.getId()),
                userLocationService.getMyLocations(user.getId())
        );
    }

    @Transactional
    public void updateProfile(Long userId, UserProfileUpdateRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        UserProfile profile = user.getProfile();
        if (profile == null) {
            throw new EntityNotFoundException("profile not found");
        }

        // gender
        if (req.getGender() != null) {
            profile.setGender(Gender.from(req.getGender()));
        }

        if (req.getGenderVisible() != null) {
            profile.setGenderVisible(req.getGenderVisible());
        }

        // birth
        if (req.getBirth() != null) {
            profile.setBirth(req.getBirth());
        }

        if (req.getBirthVisible() != null) {
            profile.setBirthVisible(req.getBirthVisible());
        }

        // portfolio
        if (req.getPortfolioTitle() != null) {
            profile.setPortfolioTitle(req.getPortfolioTitle());
        }

        if (req.getPortfolio() != null) {
            profile.setPortfolio(req.getPortfolio());
        }

        // 변경 감지(Dirty Checking) 자동 반영됨
    }
}