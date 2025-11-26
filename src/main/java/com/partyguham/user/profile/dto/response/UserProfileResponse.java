package com.partyguham.user.profile.dto.response;

import com.partyguham.user.profile.entity.Gender;

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
) {}