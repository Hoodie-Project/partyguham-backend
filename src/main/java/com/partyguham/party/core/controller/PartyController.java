package com.partyguham.party.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties")
public class PartyController {

    @PostMapping
    public ResponseEntity<PartyResponse> createParty(@RequestBody PartyRequest request) {}

    @GetMapping("/types")
    public ResponseEntity<PartyTypeResponse> getType() {} // 단일객체반환

    @GetMapping("/{partyId}")
    public ResponseEntity<PartyResponse> getParty(@PathVariable Long partyId) {}

    @GetMapping("/{partyId}/users")
    public ResponseEntity<List<PartyUserResponse>> getPartyUsers(@PathVariable Long partyId) {}

    @GetMapping("/{partyId}/users/me/authority")
    public ResponseEntity<PartyMyAuthority> getPartyMyAuthority(@PathVariable Long partyId) {}

    @DeleteMapping("/{partyId}/users/me")
    public ResponseEntity<Void> leaveParty(@PathVariable Long partyId) {}

}
