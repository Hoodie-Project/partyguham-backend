package com.partyguham.party.core.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties/{partyId}/admin")
public class PartyAdminController {
    @GetMapping("/users")
    public ResponseEntity<GetAdminPartyUsersResponseDto> getPartyUsers(
            @PathVariable Long partyId,
            @ModelAttribute GetAdminPartyUsersRequestDto request,
            @RequestHeader("Authorization") String authorization  // "Bearer " 제거
    ) {}

    @PatchMapping("/info")
    public ResponseEntity<UpdatePartyResponseDto> updateParty(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute UpdatePartyRequestDto request
    ) {}

    @PatchMapping("/status")
    public ResponseEntity<UpdatePartyResponseDto> updatePartyStatus(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdatePartyStatusRequestDto request
    ) {}

    @DeleteMapping("/image")
    public ResponseEntity<Void> deletePartyImage(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @DeleteMapping
    public ResponseEntity<Void> deleteParty(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @PatchMapping("/users/{partyUserId}")
    public RequestEntity<MessageResponseDto> updatePartyUser( // common
            @PathVariable Long partyId,
            @PathVariable Long partyUserId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdatePartyUserRequestDto request
    ) {}

    @DeleteMapping("/users/{partyUserId}")
    public ResponseEntity<Void> deletePartyUser(
            @PathVariable Long partyId,
            @PathVariable Long partyUserId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @PostMapping("/users/batch-delete")
    public ResponseEntity<Void> deletePartyUserBatch(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody DeletePartyUsersBodyRequestDto request
    ) {}

    @PatchMapping("/recruitment/{partyRecruitmentId}/completed")
    public ResponseEntity<MessageResponseDto> completePartyRecruitment(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @PostMapping("/recruitment/batch-status")
    public ResponseEntity<Void> completePartyRecruitmentBatch(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody PartyRecruitmentIdsBodyRequestDto request
    ) {}

    @PatchMapping("/recruitments/{partyRecruitmentId}")
    public ResponseEntity<PartyRecruitmentsResponseDto> updatePartyRecruitment(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreatePartyRecruitmentRequestDto request
    ) {}

    @DeleteMapping("/recruitments/{partyRecruitmentId}")
    public ResponseEntity<Void> deletePartyRecruitment(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @PostMapping("/recruitments/batch-delete")
    public ResponseEntity<Void> deletePartyRecruitmentBatch(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody artyRecruitmentIdsBodyRequestDto request
    ) {}

    @PostMapping("/applications/{partyApplicationId}/approval")
    public ResponseEntity<MessageResponseDto> approvePartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @PostMapping("/applications/{partyApplicationId}/rejection")
    public ResponseEntity<MessageResponseDto> rejectPartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @PatchMapping("/delegation")
    public ResponseEntity<MessageResponseDto> delegateParty(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody PartyDelegationRequestDto request
    ) {}

}
