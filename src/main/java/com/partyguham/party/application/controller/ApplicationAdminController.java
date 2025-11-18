package com.partyguham.party.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parties/{partyId}/admin")
public class ApplicationAdminController {

    @PostMapping("/applications/{partyApplicationId}/approval")
    public ResponseEntity<MessageResponseDto> approvePartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @RequestHeader("Authorization") String authorization
    ) {}

    @PostMapping("/applications/{partyApplicationId}/rejection")
    public ResponseEntity<MessageResponseDto> rejectPartyApplication(
            @PathVariable Long partyId,
            @PathVariable Long partyApplicationId,
            @RequestHeader("Authorization") String authorization
    ) {}
}
