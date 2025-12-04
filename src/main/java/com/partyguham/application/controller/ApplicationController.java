package com.partyguham.application.controller;

import com.partyguham.application.dto.req.CreatePartyApplicationRequestDto;
import com.partyguham.application.dto.res.MessageResponseDto;
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

    /** 나의 지원 내역 조회 */
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

    /** 모집에 대한 나의 지원 내역 조회 */
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

    /** 지원 취소 */
    @DeleteMapping("/applications/{partyApplicationId}")
    public ResponseEntity<Void> deletePartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyApplicationService.deleteApplication(partyId, partyApplicationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /** 지원자 최종 수락: PROCESSING -> APPROVED (+ 파티 합류) */
    @PostMapping("/applications/{partyApplicationId}/approval")
    public ResponseEntity<MessageResponseDto> approveMyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyApplicationService.approveByApplicant(partyId, partyApplicationId, user.getId());
        return ResponseEntity.ok(MessageResponseDto.of("파티 합류에 동의했습니다."));
    }

    /** 지원자 최종 거절: PROCESSING -> REJECTED */
    @PostMapping("/applications/{partyApplicationId}/rejection")
    public ResponseEntity<MessageResponseDto> rejectMyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyApplicationService.rejectByApplicant(partyId, partyApplicationId, user.getId());
        return ResponseEntity.ok(MessageResponseDto.of("참여를 거절했습니다."));
    }
}
