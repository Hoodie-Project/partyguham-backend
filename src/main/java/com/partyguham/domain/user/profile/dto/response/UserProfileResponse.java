package com.partyguham.domain.user.profile.dto.response;

import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.profile.entity.Gender;
import com.partyguham.domain.user.profile.entity.UserProfile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserProfileResponse(
        String email,
        String nickname,
        LocalDate birth,
        boolean birthVisible,
        Gender gender,
        boolean genderVisible,
        String portfolioTitle,
        String portfolio,
        String image,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PersonalityAnswerResponse> userPersonalities,
        List<CareerResponse> userCareers,
        List<UserLocationResponse> userLocations
) {
    public static UserProfileResponse from(User user,
                                           List<PersonalityAnswerResponse> personalities,
                                           List<CareerResponse> careers,
                                           List<UserLocationResponse> locations) {

        UserProfile profile = user.getProfile();

        // 프로필이 없을 경우를 대비한 기본값 처리
        if (profile == null) {
            return new UserProfileResponse(
                    user.getEmail(), user.getNickname(),
                    null, false, null, false, null, null, null,
                    user.getCreatedAt(), user.getUpdatedAt(),
                    null, null, null
            );
        }

        return new UserProfileResponse(
                user.getEmail(),
                user.getNickname(),
                profile.getBirth(),
                profile.isBirthVisible(),
                profile.getGender(),
                profile.isGenderVisible(),
                profile.getPortfolioTitle(),
                profile.getPortfolio(),
                profile.getImage(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                personalities,
                careers,
                locations
        );
    }
}