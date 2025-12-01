package com.partyguham.party.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.service.PartyAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * ======================================
 *  파티원 관리(Party Member Admin) API
 * ======================================
 *
 * - 파티장(MASTER)만 접근 가능, (부파티장(DEPUTY))
 * - 파티원 목록 조회
 * - 파티원 정보 변경 (포지션 변경 등)
 * - 파티원 강제 퇴장
 * - 파티원 다수 강제 퇴장
 */
@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/parties/{partyId}/admin/users")
public class PartyUserAdminController {

    private final PartyAdminService partyAdminService;

    /**
     * 파티원 목록 조회 (필터/검색 가능)
     */
    @GetMapping
    public ResponseEntity<GetAdminPartyUsersResponseDto> getPartyUsers(
            @PathVariable Long partyId,
            @ModelAttribute GetAdminPartyUsersRequestDto request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                partyAdminService.getPartyUsers(partyId, request, user.getId())
        );
    }

    /**
     * 파티원 정보 수정 (포지션, 권한 등)
     */
    @PatchMapping("/{partyUserId}")
    public ResponseEntity<UpdatePartyUserResponseDto> updatePartyUser(
            @PathVariable Long partyId,
            @PathVariable Long partyUserId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody UpdatePartyUserRequestDto request
    ) {
        return ResponseEntity.ok(
                partyAdminService.updatePartyUser(partyId, partyUserId, user.getId(), request)
        );
    }

    /**
     * 개별 파티원 강제 퇴장
     */
    @DeleteMapping("/{partyUserId}")
    public ResponseEntity<Void> deletePartyUser(
            @PathVariable Long partyId,
            @PathVariable Long partyUserId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyAdminService.deletePartyUser(partyId, partyUserId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 파티원 다수 한 번에 강제 퇴장 (Batch)
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<Void> deletePartyUserBatch(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody DeletePartyUsersBodyRequestDto request
    ) {
        partyAdminService.deletePartyUserBatch(partyId, user.getId(), request);
        return ResponseEntity.noContent().build();
    }
}