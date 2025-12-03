package com.partyguham.party.application.controller;

import com.partyguham.application.dto.req.PartyApplicantSearchRequestDto;
import com.partyguham.application.dto.res.PartyApplicationsResponseDto;
import com.partyguham.application.service.PartyApplicationService;
import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@ApiV2Controller
@RequestMapping("/api/parties/{partyId}/admin")
public class ApplicationAdminController {

    final private PartyApplicationService partyApplicationService;

    //     지원자 목록 확인
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

//    PENDING,      // 검토중
//    PROCESSING,   // 응답대기
//    APPROVED,     // 수락
//    REJECTED;     // 거절

//    // 파티장 지원자 수락
//    @PostMapping("/applications/{partyApplicationId}/approval")
//    public ResponseEntity<MessageResponseDto> approvePartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {
//    }
//
//    // 파티장 지원자 거절
//    @PostMapping("/applications/{partyApplicationId}/rejection")
//    public ResponseEntity<MessageResponseDto> rejectPartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {
//    }
}
