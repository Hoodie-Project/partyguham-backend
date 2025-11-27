package com.partyguham.party.recruitment.repository;

import com.partyguham.party.recruitment.entity.PartyRecruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartyRecruitmentCustomRepository {
    Page<PartyRecruitment> findByTitleKeyword(String keyword, Pageable pageable);
}

