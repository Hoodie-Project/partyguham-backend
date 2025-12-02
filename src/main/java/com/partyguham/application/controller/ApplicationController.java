//package com.partyguham.party.application.controller;
//
//
//import com.partyguham.auth.jwt.UserPrincipal;
//import com.partyguham.recruitment.dto.request.CreatePartyApplicationRequestDto;
//import com.partyguham.recruitment.dto.request.PartyApplicantSearchRequestDto;
//import com.partyguham.recruitment.dto.response.CreatePartyApplicationResponseDto;
//import com.partyguham.recruitment.dto.response.PartyApplicationMeResponseDto;
//import com.partyguham.recruitment.dto.response.PartyApplicationsResponseDto;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/parties")
//public class ApplicationController {
//    @PostMapping("/{partyId}/applications/{partyApplicationId}/approval")
//    public ResponseEntity<MessageResponseDto> approvePartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @PostMapping("/{partyId}/applications/{partyApplicationId}/rejection")
//    public ResponseEntity<MessageResponseDto> rejectPartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @DeleteMapping("/{partyId}/applications/{partyApplicationId}")
//    public ResponseEntity<Void> deletePartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @PostMapping("/{partyId}/recruitments/{partyRecruitmentId}/applications")
//    public ResponseEntity<CreatePartyApplicationResponseDto> createPartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @AuthenticationPrincipal UserPrincipal user,
//            @RequestBody CreatePartyApplicationRequestDto request
//    ) {}
//
//    @GetMapping("/{partyId}/recruitments/{partyRecruitmentId}/applications")
//    public ResponseEntity<PartyApplicationsResponseDto> getPartyApplications(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @AuthenticationPrincipal UserPrincipal user,
//            @ModelAttribute PartyApplicantSearchRequestDto request
//    ) {}
//
//    @GetMapping("/{partyId}/recruitments/{partyRecruitmentId}/applications/me")
//    public ResponseEntity<PartyApplicationMeResponseDto> getMyPartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @AuthenticationPrincipal UserPrincipal user
//    ) {}
//}
