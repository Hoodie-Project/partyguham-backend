package com.partyguham.recruitment.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.common.dto.MessageResponseDto;
import com.partyguham.recruitment.dto.request.CreatePartyRecruitmentRequestDto;
import com.partyguham.recruitment.dto.request.PartyRecruitmentIdsBodyRequestDto;
import com.partyguham.recruitment.service.RecruitmentAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequestMapping("/parties/{partyId}/admin/recruitments")
@RequiredArgsConstructor
public class RecruitmentAdminController {

    private final RecruitmentAdminService recruitmentAdminService;

    /**
     * 파티 모집 공고 완료 처리
     *
     * @param partyId - (PK) 파티
     * @param partyRecruitmentId - (PK) 파티 모집
     * @param user - 인증된 사용자 (Spring Security filter를 통해 JWT 토큰에서 추출된 userId)
     * @return 완료 메시지
     */
    @PatchMapping("/{partyRecruitmentId}/completed") 
    public ResponseEntity<MessageResponseDto> completePartyRecruitment(  
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        recruitmentAdminService.completePartyRecruitment(partyId, partyRecruitmentId, user.getId());
        return ResponseEntity.ok(MessageResponseDto.builder()
                .message("파티모집 공고를 완료 하였습니다.")
                .build());
    }

    /**
     * 파티 모집 다수 데이터 (완료)상태 변경
     * 
     * @param partyId - (PK) 파티
     * @param user - 인증된 사용자      
     * @param request - 상태 변경할 모집 공고 ID 리스트
     * @return void
     */
    @PostMapping("/batch-status") 
    public ResponseEntity<Void> completePartyRecruitmentBatch(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid PartyRecruitmentIdsBodyRequestDto request
    ) {
        recruitmentAdminService.completePartyRecruitmentBatch(partyId, user.getId(), request);
        return ResponseEntity.ok(null);
    }

    /**
     * 파티 모집 수정
     * 
     * @param partyId - (PK) 파티
     * @param partyRecruitmentId - (PK) 파티 모집
     * @param user - 인증된 사용자
     * @param request - 수정할 모집공고 데이터
     * @return 수정된 파티 모집 데이터
     */
    @PatchMapping("/{partyRecruitmentId}") // 파티 모집 수정
    public ResponseEntity<CreatePartyRecruitmentRequestDto> updatePartyRecruitment(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid CreatePartyRecruitmentRequestDto request
    ) {
        return ResponseEntity.ok(recruitmentAdminService.updatePartyRecruitment(partyId, partyRecruitmentId, user.getId(), request));
    }

    /** 
     * 파티 모집 삭제
     * 
     * @param partyId - (PK) 파티
     * @param partyRecruitmentId - (PK) 파티 모집
     * @param user - 인증된 사용자
     * @return void
     */
    @DeleteMapping("/{partyRecruitmentId}")
    public ResponseEntity<Void> deletePartyRecruitment(
            @PathVariable Long partyId,
            @PathVariable Long partyRecruitmentId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        recruitmentAdminService.deletePartyRecruitment(partyId, partyRecruitmentId, user.getId());
        return ResponseEntity.ok(null);
    }

    /**
     * 파티 모집 다수 삭제
     * 
     * @param partyId - (PK) 파티 
     * @param user - 인증된 사용자
     * @param request - 삭제할 모집 공고 ID 리스트
     * @return void
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<Void> deletePartyRecruitmentBatch(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid PartyRecruitmentIdsBodyRequestDto request
    ) {
        recruitmentAdminService.deletePartyRecruitmentBatch(partyId, user.getId(), request);
        return ResponseEntity.ok(null);
    }
}
