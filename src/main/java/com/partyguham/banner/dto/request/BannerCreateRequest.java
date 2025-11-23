package com.partyguham.banner.dto.request;

import com.partyguham.banner.entity.BannerPlatform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerCreateRequest {
    private BannerPlatform platform;
    private String title;
    private String image;
    private String link;
}