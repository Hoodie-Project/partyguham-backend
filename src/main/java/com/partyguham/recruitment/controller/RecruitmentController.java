package com.partyguham.recruitment.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.recruitment.dto.request.*;
import com.partyguham.recruitment.dto.response.*;
import com.partyguham.recruitment.service.PartyRecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class RecruitmentController {

    private final PartyRecruitmentService partyRecruitmentService;

    @GetMapping("/{partyId}/recruitments")
    public ResponseEntity<PartyRecruitmentsResponseDto> getPartyRecruitments(
            @PathVariable Long partyId,
            @ModelAttribute PartyRecruitmentsRequestDto request) {
        
        return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitments(partyId, request));
    }

    @PostMapping("/{partyId}/recruitments")
    public ResponseEntity<CreatePartyRecruitmentsResponseDto> createPartyRecruitment( // 파티 모집 생성하기
                                                                                      @PathVariable Long partyId,
                                                                                      @AuthenticationPrincipal UserPrincipal user,
                                                                                      @RequestBody CreatePartyRecruitmentRequestDto request) {

        return ResponseEntity.ok(partyRecruitmentService.createPartyRecruitment(partyId, user.getId(), request));
    }

//    @GetMapping("/recruitments/personalized")
//    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitmentsPersonalized(
//            @AuthenticationPrincipal UserPrincipal user,
//            @ModelAttribute GetPartyRecruitmentsPersonalizedRequestDto request) {
//                return ResponseEntity.ok(partyRecruitmentService.getPersonalizedRecruitments(user.getId(), request));
//            }

    @GetMapping("/recruitments/{partyRecruitmentId}")
    public ResponseEntity<PartyRecruitmentResponseDto> getPartyRecruitment(
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user) {
                return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitment(partyRecruitmentId, user.getId()));
            }






}
