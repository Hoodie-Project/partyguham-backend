package com.partyguham.application.controller;

import com.partyguham.application.dto.req.CreatePartyApplicationRequestDto;
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
//
//    @PostMapping("/applications/{partyApplicationId}/approval")
//    public ResponseEntity<MessageResponseDto> approvePartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @PostMapping("/applications/{partyApplicationId}/rejection")
//    public ResponseEntity<MessageResponseDto> rejectPartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @DeleteMapping("/applications/{partyApplicationId}")
//    public ResponseEntity<Void> deletePartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @GetMapping("/recruitments/{partyRecruitmentId}/applications")
//    public ResponseEntity<PartyApplicationsResponseDto> getPartyApplications(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @AuthenticationPrincipal UserPrincipal user,
//            @ModelAttribute PartyApplicantSearchRequestDto request
//    ) {}
//
//    @GetMapping("/recruitments/{partyRecruitmentId}/applications/me")
//    public ResponseEntity<PartyApplicationMeResponseDto> getMyPartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @AuthenticationPrincipal UserPrincipal user
//    ) {}
}
