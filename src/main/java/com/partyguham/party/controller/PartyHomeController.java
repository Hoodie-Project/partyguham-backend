package com.partyguham.party.controller;

import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.party.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.dto.party.response.GetPartiesResponseDto;
import com.partyguham.party.dto.party.response.GetSearchResponseDto;
import com.partyguham.party.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class PartyHomeController {

    private final PartyService partyService;

    @GetMapping
    public ResponseEntity<GetPartiesResponseDto> getParties( // 파티 목록 조회
                                                             @ModelAttribute GetPartiesRequestDto parties) {

        return ResponseEntity.ok(partyService.getParties(parties));
    }

    @GetMapping("/search")
    public ResponseEntity<GetSearchResponseDto> searchParties( // 파티 / 파티 모집공고 통합 검색
                                                               @RequestParam int page,
                                                               @RequestParam int limit,
                                                               @RequestParam(required = false) String titleSearch) {

        return ResponseEntity.ok(partyService.searchParties(page, limit, titleSearch));
    }


    @GetMapping("/recruitments/personalized")
    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitmentsPersonalized(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute GetPartyRecruitmentsPersonalizedRequestDto request) {
                return ResponseEntity.ok(partyRecruitmentService.getPersonalizedRecruitments(user.getId(), request));
            }

}
