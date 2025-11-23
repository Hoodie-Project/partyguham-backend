package com.partyguham.report.dto.request;

import com.partyguham.report.entity.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportCreateRequest(
        @NotNull ReportTargetType targetType,   // "user" / "party" ...
        @NotNull Long targetId,
        @NotBlank String content
) {}
