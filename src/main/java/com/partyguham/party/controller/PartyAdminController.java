package com.partyguham.party.controller;

import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.service.PartyAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties/{partyId}/admin")
@RequiredArgsConstructor
public class PartyAdminController {

    private final PartyAdminService partyAdminService;
    private final JwtService jwtService;

    private Long getUserIdFromToken(String authorization) {
        return jwtService.getUserIdFromAuthorization(authorization);
    }

    @PatchMapping("/info")
    public ResponseEntity<UpdatePartyResponseDto> updateParty(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @ModelAttribute UpdatePartyRequestDto request
    ) {
        Long userId = getUserIdFromToken(authorization);
        return ResponseEntity.ok(partyAdminService.updateParty(partyId, userId, request));
    }

    @PatchMapping("/status")
    public ResponseEntity<UpdatePartyStatusResponseDto> updatePartyStatus(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdatePartyStatusRequestDto request
    ) {
        Long userId = getUserIdFromToken(authorization);
        return ResponseEntity.ok(partyAdminService.updatePartyStatus(partyId, userId, request));
    }

    @DeleteMapping("/image")
    public ResponseEntity<Void> deletePartyImage(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long userId = getUserIdFromToken(authorization);
        partyAdminService.deletePartyImage(partyId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteParty(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long userId = getUserIdFromToken(authorization);
        partyAdminService.deleteParty(partyId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/delegation")
    public ResponseEntity<PartyDelegationResponseDto> delegateParty(  // MessageResponseDto
                                                                      @PathVariable Long partyId,
                                                                      @RequestHeader("Authorization") String authorization,
                                                                      @RequestBody PartyDelegationRequestDto request
    ) {
        Long userId = getUserIdFromToken(authorization);
        return ResponseEntity.ok(partyAdminService.delegateParty(partyId, userId, request));
    }

    @GetMapping("/users")
    public ResponseEntity<GetAdminPartyUsersResponseDto> getPartyUsers(
            @PathVariable Long partyId,
            @ModelAttribute GetAdminPartyUsersRequestDto request,
            @RequestHeader("Authorization") String authorization   // "Bearer " 제거
    ) {
        Long userId = getUserIdFromToken(authorization);
        return ResponseEntity.ok(partyAdminService.getPartyUsers(partyId, request, userId));
    }

    @PatchMapping("/users/{partyUserId}")
    public ResponseEntity<UpdatePartyUserResponseDto> updatePartyUser( // MessageResponseDto
                                                                       @PathVariable Long partyId,
                                                                       @PathVariable Long partyUserId,
                                                                       @RequestHeader("Authorization") String authorization,
                                                                       @RequestBody UpdatePartyUserRequestDto request
    ) {
        Long userId = getUserIdFromToken(authorization);
        return ResponseEntity.ok(partyAdminService.updatePartyUser(partyId, partyUserId, userId, request));
    }

    @DeleteMapping("/users/{partyUserId}")
    public ResponseEntity<Void> deletePartyUser(
            @PathVariable Long partyId,
            @PathVariable Long partyUserId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long userId = getUserIdFromToken(authorization);
        partyAdminService.deletePartyUser(partyId, partyUserId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/batch-delete")
    public ResponseEntity<Void> deletePartyUserBatch(
            @PathVariable Long partyId,
            @RequestHeader("Authorization") String authorization,
            @RequestBody DeletePartyUsersBodyRequestDto request
    ) {
        Long userId = getUserIdFromToken(authorization);
        partyAdminService.deletePartyUserBatch(partyId, userId, request);
        return ResponseEntity.noContent().build();
    }

}
