package com.partyguham.party.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.service.PartyAccessService;
import com.partyguham.party.service.PartyAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/parties/{partyId}/admin")
public class PartyAdminController {

    private final PartyAdminService partyAdminService;
    private final PartyAccessService partyAccessService;

    // 파티 수정
    @PatchMapping("/info")
    public ResponseEntity<UpdatePartyResponseDto> updateParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute UpdatePartyRequestDto request
    ) {

        return ResponseEntity.ok(partyAdminService.updateParty(partyId, user.getId(), request));
    }

    // 파티 상태 수정
    @PatchMapping("/status")
    public ResponseEntity<UpdatePartyStatusResponseDto> updatePartyStatus(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody UpdatePartyStatusRequestDto request
    ) {

        return ResponseEntity.ok(partyAdminService.updatePartyStatus(partyId, user.getId(), request));
    }

    // 이미지 삭제
    @DeleteMapping("/image")
    public ResponseEntity<Void> deletePartyImage(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyAdminService.deletePartyImage(partyId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user
    ) {

        partyAdminService.deleteParty(partyId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/delegation")
    public ResponseEntity<PartyDelegationResponseDto> delegateParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody PartyDelegationRequestDto request
            // MessageResponseDto
    ) {

        return ResponseEntity.ok(partyAdminService.delegateParty(partyId, user.getId(), request));
    }

    @GetMapping("/users")
    public ResponseEntity<GetAdminPartyUsersResponseDto> getPartyUsers(
            @PathVariable Long partyId,
            @ModelAttribute GetAdminPartyUsersRequestDto request,
            @AuthenticationPrincipal UserPrincipal user
    ) {

        return ResponseEntity.ok(partyAdminService.getPartyUsers(partyId, request, user.getId()));
    }

    @PatchMapping("/users/{partyUserId}")
    public ResponseEntity<UpdatePartyUserResponseDto> updatePartyUser(
            @PathVariable Long partyId,
            @PathVariable Long partyUserId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody UpdatePartyUserRequestDto request
            // MessageResponseDto
    ) {

        return ResponseEntity.ok(partyAdminService.updatePartyUser(partyId, partyUserId, user.getId(), request));
    }

    @DeleteMapping("/users/{partyUserId}")
    public ResponseEntity<Void> deletePartyUser(
            @PathVariable Long partyId,
            @PathVariable Long partyUserId,
            @AuthenticationPrincipal UserPrincipal user
    ) {

        partyAdminService.deletePartyUser(partyId, partyUserId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/batch-delete")
    public ResponseEntity<Void> deletePartyUserBatch(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody DeletePartyUsersBodyRequestDto request
    ) {

        partyAdminService.deletePartyUserBatch(partyId, user.getId(), request);
        return ResponseEntity.noContent().build();
    }

}
