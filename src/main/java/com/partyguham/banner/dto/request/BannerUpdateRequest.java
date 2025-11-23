package com.partyguham.banner.dto.request;

import com.partyguham.banner.entity.BannerPlatform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerUpdateRequest {
    private BannerPlatform platform; // 선택적으로 수정하고 싶으면 nullable 허용
    private String title;
    private String image;
    private String link;
}
