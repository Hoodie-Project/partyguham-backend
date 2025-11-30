package com.partyguham.party.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.service.PartyAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ===========================
 *  파티 관리(Party Admin) API
 * ===========================
 *
 * - 파티장(MASTER) 또는 부파티장(DEPUTY)만 접근 가능한 영역
 * - 파티 자체 정보(이름, 설명, 썸네일 등) 관리
 * - 파티 상태
 * - 파티 이미지 삭제
 * - 파티장 권한 위임
 * - 파티 삭제
 */
@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/parties/{partyId}/admin")
public class PartyAdminController {

    private final PartyAdminService partyAdminService;
    private final S3FileService s3FileService;

    /**
     * 파티 정보 수정 (파티 설명/소개/조건 등)
     */
    @PatchMapping("/info")
    public ResponseEntity<UpdatePartyResponseDto> updateParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestPart MultipartFile image,
            @ModelAttribute UpdatePartyRequestDto request
    ) {
        String imageKey = null;
        if (image != null && !image.isEmpty()) {
            imageKey = s3FileService.upload(image, S3Folder.PARTY);
        }

        return ResponseEntity.ok(
                partyAdminService.updateParty(partyId, user.getId(), request, imageKey)
        );
    }

    /**
     * 파티 상태 변경 (진행중 ↔ 종료)
     */
    @PatchMapping("/status")
    public ResponseEntity<UpdatePartyStatusResponseDto> updatePartyStatus(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody UpdatePartyStatusRequestDto request
    ) {
        return ResponseEntity.ok(
                partyAdminService.updatePartyStatus(partyId, user.getId(), request)
        );
    }

    /**
     * 파티 대표 이미지 삭제
     */
    @DeleteMapping("/image")
    public ResponseEntity<Void> deletePartyImage(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyAdminService.deletePartyImage(partyId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 파티 삭제
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        partyAdminService.deleteParty(partyId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 파티장 권한 위임 (MASTER → MEMBER/DEPUTY)
     */
    @PatchMapping("/delegation")
    public ResponseEntity<PartyDelegationResponseDto> delegateParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody PartyDelegationRequestDto request
    ) {
        return ResponseEntity.ok(
                partyAdminService.delegateParty(partyId, user.getId(), request)
        );
    }
}