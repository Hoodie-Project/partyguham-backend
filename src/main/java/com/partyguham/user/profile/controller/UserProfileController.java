package com.partyguham.user.profile.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.party.dto.party.response.UserJoinedPartyResponse;
import com.partyguham.party.service.PartyService;
import com.partyguham.user.profile.dto.request.UserProfileUpdateRequest;
import com.partyguham.user.profile.dto.response.UserProfileResponse;
import com.partyguham.user.profile.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final PartyService partyService;

    @GetMapping("/me/profile")
    public UserProfileResponse getMyProfile(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return userProfileService.getMyProfile(user.getId());
    }

    @PatchMapping("/me/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid UserProfileUpdateRequest req
    ) {
        userProfileService.updateProfile(user.getId(), req);
    }

    @GetMapping("/profile")
    public UserProfileResponse getUserProfile(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("nickname") String nickname
    ) {
        return userProfileService.getProfileByNickname(nickname);
    }

    /**
     * 닉네임으로 유저가 소속된 파티 조회
     */
    @GetMapping("/parties")
    public ResponseEntity<UserJoinedPartyResponse> getPartyUsersByNickname(
            @RequestParam("nickname") String nickname
    ) {
        return ResponseEntity.ok(
                partyService.getByNickname(nickname)
        );
    }

}
