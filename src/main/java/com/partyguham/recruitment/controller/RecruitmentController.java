package com.partyguham.recruitment.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.recruitment.dto.request.*;
import com.partyguham.recruitment.dto.response.*;
import com.partyguham.recruitment.service.RecruitmentService;
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
    public ResponseEntity<List<PartyRecruitmentsResponseDto>> getPartyRecruitment(
            @PathVariable Long partyId,
            @ModelAttribute @Valid PartyRecruitmentsRequestDto request) {

        return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitments(partyId, request));
    }

    /**
     * 파티 모집 공고 생성 API
     */
    @PostMapping("/{partyId}/recruitments")
    public ResponseEntity<CreatePartyRecruitmentsResponseDto> createPartyRecruitment(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid CreatePartyRecruitmentRequestDto request) {

        return ResponseEntity.ok(partyRecruitmentService.createPartyRecruitment(partyId, user.getId(), request));
    }

    /**
     * 파티 모집 단일 조회 API
     */
    @GetMapping("/recruitments/{partyRecruitmentId}")
    public ResponseEntity<PartyRecruitmentResponseDto> getPartyRecruitment(@PathVariable Long partyRecruitmentId) {
                return ResponseEntity.ok(partyRecruitmentService.getPartyRecruitment(partyRecruitmentId));
            }

}
