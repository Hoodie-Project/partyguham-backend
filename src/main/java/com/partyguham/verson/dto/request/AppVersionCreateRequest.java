package com.partyguham.verson.dto.request;

import com.partyguham.verson.entity.AppPlatform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppVersionCreateRequest {
    private AppPlatform platform;     // ANDROID / IOS
    private String latestVersion;     // "1.2.3"
    private String minRequiredVersion;
    private String releaseNotes;
    private boolean forceUpdate;
    private String downloadUrl;
}