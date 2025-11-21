package com.partyguham.banner.dto.response;

import com.partyguham.banner.entity.BannerPlatform;
import lombok.Builder;

@Builder
public record BannerResponse(
        Long id,
        String status,
        String createdAt,
        String updatedAt,
        BannerPlatform platform,
        String title,
        String image,
        String link
) {}