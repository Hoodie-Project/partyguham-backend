package com.partyguham.party.core.controller;

import com.partyguham.party.core.dto.partyAdmin.request.*;
import com.partyguham.party.core.dto.partyAdmin.response.*;
import com.partyguham.party.core.service.PartyAdminService;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties/{partyId}/admin")
public class PartyAdminController {

    @PatchMapping("/info")
    public ResponseEntity<UpdatePartyResponseDto> updateParty(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute UpdatePartyRequestDto request
    ) {}

    @PatchMapping("/status")
    public ResponseEntity<UpdatePartyStatusResponseDto> updatePartyStatus(
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

    @PatchMapping("/delegation")
    public ResponseEntity<PartyDelegationResponseDto> delegateParty( // MessageResponseDto
                                                                     @PathVariable Long partyId,
                                                                     @RequestHeader("Authorization") String authorization,
                                                                     @RequestBody PartyDelegationRequestDto request
    ) {}

    @GetMapping("/users")
    public ResponseEntity<GetAdminPartyUsersResponseDto> getPartyUsers(
            @PathVariable Long partyId,
            @ModelAttribute GetAdminPartyUsersRequestDto request,
            @RequestHeader("Authorization") String authorization  // "Bearer " 제거
    ) {}

    @PatchMapping("/users/{partyUserId}")
    public RequestEntity<UpdatePartyUserResponseDto> updatePartyUser( // MessageResponseDto
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

}
