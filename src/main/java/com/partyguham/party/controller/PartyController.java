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

@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class PartyController { // create → get → search → action 순서

    private final PartyService partyService;

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

    @GetMapping("/{partyId}")
    public ResponseEntity<GetPartyResponse> getParty(
                                                         @PathVariable Long partyId){

        return ResponseEntity.ok(partyService.getParty(partyId));
    }

    @GetMapping("/{partyId}/users")
    public ResponseEntity<GetPartyUserResponse> getPartyUsers(
                                                                  @PathVariable Long partyId,
                                                                  @ModelAttribute GetPartyUsersRequest request) {

        request.setPartyId(partyId);
        request.applyDefaultValues();

        return ResponseEntity.ok(partyService.getPartyUsers(request, partyId));
    }

    @GetMapping("/{partyId}/users/me/authority")
    public ResponseEntity<PartyAuthorityResponse> getPartyAuthority(
                                                                        @PathVariable Long partyId,
                                                                        @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(partyService.getPartyAuthority(partyId, user.getId()));
    }

    @GetMapping("/types")
    public ResponseEntity<PartyTypeResponse> getPartyTypes() {

        return ResponseEntity.ok(partyService.getPartyTypes());
    }

    @DeleteMapping("/{partyId}/users/me") //파티 나가기
    public ResponseEntity<Void> leaveParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user) {

        partyService.leaveParty(partyId, user.getId());

        return ResponseEntity.noContent().build();
    }
}
