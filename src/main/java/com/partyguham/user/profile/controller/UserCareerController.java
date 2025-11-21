package com.partyguham.user.profile.controller;

import com.partyguham.common.annotation.ApiV2Controller;
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
    public List<CareerResponse> getMyCareers(@AuthenticationPrincipal Long userId) {
        return userCareerService.getMyCareers(userId);
    }

    // CREATE/UPSERT: PRIMARY / SECONDARY 한 번에 등록/갱신
    @PostMapping
    public List<CareerResponse> upsertMyCareers(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserCareerBulkCreateRequest req) {
        return userCareerService.upsertMyCareers(userId, req);
    }

    // UPDATE: 특정 경력의 years 수정
    @PatchMapping("/{careerId}")
    public CareerResponse updateYears(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long careerId,
            @RequestParam("years") Integer years) {
        return userCareerService.updateYears(userId, careerId, years);
    }

    // DELETE: 특정 경력 삭제
    @DeleteMapping("/{careerId}")
    public void deleteCareer(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long careerId) {
        userCareerService.deleteCareer(userId, careerId);
    }
}