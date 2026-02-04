package com.partyguham.domain.version.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppVersionUpdateRequest {

    private String latestVersion;
    private String minRequiredVersion;
    private String releaseNotes;
    private boolean forceUpdate;
    private String downloadUrl;
}