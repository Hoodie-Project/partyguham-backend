package com.partyguham.recruitment.controller;


import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.party.service.PartyService;
import com.partyguham.recruitment.dto.request.*;
import com.partyguham.recruitment.dto.response.*;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.service.PartyRecruitmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties")
public class RecruitmentController {

    private PartyRecruitmentService partyRecruitmentService;


    @GetMapping("/{partyId}/recruitments")
    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitments(
            @PathVariable Long partyId,
            @ModelAttribute GetPartyRecruitmentsRequestDto request) {
        
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
