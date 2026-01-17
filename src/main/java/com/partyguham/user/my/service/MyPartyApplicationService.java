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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPartyApplicationService {

    private final MyPartyApplicationQueryRepository myPartyApplicationQueryRepository;

    @Transactional(readOnly = true)
    public GetMyPartyApplicationsResponseDto getMyPartyApplications(
            Long userId,
            GetMyPartyApplicationsRequestDto req
    ) {
        // 1) 페이지 및 사이즈 설정
        int page = (req.getPage() != null && req.getPage() > 0) ? req.getPage() - 1 : 0;
        int size = (req.getSize() != null && req.getSize() > 0) ? req.getSize() : 20;

        // 2) 정렬 방향
        Sort.Direction dir = (req.getOrder() != null) ? req.getOrder() : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "createdAt"));

        // 3) 리스트 필터 적용
        List<PartyApplicationStatus> statusFilters = req.getPartyApplicationStatus();

        Page<PartyApplication> result = myPartyApplicationQueryRepository.searchMyApplications(
                userId,
                statusFilters, // 이제 단일 status가 아닌 리스트를 넘깁니다.
                pageable,
                dir.isAscending() ? Order.ASC : Order.DESC
        );

        return GetMyPartyApplicationsResponseDto.from(result);
    }
}