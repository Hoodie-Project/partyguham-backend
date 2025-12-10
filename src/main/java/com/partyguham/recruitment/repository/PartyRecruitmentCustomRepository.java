package com.partyguham.recruitment.repository;

import com.partyguham.party.dto.party.request.GetPartyRecruitmentsRequestDto;
import com.partyguham.recruitment.dto.request.GetPartyRecruitmentsPersonalizedRequestDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartyRecruitmentCustomRepository {
    Page<PartyRecruitment> searchRecruitments(GetPartyRecruitmentsRequestDto request, Pageable pageable);

    Page<PartyRecruitment> searchRecruitmentsPersonalized(GetPartyRecruitmentsPersonalizedRequestDto request, 
                                                          Long positionId, 
                                                          Pageable pageable);
}

