package com.partyguham.domain.report.repository;

import com.partyguham.domain.report.entity.Report;
import com.partyguham.domain.report.entity.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 어떤 대상에 대한 모든 신고 조회
    List<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);
}