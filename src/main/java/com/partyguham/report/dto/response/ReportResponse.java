package com.partyguham.report.dto.response;

import com.partyguham.report.entity.ReportTargetType;
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