package com.partyguham.user.profile.service;

import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.dto.response.UserProfileResponse;
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
}