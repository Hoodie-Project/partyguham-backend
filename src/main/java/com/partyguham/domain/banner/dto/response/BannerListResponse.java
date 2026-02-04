package com.partyguham.domain.banner.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record BannerListResponse(
        long total,
        List<BannerResponse> banner
) {}