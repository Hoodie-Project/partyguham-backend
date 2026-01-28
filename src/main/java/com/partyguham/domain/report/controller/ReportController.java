package com.partyguham.domain.report.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.report.dto.request.ReportCreateRequest;
import com.partyguham.domain.report.dto.response.ReportResponse;
import com.partyguham.domain.report.entity.ReportTargetType;
import com.partyguham.domain.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * 신고 생성
     * POST /api/v2/reports
     *
     * body 예시:
     * {
     *   "targetType": "party",
     *   "targetId": 123,
     *   "content": "욕설을 했습니다"
     * }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse create(@AuthenticationPrincipal UserPrincipal user,
                                 @RequestBody @Valid ReportCreateRequest req) {
        Long reporterId = user.getId();
        return reportService.createReport(reporterId, req);
    }

    /**
     * 신고 단건 조회 (관리자/운영자용)
     * GET /api/v2/reports/{id}
     */
    @GetMapping("/{id}")
    public ReportResponse get(@PathVariable Long id) {
        return reportService.getReport(id);
    }

    /**
     * 특정 대상에 대한 신고 목록 조회
     * GET /api/v2/reports?targetType=party&targetId=123
     */
    @GetMapping
    public List<ReportResponse> listByTarget(@RequestParam ReportTargetType targetType,
                                             @RequestParam Long targetId) {
        return reportService.getReportsForTarget(targetType, targetId);
    }
}