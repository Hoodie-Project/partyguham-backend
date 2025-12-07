package com.partyguham.user.my.service;

import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.entity.PartyApplicationStatus;
import com.partyguham.user.my.dto.request.GetMyPartyApplicationsRequestDto;
import com.partyguham.user.my.dto.response.GetMyPartyApplicationsResponseDto;
import com.partyguham.user.my.repository.MyPartyApplicationQueryRepository;
import com.querydsl.core.types.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPartyApplicationService {

    private final MyPartyApplicationQueryRepository myPartyApplicationQueryRepository;

    @Transactional(readOnly = true)
    public GetMyPartyApplicationsResponseDto getMyPartyApplications(
            Long userId,
            GetMyPartyApplicationsRequestDto req
    ) {
        int page = (req.getPage() != null && req.getPage() > 0) ? req.getPage() - 1 : 0;
        int size = (req.getSize() != null && req.getSize() > 0) ? req.getSize() : 20;

        // 2) 정렬 방향 (null이면 기본 DESC)
        Sort.Direction dir = req.getOrder();

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "createdAt"));

        PartyApplicationStatus statusFilter = req.getPartyApplicationStatus(); // enum 그대로 사용

        Page<PartyApplication> result = myPartyApplicationQueryRepository.searchMyApplications(
                userId,
                statusFilter,
                pageable,
                dir.isAscending() ? Order.ASC : Order.DESC
        );

        return GetMyPartyApplicationsResponseDto.from(result);
    }
}