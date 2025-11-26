package com.partyguham.user.profile.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.user.profile.dto.response.UserProfileResponse;
import com.partyguham.user.profile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me/profile")
    public UserProfileResponse getMyProfile(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return userProfileService.getMyProfile(user.getId());
    }

}
