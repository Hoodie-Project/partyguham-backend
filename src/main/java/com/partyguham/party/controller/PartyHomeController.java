package com.partyguham.party.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.party.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.dto.party.request.GetPartyRecruitmentsRequestDto;
import com.partyguham.party.dto.party.response.GetPartiesResponseDto;
import com.partyguham.party.dto.party.response.GetPartyRecruitmentsResponseDto;
import com.partyguham.party.dto.party.response.GetSearchResponseDto;
import com.partyguham.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequestDto;
import com.partyguham.recruitment.service.RecruitmentService;
import com.partyguham.party.service.PartyService;
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
    public ResponseEntity<GetPartiesResponseDto> getParties(
            @ModelAttribute GetPartiesRequestDto parties) {

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
    public ResponseEntity<GetSearchResponseDto> searchParties(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String titleSearch) {

        return ResponseEntity.ok(partyService.searchParties(page, size, titleSearch));
    }

    /**
     * 파티 모집 공고 목록 조회
     */
    @GetMapping("/recruitments")
    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitments(@ModelAttribute @Valid GetPartyRecruitmentsRequestDto request) {

        return ResponseEntity.ok(RecruitmentService.getRecruitments(request));
    }


    /**
     * 개인화 - 파티 모집 공고 목록 조회
     */
    @GetMapping("/recruitments/personalized")
    public ResponseEntity<GetPartyRecruitmentsResponseDto> getRecruitmentsPersonalized(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute @Valid GetPartyRecruitmentsPersonalizedRequestDto request) {
        return ResponseEntity.ok(RecruitmentService.getPersonalizedRecruitments(user.getId(), request));
    }


}


