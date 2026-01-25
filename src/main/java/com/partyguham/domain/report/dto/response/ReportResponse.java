package com.partyguham.domain.report.dto.response;

import com.partyguham.domain.report.entity.ReportTargetType;
import lombok.Builder;

@Builder
public record ReportResponse(
        Long id,
        ReportTargetType targetType,
        Long targetId,
        String content,
        Long reporterId,
        String createdAt
) {}