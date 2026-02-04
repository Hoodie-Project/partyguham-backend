package com.partyguham.domain.user.profile.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.user.profile.dto.request.UserLocationBulkRequest;
import com.partyguham.domain.user.profile.dto.response.UserLocationResponse;
import com.partyguham.domain.user.profile.service.UserLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/users/me/locations")
public class UserLocationController {

    private final UserLocationService userLocationService;

    /** 내 관심지역 조회 */
    @GetMapping
    public List<UserLocationResponse> getMyLocations(@AuthenticationPrincipal UserPrincipal user) {
        return userLocationService.getMyLocations(user.getId());
    }

    /**
     * 내 관심지역 설정 (전체 교체)
     * - body.locationIds: [1, 2, 3] 형식
     */
    @PostMapping
    public List<UserLocationResponse> putMyLocations(@AuthenticationPrincipal UserPrincipal user,
                               @RequestBody UserLocationBulkRequest req) {
        return userLocationService.setMyLocations(user.getId(), req);
    }

    /** 내 관심지역 전체 삭제 */
    @DeleteMapping
    public void deleteAllMyLocations(@AuthenticationPrincipal UserPrincipal user) {
        userLocationService.deleteAllMyLocations(user.getId());
    }

    /** 개별 관심지역 삭제 */
    @DeleteMapping("/{userLocationId}")
    public void deleteMyLocation(@AuthenticationPrincipal UserPrincipal user,
                                 @PathVariable Long userLocationId) {
        userLocationService.deleteMyLocation(user.getId(), userLocationId);
    }
}