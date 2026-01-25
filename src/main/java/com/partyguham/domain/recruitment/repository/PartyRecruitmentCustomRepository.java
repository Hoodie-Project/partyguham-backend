package com.partyguham.domain.recruitment.repository;

import com.partyguham.domain.party.dto.party.request.GetPartyRecruitmentsRequest;
import com.partyguham.domain.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequest;
import com.partyguham.domain.recruitment.dto.request.PartyRecruitmentsRequest;
import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyRecruitmentCustomRepository {
    // [파티모집] 특정 파티의 모집공고 조회 
    List<PartyRecruitment> searchRecruitmentsByPartyId(Long partyId, PartyRecruitmentsRequest request);

    // [라운지] 전체 모집공고 조회 
    Page<PartyRecruitment> searchRecruitments(GetPartyRecruitmentsRequest request, Pageable pageable);

    Page<PartyRecruitment> searchRecruitmentsPersonalized(GetPartyRecruitmentsPersonalizedRequest request,
                                                          Long positionId, 
                                                          Pageable pageable);
}

