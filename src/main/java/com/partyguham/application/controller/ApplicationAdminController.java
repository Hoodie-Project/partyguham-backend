package com.partyguham.application.controller;

import com.partyguham.application.dto.req.PartyApplicantSearchRequestDto;
import com.partyguham.application.dto.res.MessageResponseDto;
import com.partyguham.application.dto.res.PartyApplicationsResponseDto;
import com.partyguham.application.service.PartyApplicationService;
import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/parties/{partyId}/admin")
public class ApplicationAdminController {

    final private PartyApplicationService partyApplicationService;

    /** 지원자 목록 확인 */
    @GetMapping("/recruitments/{partyRecruitmentId}/applications")
    public ResponseEntity<PartyApplicationsResponseDto> getPartyApplications(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute PartyApplicantSearchRequestDto request
    ) {
        return ResponseEntity.ok(
                partyApplicationService.getPartyApplications(
                        partyId,
                        partyRecruitmentId,
                        user.getId(),
                        request
                )
        );
    }

    /** 파티장 지원자 수락: PENDING -> PROCESSING */
    @PostMapping("/applications/{partyApplicationId}/approval")
    public ResponseEntity<MessageResponseDto> approvePartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyApplicationService.approveByManager(partyId, partyApplicationId, user.getId());
        return ResponseEntity.ok(MessageResponseDto.of("지원 요청을 승인했습니다."));
    }

    /** 파티장 지원자 거절: PENDING -> REJECTED */
    @PostMapping("/applications/{partyApplicationId}/rejection")
    public ResponseEntity<MessageResponseDto> rejectPartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyApplicationService.rejectByManager(partyId, partyApplicationId, user.getId());
        return ResponseEntity.ok(MessageResponseDto.of("지원 요청을 거절했습니다."));
    }
}
