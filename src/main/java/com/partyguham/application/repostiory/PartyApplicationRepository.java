package com.partyguham.application.repostiory;

import com.partyguham.application.entity.PartyApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyApplicationRepository extends JpaRepository<PartyApplication, Long> {
    boolean existsByPartyUser_IdAndPartyRecruitment_Id(Long partyUserId,
                                                       Long partyRecruitmentId);
}
