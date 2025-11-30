package com.partyguham.party.service;

import com.partyguham.common.entity.Status;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.party.dto.partyAdmin.mapper.PartyUserAdminMapper;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyType;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.repository.PartyTypeRepository;
import com.partyguham.party.repository.PartyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyAdminService {

    private final PartyAccessService partyAccessService;
    private final PartyUserAdminMapper partyUserAdminMapper;
    private final PartyUserRepository partyUserRepository;
    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final S3FileService s3FileService; // 이전 이미지 삭제용(옵션)



    /**
     * 관리자용 파티원 목록 조회
     * - 파티장/부파티장 권한 필요
     * - 필터(authority, nickname, main) + 페이징 적용
     * - totalPartyUserCount(전체 인원) + total(필터 후 인원) 반환
     */
    @Transactional(readOnly = true)
    public GetAdminPartyUsersResponseDto getPartyUsers(
            Long partyId,
            GetAdminPartyUsersRequestDto request,
            Long userId
    ) {
        // 1) 권한 체크
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 2) 페이징 기본값 (0-based page)
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 3) 전체 파티원 수 (삭제 제외, 필터 X)
        long totalCount = partyUserRepository.countAllByPartyIdNotDeleted(partyId);

        // 4) 필터 + 페이징 적용된 결과 조회
        Page<PartyUser> pageResult =
                partyUserRepository.searchAdminPartyUsers(partyId, request, pageable);

        long total = pageResult.getTotalElements();

        // 5) 엔티티 → DTO 리스트 변환
        List<GetAdminPartyUsersResponseDto.AdminPartyUserDto> items =
                pageResult.getContent().stream()
                        .map(partyUserAdminMapper::toAdminDto)
                        .toList();

        // 6) 최종 응답 조립
        return GetAdminPartyUsersResponseDto.builder()
                .totalCount(totalCount) // 파티 전체 인원 수
                .total(total)           // 필터 + 페이징 후 인원 수
                .partyUsers(items)
                .build();
    }

    @Transactional
    public UpdatePartyResponseDto updateParty(
            Long partyId,
            Long userId,
            UpdatePartyRequestDto request,
            String newImageKey // 새 이미지가 없으면 null
    ) {
        // 1) 권한 체크
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티입니다. id=" + partyId));

        // 3) 파티 타입 변경 (nullable이면 변경 안 함)
        if (request.getPartyTypeId() != null) {
            PartyType partyType = partyTypeRepository.findById(request.getPartyTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티 타입입니다. id=" + request.getPartyTypeId()));
            party.setPartyType(partyType);
        }

        // 4) 제목/내용 부분 수정 (null이면 변경 안 함)
        if (request.getTitle() != null) {
            party.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            party.setContent(request.getContent());
        }

        // 5) 이미지 변경 (있을 때만)
        if (newImageKey != null) {
            String oldKey = party.getImage();
            party.setImage(newImageKey);

            // 이전 이미지 삭제하고 싶으면
            if (oldKey != null && !oldKey.equals(newImageKey)) {
                s3FileService.delete(oldKey);
            }
        }

        return UpdatePartyResponseDto.from(party);
    }

    @Transactional
    public UpdatePartyStatusResponseDto updatePartyStatus(
            Long partyId,
            Long userId,
            UpdatePartyStatusRequestDto request
    ) {
        // 1) 권한 체크 (파티장/부파티장)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티입니다. id=" + partyId));

        // 3) 문자열 → 공통 Status enum 변환 (DTO에서 active/archived)
        Status newStatus = Status.from(request.getStatus()); // ACTIVE / ARCHIVED

        // 4) 상태 변경
        party.setStatus(newStatus);

        // 5) 응답 DTO로 변환
        return UpdatePartyStatusResponseDto.from(party);
    }

    @Transactional
    public void deletePartyImage(Long partyId, Long userId) {
        // 1) 권한 체크 (파티장/부파티장만)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티입니다. id=" + partyId));

        // 3) 기존 이미지 키 가져오기
        String oldImageKey = party.getImage();
        if (oldImageKey == null || oldImageKey.isBlank()) {
            // 이미지가 원래 없으면 그냥 리턴
            return;
        }

        // 4) DB에서 먼저 끊어주기 (null 세팅)
        party.setImage(null);

        // 5) S3에서 실제 파일 삭제 - 삭제 실패시 어떻게 할지는 정책에 따라 (지금은 예외 그대로 던지도록)
        s3FileService.delete(oldImageKey);
    }


    public void deleteParty(Long partyId, Long userId) {
        partyAccessService.checkMasterOrThrow(partyId, userId);
    }


    public PartyDelegationResponseDto delegateParty(Long partyId, Long userId, PartyDelegationRequestDto request) {
        partyAccessService.checkMasterOrThrow(partyId, userId);

        return null;
    }


    public UpdatePartyUserResponseDto updatePartyUser(Long partyId, Long partyUserId, Long userId, UpdatePartyUserRequestDto request) {
        partyAccessService.checkMasterOrThrow(partyId, userId);

        return null;
    }


    public void deletePartyUser(Long partyId, Long partyUserId, Long userId) {
        partyAccessService.checkMasterOrThrow(partyId, userId);
    }


    public void deletePartyUserBatch(Long partyId, Long userId, DeletePartyUsersBodyRequestDto request) {
        partyAccessService.checkMasterOrThrow(partyId, userId);
    }
}
