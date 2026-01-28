package com.partyguham.domain.user.profile.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.global.error.CommonErrorCode;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.user.profile.dto.request.BulkCareerUpdateRequest;
import com.partyguham.domain.user.profile.dto.request.UpdateCareerRequest;
import com.partyguham.domain.user.profile.dto.request.UserCareerBulkCreateRequest;
import com.partyguham.domain.user.profile.dto.response.CareerResponse;
import com.partyguham.domain.user.profile.service.UserCareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // UPDATE: 특정 경력의 수정
    @PatchMapping("/{careerId}")
    public CareerResponse updateCareer(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long careerId,
            @RequestBody UpdateCareerRequest dto
    ) {
        if (dto.isEmpty()) {
            throw new BusinessException(CommonErrorCode.BAD_REQUEST);
        }

        return userCareerService.updateCareer(user.getId(), careerId, dto);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCareers(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody BulkCareerUpdateRequest dto
    ) {
        userCareerService.updateCareers(user.getId(), dto);
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