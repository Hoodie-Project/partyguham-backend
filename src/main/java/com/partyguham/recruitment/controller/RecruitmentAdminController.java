//package com.partyguham.party.recruitment.controller;
//
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/parties/{partyId}/admin/recruitments")
//public class RecruitmentAdminController {
//    @PatchMapping("/{partyRecruitmentId}/completed")
//    public ResponseEntity<MessageResponseDto> completePartyRecruitment(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @PostMapping("/batch-status") // 주소변경 확인요망
//    public ResponseEntity<Void> completePartyRecruitmentBatch(
//            @PathVariable Long partyId,
//            @RequestHeader("Authorization") String authorization,
//            @RequestBody PartyRecruitmentIdsBodyRequestDto request
//    ) {}
//
//    @PatchMapping("/{partyRecruitmentId}") // 주소변경 확인요망
//    public ResponseEntity<PartyRecruitmentsResponseDto> updatePartyRecruitment(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @RequestHeader("Authorization") String authorization,
//            @RequestBody CreatePartyRecruitmentRequestDto request
//    ) {}
//
//    @DeleteMapping("/{partyRecruitmentId}")
//    public ResponseEntity<Void> deletePartyRecruitment(
//            @PathVariable Long partyId,
//            @PathVariable Long partyRecruitmentId,
//            @RequestHeader("Authorization") String authorization
//    ) {}
//
//    @PostMapping("/batch-delete")
//    public ResponseEntity<Void> deletePartyRecruitmentBatch(
//            @PathVariable Long partyId,
//            @RequestHeader("Authorization") String authorization,
//            @RequestBody artyRecruitmentIdsBodyRequestDto request
//    ) {}
//}
