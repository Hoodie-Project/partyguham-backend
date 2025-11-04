package com.partyguham.party.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/parties")
public class PartyHomeController {
    @GetMapping("/search")
    public ResponseEntity<GetSearchResponseDto> getSearch(@RequestParam int page,
                                                          @RequestParam int limit,
                                                          @RequestParam(required = false) String titleSearch
    ) {
    }

    @GetMapping
    public ResponseEntity<GetPartiesResponseDto> getParties(@ModelAttribute GetPartiesRequestDto parties) {
    }

    @GetMapping("/recruitments/personalized")
    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitmentsPersonalized(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute GetPartyRecruitmentsPersonalizedRequestDto request
    ) {
    }

    @GetMapping("/recruitments")
    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitments(@ModelAttribute GetPartyRecruitmentsRequestDto request) {
    }

}
