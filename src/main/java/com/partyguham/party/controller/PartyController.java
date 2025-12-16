package com.partyguham.party.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.party.dto.party.request.GetPartyUsersRequestDto;
import com.partyguham.party.dto.party.request.PartyCreateRequestDto;
import com.partyguham.party.dto.party.response.*;
import com.partyguham.party.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class PartyController { // create → get → search → action 순서

    private final PartyService partyService;

    /**
     * 파티 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PartyResponseDto> createParty(
            @RequestPart(required = false) MultipartFile image,
            @ModelAttribute @Valid PartyCreateRequestDto request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                partyService.createParty(request, user.getId(), image)
        );
    }

    /**
     * 파티 단일 조회
     */
    @GetMapping("/{partyId}")
    public ResponseEntity<GetPartyResponseDto> getParty(
                                                         @PathVariable Long partyId){

        return ResponseEntity.ok(partyService.getParty(partyId));
    }

    /**
     * 파티원 목록 조회
     */
    @GetMapping("/{partyId}/users")
    public ResponseEntity<GetPartyUserResponseDto> getPartyUsers( 
                                                                  @PathVariable Long partyId,
                                                                  @ModelAttribute GetPartyUsersRequestDto request) {

        request.setPartyId(partyId);
        request.applyDefaultValues();

        return ResponseEntity.ok(partyService.getPartyUsers(request, partyId));
    }

    /**
     * 나의 파티 권한 조회
     */
    @GetMapping("/{partyId}/users/me/authority")
    public ResponseEntity<PartyAuthorityResponseDto> getPartyAuthority(
                                                                        @PathVariable Long partyId,
                                                                        @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(partyService.getPartyAuthority(partyId, user.getId()));
    }

    /**
     * 파티 타입 목록 조회
     */
    @GetMapping("/types")
    public ResponseEntity<PartyTypeResponseDto> getPartyTypes() { 

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
