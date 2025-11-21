package com.partyguham.party.core.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.party.core.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.core.dto.party.request.PartyCreateRequestDto;
import com.partyguham.party.core.dto.party.response.*;
import com.partyguham.party.core.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties")
@RequiredArgsConstructor
public class PartyController { // create → get → search → action 순서

    private final PartyService partyService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 파티생성
    public ResponseEntity<PartyResponseDto> createParty(
            @ModelAttribute PartyCreateRequestDto request,
            @AuthenticationPrincipal UserPrincipal user) {


        return ResponseEntity.ok(partyService.createParty(request, user.getId()));
    }

    @GetMapping
    public ResponseEntity<GetPartiesResponseDto> getParties( // 파티 목록 조회
            @ModelAttribute GetPartiesRequestDto parties) {

        return ResponseEntity.ok(partyService.getParties(parties));
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<GetPartyResponseDto> getParty( //파티 단일 조회
            @PathVariable Long partyId) {

        return ResponseEntity.ok(partyService.getParty(partyId));
    }

    @GetMapping("/{partyId}/users")
    public ResponseEntity<GetPartyUserResponseDto> getPartyUsers( //파티원 목록 조회
            @PathVariable Long partyId) {

        return ResponseEntity.ok(partyService.getPartyUsers(partyId));
    }

    @GetMapping("/{partyId}/users/me/authority")
    public ResponseEntity<PartyAuthorityResponseDto> getPartyAuthority( // 나의 파티 권한 조회
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(partyService.getPartyAuthority(partyId, user.getId()));
    }

    @GetMapping("/types")
    public ResponseEntity<PartyTypeResponseDto> getPartyTypes() { // 파티 타입 목록 조회

        return ResponseEntity.ok(partyService.getPartyTypes());
    }

    @GetMapping("/search")
    public ResponseEntity<GetSearchResponseDto> searchParties( // 파티 / 파티 모집공고 통합 검색
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) String titleSearch) {

        return ResponseEntity.ok(partyService.searchParties(page, limit, titleSearch));
    }

    @DeleteMapping("/{partyId}/users/me") //파티 나가기
    public ResponseEntity<Void> leaveParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user) {
                
        partyService.leaveParty(partyId, user.getId());
        
        return ResponseEntity.noContent().build();
    }
}