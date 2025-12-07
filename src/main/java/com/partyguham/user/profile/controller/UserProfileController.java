package com.partyguham.user.profile.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.user.profile.dto.request.UserProfileUpdateRequest;
import com.partyguham.user.profile.dto.response.UserProfileResponse;
import com.partyguham.user.profile.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/profile")
    public UserProfileResponse getUserProfile(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam() String nickname
    ) {
        return userProfileService.getProfileByNickname(nickname);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid UserProfileUpdateRequest req
    ) {
        userProfileService.updateProfile(user.getId(), req);
    }

}
