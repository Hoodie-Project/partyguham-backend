package com.partyguham.recruitment.service;

import com.partyguham.recruitment.dto.request.CreatePartyRecruitmentRequestDto;
import com.partyguham.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequestDto;
import com.partyguham.recruitment.dto.request.GetPartyRecruitmentsRequestDto;
import com.partyguham.recruitment.dto.request.PartyRecruitmentSearchRequestDto;
import com.partyguham.recruitment.dto.response.CreatePartyRecruitmentsResponseDto;
import com.partyguham.recruitment.dto.response.GetPartyRecruitmentsResponseDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentResponseDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 파티 모집공고 관련 비즈니스 로직을 담당하는 서비스
 * - 컨트롤러에서 사용할 메서드 시그니처만 정의해두고, 내부 구현은 추후 채웁니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyRecruitmentService {

    /**
     * 전체/조건별 파티 모집글 목록 조회
     */
    public GetPartyRecruitmentsResponseDto getPartyRecruitments(Long partyId,
                                                                GetPartyRecruitmentsRequestDto request) {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 개인화된 파티 모집글 목록 조회
     */
    public GetPartyRecruitmentsResponseDto getPersonalizedRecruitments(
            Long partyId,
            GetPartyRecruitmentsPersonalizedRequestDto request
    ) {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 단일 파티 모집글 조회
     */
    public PartyRecruitmentResponseDto getPartyRecruitment(Long partyRecruitmentId, Long userId) {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 특정 파티의 모집글 목록 조회
     */
    public PartyRecruitmentsResponseDto getPartyRecruitmentList(Long partyId,
                                                                PartyRecruitmentSearchRequestDto request) {
        // TODO: 구현 예정
        return null;
    }

    /**
     * 파티 모집글 생성
     */
    @Transactional
    public CreatePartyRecruitmentsResponseDto createPartyRecruitment(Long partyId,
                                                                     Long userId,
                                                                     CreatePartyRecruitmentRequestDto request) {
        // TODO: 구현 예정
        return null;
    }
}


