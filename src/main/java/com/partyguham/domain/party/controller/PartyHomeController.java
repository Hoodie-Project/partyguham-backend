package com.partyguham.domain.party.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.party.dto.party.request.GetPartiesRequest;
import com.partyguham.domain.party.dto.party.request.GetPartyRecruitmentsRequest;
import com.partyguham.domain.party.dto.party.response.GetPartiesResponse;
import com.partyguham.domain.party.dto.party.response.GetPartyRecruitmentsResponse;
import com.partyguham.domain.party.dto.party.response.GetSearchResponse;
import com.partyguham.domain.party.service.PartyService;
import com.partyguham.domain.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequest;
import com.partyguham.domain.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 파티 홈(라운지) 페이지 관련 컨트롤러
 */
@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class PartyHomeController {

    private final PartyService partyService;
    private final RecruitmentService RecruitmentService;
    /**
     * 파티 목록 조회 
     */
    @GetMapping
    public ResponseEntity<GetPartiesResponse> getParties(
            @ModelAttribute @Valid GetPartiesRequest parties) {

        return ResponseEntity.ok(partyService.getParties(parties));
    }

    /**
     * 파티/파티 모집공고 통합검색
     * 
     * 파티 제목을 기준으로 party 목록을 조회하고,
     * 해당 파티의 모집 공고 목록을 조회합니다.
     * (default: 생성일 기준, 내림차순 정렬)
     */
    @GetMapping("/search")
    public ResponseEntity<GetSearchResponse> searchParties(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String titleSearch) {

        return ResponseEntity.ok(partyService.searchParties(page, size, titleSearch));
    }

    /**
     * 파티 모집 공고 목록 조회
     */
    @GetMapping("/recruitments")
    public ResponseEntity<GetPartyRecruitmentsResponse> getRecruitments(@ModelAttribute @Valid GetPartyRecruitmentsRequest request) {

        return ResponseEntity.ok(RecruitmentService.getRecruitments(request));
    }


    /**
     * 개인화 - 파티 모집 공고 목록 조회
     */
    @GetMapping("/recruitments/personalized")
    public ResponseEntity<GetPartyRecruitmentsResponse> getRecruitmentsPersonalized(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute @Valid GetPartyRecruitmentsPersonalizedRequest request) {
        return ResponseEntity.ok(RecruitmentService.getPersonalizedRecruitments(user.getId(), request));
    }


}


