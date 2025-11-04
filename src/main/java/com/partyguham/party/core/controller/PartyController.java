package com.partyguham.party.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties")
public class PartyController {

    @PostMapping
    public ResponseEntity<PartyResponseDto> createParty(@RequestBody PartyCreateRequestDto request, @AuthenticationPrincipal UserPrincipal user) {}

    @GetMapping("/types")
    public ResponseEntity<PartyTypeResponseDto> getType() {}

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
