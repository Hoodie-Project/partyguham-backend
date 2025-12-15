package com.partyguham.recruitment.repository;

import com.partyguham.party.dto.party.request.GetPartyRecruitmentsRequestDto;
import com.partyguham.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequestDto;
import com.partyguham.recruitment.dto.request.PartyRecruitmentsRequestDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyRecruitmentCustomRepository {
    // 특정 파티의 모집공고 조회 (페이징 없음)
    List<PartyRecruitment> searchRecruitmentsByPartyId(Long partyId, PartyRecruitmentsRequestDto request);

    // 전체 모집공고 조회 (페이징 있음)
    Page<PartyRecruitment> searchRecruitments(GetPartyRecruitmentsRequestDto request, Pageable pageable);

    Page<PartyRecruitment> searchRecruitmentsPersonalized(GetPartyRecruitmentsPersonalizedRequestDto request, 
                                                          Long positionId, 
                                                          Pageable pageable);
}

