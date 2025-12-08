package com.partyguham.recruitment.repository;

import com.partyguham.recruitment.entity.PartyRecruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRecruitmentRepository extends JpaRepository<PartyRecruitment, Long>, PartyRecruitmentCustomRepository {
    Page<PartyRecruitment> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    List<PartyRecruitment> findByPartyId(Long partyId, Sort sort);

    Page<PartyRecruitment> findByPartyIdIn(List<Long> partyIds, Pageable pageable);
}

