package com.partyguham.user.profile.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.user.profile.dto.request.UpdateCareerYearsRequest;
import com.partyguham.user.profile.dto.request.UserCareerBulkCreateRequest;
import com.partyguham.user.profile.dto.response.CareerResponse;
import com.partyguham.user.profile.service.UserCareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/users/me/careers")
public class UserCareerController {

    private final UserCareerService userCareerService;

    // READ: 내 경력 목록 조회
    @GetMapping
    public List<CareerResponse> getMyCareers(@AuthenticationPrincipal UserPrincipal user) {
        return userCareerService.getMyCareers(user.getId());
    }

    // CREATE/UPSERT: PRIMARY / SECONDARY 한 번에 등록/갱신
    @PostMapping
    public List<CareerResponse> upsertMyCareers(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody UserCareerBulkCreateRequest req) {
        return userCareerService.upsertMyCareers(user.getId(), req);
    }

    // UPDATE: 특정 경력의 years 수정
    @PatchMapping("/{careerId}")
    public CareerResponse updateYears(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long careerId,
            @RequestBody UpdateCareerYearsRequest request
    ) {
        return userCareerService.updateYears(user.getId(), careerId, request.getYears());
    }

    // DELETE: 경력 전체 삭제
    @DeleteMapping
    public void deleteAllCareers(@AuthenticationPrincipal UserPrincipal user) {
        userCareerService.deleteAllByUserId(user.getId());
    }

    // DELETE: 경력 삭제
    @DeleteMapping("/{careerId}")
    public void deleteCareer(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long careerId) {
        userCareerService.deleteCareer(user.getId(), careerId);
    }

}