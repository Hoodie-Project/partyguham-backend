package com.partyguham.domain.version.dto.response;


import com.partyguham.domain.version.entity.AppPlatform;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppVersionResponse(
        Long id,
        AppPlatform platform,
        String latestVersion,
        String minRequiredVersion,
        String releaseNotes,
        boolean forceUpdate,
        String downloadUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }