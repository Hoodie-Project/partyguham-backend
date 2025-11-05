package com.partyguham.party.recruitment.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties")
public class PartyRecruitmentController {
    @GetMapping("/recruitments/{partyRecruitmentId}")
    public ResponseEntity<PartyRecruitmentResponseDto> getPartyRecruitment(
            @PathVariable Long partyRecruitmentId,
            @RequestHeader(value = "Authorization") String authorization
    ) {}

    @GetMapping("/{partyId}/recruitments")
    public ResponseEntity<List<PartyRecruitmentsResponseDto>> getPartyRecruitmentList(
            @PathVariable Long partyId,
            @ModelAttribute PartyRecruitmentSearchRequestDto request
    ) {}

    @PostMapping("/{partyId}/recruitments")
    public ResponseEntity<CreatePartyRecruitmentsResponseDto> createPartyRecruitment(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreatePartyRecruitmentRequestDto request
    ) {}

    @PostMapping("/{partyId}/recruitments/{partyRecruitmentId}/applications")
    public ResponseEntity<CreatePartyApplicationResponseDto> createPartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreatePartyApplicationRequestDto request
    ) {}

    @GetMapping("/{partyId}/recruitments/{partyRecruitmentId}/applications")
    public ResponseEntity<PartyApplicationsResponseDto> getPartyApplications(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute PartyApplicantSearchRequestDto request
    ) {}

    @GetMapping("/{partyId}/recruitments/{partyRecruitmentId}/applications/me")
    public ResponseEntity<PartyApplicationMeResponseDto> getMyPartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @RequestHeader("Authorization") String authorization
    ) {}
}