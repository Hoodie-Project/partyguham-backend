package com.partyguham.party.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.party.dto.party.request.GetPartyUsersRequest;
import com.partyguham.party.dto.party.request.PartyCreateRequest;
import com.partyguham.party.dto.party.response.*;
import com.partyguham.party.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ===========================
 *  파티(Party) API
 * ===========================
 *
 * - 파티 생성
 * - 파티 상세 조회
 * - 파티원 목록 조회
 * - 파티 타입 목록 조회
 * - 파티 나가기
 * - 나의 파티 권한 조회
 */
@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    /**
     * 파티 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PartyResponse> createParty(
            @RequestPart(required = false) MultipartFile image,
            @ModelAttribute @Valid PartyCreateRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                partyService.createParty(request, user.getId(), image)
        );
    }

    /**
     * 닉네임으로 유저가 소속된 파티 조회
     */
    @GetMapping("/users")
    public ResponseEntity<UserJoinedPartyResponse> getPartyUsersByNickname(
            @RequestParam String nickname
    ) {
        return ResponseEntity.ok(
                partyService.getByNickname(nickname)
        );
    }

    /**
     * 파티 상세 조회
     */
    @GetMapping("/{partyId}")
    public ResponseEntity<GetPartyResponse> getParty(
            @PathVariable Long partyId) {
        return ResponseEntity.ok(partyService.getParty(partyId));
    }

    /**
     * 파티원 목록 조회
     */
    @GetMapping("/{partyId}/users")
    public ResponseEntity<GetPartyUserResponse> getPartyUsers(
            @PathVariable Long partyId,
            @ModelAttribute GetPartyUsersRequest request) {
        request.setPartyId(partyId);
        return ResponseEntity.ok(partyService.getPartyUsers(request, partyId));
    }

    /**
     * 나의 파티 권한 조회
     */
    @GetMapping("/{partyId}/users/me/authority")
    public ResponseEntity<PartyAuthorityResponse> getPartyAuthority(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(partyService.getPartyAuthority(partyId, user.getId()));
    }

    /**
     * 파티 타입 목록 조회
     */
    @GetMapping("/types")
    public ResponseEntity<PartyTypeResponse> getPartyTypes() {
        return ResponseEntity.ok(partyService.getPartyTypes());
    }

    /**
     * 파티 나가기
     */
    @DeleteMapping("/{partyId}/users/me")
    public ResponseEntity<Void> leaveParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user) {

        partyService.leaveParty(partyId, user.getId());

        return ResponseEntity.noContent().build();
    }
}
