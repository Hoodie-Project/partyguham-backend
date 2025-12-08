package com.partyguham.recruitment.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.recruitment.dto.request.*;
import com.partyguham.recruitment.dto.response.*;
import com.partyguham.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService partyRecruitmentService;

    /**
     * 파티 모집 목록 조회 API
     */
    @GetMapping("/{partyId}/recruitments")
    public ResponseEntity<List<PartyRecruitmentsResponseDto>> getPartyRecruitments(
            @PathVariable Long partyId,
            @ModelAttribute PartyRecruitmentsRequestDto request) {
        
        return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitments(partyId, request));
    }

    @PostMapping("/{partyId}/recruitments") // 파티 모집공고 생성
    public ResponseEntity<CreatePartyRecruitmentsResponseDto> createPartyRecruitment(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid CreatePartyRecruitmentRequestDto request) {

        return ResponseEntity.ok(partyRecruitmentService.createPartyRecruitment(partyId, user.getId(), request));
    }

//    @GetMapping("/recruitments/personalized")
//    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitmentsPersonalized(
//            @AuthenticationPrincipal UserPrincipal user,
//            @ModelAttribute GetPartyRecruitmentsPersonalizedRequestDto request) {
//                return ResponseEntity.ok(partyRecruitmentService.getPersonalizedRecruitments(user.getId(), request));
//            }

    @GetMapping("/recruitments/{partyRecruitmentId}") // 파티 모집 단일 조회
    public ResponseEntity<PartyRecruitmentResponseDto> getPartyRecruitment(
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user) {
                return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitment(partyRecruitmentId, user.getId()));
            }

}
