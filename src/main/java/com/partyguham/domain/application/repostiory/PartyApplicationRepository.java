package com.partyguham.domain.application.repostiory;

import com.partyguham.domain.application.entity.PartyApplication;
import com.partyguham.domain.application.entity.PartyApplicationStatus;
import com.partyguham.global.entity.Status;
import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PartyApplicationRepository extends JpaRepository<PartyApplication, Long> {

    Optional<PartyApplication> findByIdAndStatusNot(Long id, Status status);

    @Query("select pa from PartyApplication pa " +
            "join fetch pa.partyRecruitment pr " +
            "join fetch pr.party p " +
            "where pa.id = :id and pa.status != :status")
    Optional<PartyApplication> findWithDetailsById(@Param("id") Long id, @Param("status") Status status);

    @Query("SELECT pa FROM PartyApplication pa " +
            "JOIN FETCH pa.partyRecruitment pr " +
            "WHERE pa.id = :id")
    Optional<PartyApplication> findByIdFetchRecruitment(@Param("id") Long id);

    @Query("SELECT pa FROM PartyApplication pa " +
            "join fetch pa.user " +
            "WHERE pa.id = :id")
    Optional<PartyApplication> findByIdFetchUser(@Param("id") Long id);

    // 한 유저가 같은 모집에 두 번 지원 못하게
    boolean existsByUser_IdAndPartyRecruitment_Id(Long partyUserId,
                                                       Long partyRecruitmentId);

    // 특정 파티 + 모집공고 + 유저 기준으로 내 지원 1건 찾기
    Optional<PartyApplication> findByPartyRecruitment_IdAndPartyRecruitment_Party_IdAndUser_Id(
            Long partyRecruitmentId,
            Long partyId,
            Long userId
    );

    List<PartyApplication> findByPartyRecruitmentAndApplicationStatusIn(
            PartyRecruitment recruitment,
            Collection<PartyApplicationStatus> statuses
    );

    /**
     * 알림 대상자 정보를 User와 함께 한 번에 조회 (N+1 방지)
     * @param recruitment
     * @param statuses
     * @return
     */
    @Query("select pa from PartyApplication pa " +
            "join fetch pa.user " +
            "where pa.partyRecruitment = :recruitment " +
            "and pa.applicationStatus in :statuses")
    List<PartyApplication> findWithUserByRecruitmentAndStatusIn(
            @Param("recruitment") PartyRecruitment recruitment,
            @Param("statuses") Collection<PartyApplicationStatus> statuses
    );

    /**
     * 벌크 업데이트를 통한 상태 일괄 변경 (성능 최적화)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PartyApplication pa " +
            "set pa.applicationStatus = 'CLOSED', " +
            "pa.updatedAt = now() " +
            "where pa.partyRecruitment.id = :recruitmentId " +
            "and pa.applicationStatus in ('PENDING', 'PROCESSING')")
    void bulkUpdateStatusToClosed(@Param("recruitmentId") Long recruitmentId, @Param("now") LocalDateTime now);
}
