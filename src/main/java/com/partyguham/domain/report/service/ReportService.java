package com.partyguham.domain.report.service;

import com.partyguham.domain.report.dto.request.ReportCreateRequest;
import com.partyguham.domain.report.dto.response.ReportResponse;
import com.partyguham.domain.report.entity.Report;
import com.partyguham.domain.report.entity.ReportTargetType;
import com.partyguham.domain.report.repository.ReportRepository;
import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.account.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    /** 신고 생성 */
    @Transactional
    public ReportResponse createReport(Long reporterId, ReportCreateRequest req) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        Report report = Report.builder()
                .targetType(req.type())
                .targetId(req.typeId())
                .content(req.content())
                .reporter(reporter)
                .build();

        Report saved = reportRepository.save(report);
        return toResponse(saved);
    }

    /** 신고 단건 조회 (관리자용) */
    @Transactional(readOnly = true)
    public ReportResponse getReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("report not found"));
        return toResponse(report);
    }

    /** 특정 대상에 대한 신고 목록 조회 (ex: 어떤 파티에 대한 모든 신고) */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsForTarget(ReportTargetType type, Long targetId) {
        return reportRepository.findByTargetTypeAndTargetId(type, targetId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReportResponse toResponse(Report r) {
        return ReportResponse.builder()
                .id(r.getId())
                .targetType(r.getTargetType())
                .targetId(r.getTargetId())
                .content(r.getContent())
                .reporterId(r.getReporter().getId())
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null)
                .build();
    }
}