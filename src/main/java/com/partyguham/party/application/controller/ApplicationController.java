//package com.partyguham.party.application.controller;
//
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/parties/{partyId}/applications")
//public class ApplicationController {
//    @PostMapping("/{partyApplicationId}/approval")
//    public ResponseEntity<MessageResponseDto> approvePartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @PostMapping("/{partyApplicationId}/rejection")
//    public ResponseEntity<MessageResponseDto> rejectPartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @DeleteMapping("/{partyApplicationId}")
//    public ResponseEntity<Void> deletePartyApplication(
//            @PathVariable Long partyId,
//            @PathVariable Long partyApplicationId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//}
