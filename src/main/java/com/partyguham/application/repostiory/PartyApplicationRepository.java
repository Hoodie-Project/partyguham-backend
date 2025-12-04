package com.partyguham.application.repostiory;

import com.partyguham.application.entity.PartyApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyApplicationRepository extends JpaRepository<PartyApplication, Long> {

    // 한 유저가 같은 모집에 두 번 지원 못하게
    boolean existsByUser_IdAndPartyRecruitment_Id(Long partyUserId,
                                                       Long partyRecruitmentId);

    // 특정 파티 + 모집공고 + 유저 기준으로 내 지원 1건 찾기
    Optional<PartyApplication> findByPartyRecruitment_IdAndPartyRecruitment_Party_IdAndUser_Id(
            Long partyRecruitmentId,
            Long partyId,
            Long userId
    );
}
