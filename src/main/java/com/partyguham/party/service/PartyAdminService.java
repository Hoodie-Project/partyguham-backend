package com.partyguham.party.service;

import com.partyguham.party.dto.partyAdmin.mapper.PartyUserAdminMapper;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.entity.PartyUser;
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

    public UpdatePartyResponseDto updateParty(Long partyId, Long userId, UpdatePartyRequestDto request) {
        partyAccessService.checkMasterOrThrow(partyId, userId);

        return null;
    }


    public UpdatePartyStatusResponseDto updatePartyStatus(Long partyId, Long userId, UpdatePartyStatusRequestDto request) {
        partyAccessService.checkMasterOrThrow(partyId, userId);

        return null;
    }


    public void deletePartyImage(Long partyId, Long userId) {
        partyAccessService.checkMasterOrThrow(partyId, userId);

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
