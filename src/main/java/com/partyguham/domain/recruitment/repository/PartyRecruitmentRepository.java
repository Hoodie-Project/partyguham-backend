package com.partyguham.domain.recruitment.repository;

import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRecruitmentRepository extends JpaRepository<PartyRecruitment, Long>, PartyRecruitmentCustomRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PartyRecruitment p where p.id = :id")
    Optional<PartyRecruitment> findByIdWithLock(@Param("id") Long id);

    Optional<PartyRecruitment> findByIdAndPartyId(
            Long id,
            Long partyId
    );

    Page<PartyRecruitment> findByPartyIdIn(List<Long> partyIds, Pageable pageable);

    List<PartyRecruitment> findAllByIdInAndPartyId(
            List<Long> ids,
            Long partyId
    );
}

