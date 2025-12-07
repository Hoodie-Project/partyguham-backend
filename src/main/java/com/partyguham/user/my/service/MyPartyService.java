package com.partyguham.user.my.service;

import com.partyguham.party.entity.PartyStatus;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.user.my.dto.request.GetMyPartiesRequestDto;
import com.partyguham.user.my.dto.response.GetMyPartiesResponseDto;
import com.partyguham.user.my.repository.MyPartyQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPartyService {

    private final MyPartyQueryRepository myPartyQueryRepository;

    /**
     * 내가 속한 파티 목록 조회
     */
    @Transactional(readOnly = true)
    public GetMyPartiesResponseDto getMyParties(Long userId,
                                                GetMyPartiesRequestDto req) {

        // 1) page/size 기본값 처리 (page는 1-based → 0-based 변환)
        int page = (req.getPage() != null && req.getPage() > 0) ? req.getPage() - 1 : 0;
        int size = (req.getSize() != null && req.getSize() > 0) ? req.getSize() : 20;

        Sort.Direction dir = req.getOrder();

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "createdAt"));

        // 3) 파티 상태 필터 (enum 그대로 사용, null 허용)
        PartyStatus partyStatusFilter = req.getPartyStatus();

        // 4) QueryDSL 레포 호출 (JPA Sort.Direction → QueryDSL Order 변환만 해줌)
        Page<PartyUser> result = myPartyQueryRepository.searchMyParties(
                userId,
                partyStatusFilter,
                pageable,
                dir.isAscending()
                        ? com.querydsl.core.types.Order.ASC
                        : com.querydsl.core.types.Order.DESC
        );

        // 5) 엔티티 → 응답 DTO 변환
        return GetMyPartiesResponseDto.fromEntities(
                result.getContent(),
                result.getTotalElements()
        );
    }
}