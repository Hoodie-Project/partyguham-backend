package com.partyguham.party.core.controller;

import com.partyguham.party.core.dto.request.*;
import com.partyguham.party.core.dto.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties")
public class PartyController {
    @GetMapping
    public ResponseEntity<GetPartiesResponseDto> getParties(@ModelAttribute GetPartiesRequestDto parties) {}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PartyResponseDto> createParty(
            @ModelAttribute PartyCreateRequestDto request, 
            @AuthenticationPrincipal UserPrincipal user) {}

    @GetMapping("/types")
    public ResponseEntity<PartyTypeResponseDto> getType() {}

    @GetMapping("/search")
    public ResponseEntity<GetSearchResponseDto> getSearch(
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) String titleSearch) {
    }

    @GetMapping("/{partyId}")
    public ResponseEntity<GetPartyResponseDto> getParty(@PathVariable Long partyId) {}

    @GetMapping("/{partyId}/users")
    public ResponseEntity<GetPartyUserResponseDto> getPartyUsers(@PathVariable Long partyId) {}

    @GetMapping("/{partyId}/users/me/authority")
    public ResponseEntity<PartyAuthorityResponseDto> getPartyAuthority(@PathVariable Long partyId) {}

    @DeleteMapping("/{partyId}/users/me")
    public ResponseEntity<Void> leaveParty(@PathVariable Long partyId, @RequestHeader("Authorization") String authorization) {
        // "Bearer " 제거
    }

}
