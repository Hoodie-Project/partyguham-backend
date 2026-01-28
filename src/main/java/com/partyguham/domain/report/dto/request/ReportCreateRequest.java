package com.partyguham.domain.report.dto.request;

import com.partyguham.domain.report.entity.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportCreateRequest(
        @NotNull ReportTargetType type,   // "user" / "party" ...
        @NotNull Long typeId,
        @NotBlank String content
) {}
