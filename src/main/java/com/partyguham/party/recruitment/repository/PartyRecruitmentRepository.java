package com.partyguham.party.recruitment.repository;

import com.partyguham.party.recruitment.entity.PartyRecruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRecruitmentRepository extends JpaRepository<PartyRecruitment, Long>, PartyRecruitmentCustomRepository {
    Page<PartyRecruitment> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}

