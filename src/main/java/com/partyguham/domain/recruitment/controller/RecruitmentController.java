package com.partyguham.domain.recruitment.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.domain.recruitment.dto.request.CreatePartyRecruitmentRequest;
import com.partyguham.domain.recruitment.dto.request.PartyRecruitmentsRequest;
import com.partyguham.domain.recruitment.dto.response.CreatePartyRecruitmentsResponse;
import com.partyguham.domain.recruitment.dto.response.PartyRecruitmentResponse;
import com.partyguham.domain.recruitment.dto.response.PartyRecruitmentsResponse;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@ApiV2Controller
@RequestMapping("/parties")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService partyRecruitmentService;

    /**
     * [파티모집] 파티 모집 목록 조회 API
     *
     * 특정 파티에 대한 파티 모집 목록을 조회하는 요청입니다.
     */
    @GetMapping("/{partyId}/recruitments")
    public ResponseEntity<List<PartyRecruitmentsResponse>> getPartyRecruitment(
            @PathVariable Long partyId,
            @ModelAttribute @Valid PartyRecruitmentsRequest request) {

        return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitments(partyId, request));
    }

    /**
     * 파티 모집 공고 생성 API
     */
    @PostMapping("/{partyId}/recruitments")
    public ResponseEntity<CreatePartyRecruitmentsResponse> createPartyRecruitment(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid CreatePartyRecruitmentRequest request) {

        return ResponseEntity.ok(partyRecruitmentService.createPartyRecruitment(partyId, user.getId(), request));
    }

    /**
     * 파티 모집 단일 조회 API
     */
    @GetMapping("/recruitments/{partyRecruitmentId}")
    public ResponseEntity<PartyRecruitmentResponse> getPartyRecruitment(@PathVariable Long partyRecruitmentId) {
                return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitment(partyRecruitmentId));
            }

}
