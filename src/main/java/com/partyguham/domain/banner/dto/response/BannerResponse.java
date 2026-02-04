package com.partyguham.domain.banner.dto.response;

import com.partyguham.domain.banner.entity.Banner;
import com.partyguham.domain.banner.entity.BannerPlatform;
import lombok.Builder;

@Builder
public record BannerResponse(
        Long id,
        String status,
        String createdAt,
        String updatedAt,
        BannerPlatform platform,   // 문자열로 내려줌
        String title,
        String image,   // S3 URL
        String link
) {
    public static BannerResponse from(Banner b) {
        return BannerResponse.builder()
                .id(b.getId())
                .status(b.getStatus().toJson())
                .createdAt(b.getCreatedAt().toString())
                .updatedAt(b.getUpdatedAt().toString())
                .platform(b.getPlatform())
                .title(b.getTitle())
                .image(b.getImage())
                .link(b.getLink())
                .build();
    }
}