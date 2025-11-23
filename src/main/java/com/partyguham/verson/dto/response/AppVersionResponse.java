package com.partyguham.verson.dto.response;


import com.partyguham.verson.entity.AppPlatform;
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