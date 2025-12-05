package com.partyguham.party.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import com.partyguham.party.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.dto.party.request.GetPartyUsersRequestDto;
import com.partyguham.party.dto.party.request.PartyCreateRequestDto;
import com.partyguham.party.dto.party.response.*;
import com.partyguham.party.service.PartyService;
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
    private final S3FileService s3FileService;

    @PostMapping() // 파티생성
    public ResponseEntity<PartyResponseDto> createParty(
            @RequestPart MultipartFile image,
            @ModelAttribute PartyCreateRequestDto request,
            @AuthenticationPrincipal UserPrincipal user) {

        String key = s3FileService.upload(image, S3Folder.PARTY);

        return ResponseEntity.ok(partyService.createParty(request, user.getId(), key));
    }

    @GetMapping
    public ResponseEntity<GetPartiesResponseDto> getParties( // 파티 목록 조회
                                                             @ModelAttribute GetPartiesRequestDto parties) {

        return ResponseEntity.ok(partyService.getParties(parties));
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<GetPartyResponseDto> getParty( //파티 단일 조회
                                                         @PathVariable Long partyId){

        return ResponseEntity.ok(partyService.getParty(partyId));
    }

    @GetMapping("/{partyId}/users")
    public ResponseEntity<GetPartyUserResponseDto> getPartyUsers( //파티원 목록 조회
                                                                  @PathVariable Long partyId,
                                                                  @ModelAttribute GetPartyUsersRequestDto request) {

        request.setPartyId(partyId);
        request.applyDefaultValues();

        return ResponseEntity.ok(partyService.getPartyUsers(request, partyId));
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
