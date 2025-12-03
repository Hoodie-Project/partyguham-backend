package com.partyguham.application.controller;

import com.partyguham.application.dto.req.CreatePartyApplicationRequestDto;
import com.partyguham.application.dto.res.PartyApplicationMeResponseDto;
import com.partyguham.application.service.PartyApplicationService;
import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/parties/{partyId}")
public class ApplicationController {

    final private PartyApplicationService partyApplicationService;

    // 모집 지원
    @PostMapping("/recruitments/{partyRecruitmentId}/applications")
    public ResponseEntity<Void> createPartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody CreatePartyApplicationRequestDto request
    ) {
        partyApplicationService.applyToRecruitment(partyId, partyRecruitmentId, user.getId(), request);
        return ResponseEntity.noContent().build(); // 204
    }

    // 특정 모집에 대한 나의 지원 확인
    @GetMapping("/recruitments/{partyRecruitmentId}/applications/me")
    public ResponseEntity<PartyApplicationMeResponseDto> getMyPartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                partyApplicationService.getMyApplication(
                        partyId,
                        partyRecruitmentId,
                        user.getId()
                )
        );
    }

    // 지원 취소
    @DeleteMapping("/applications/{partyApplicationId}")
    public ResponseEntity<Void> deletePartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyApplicationService.deleteApplication(partyId, partyApplicationId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
