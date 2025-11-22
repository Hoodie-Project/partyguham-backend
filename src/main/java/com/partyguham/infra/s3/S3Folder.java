package com.partyguham.infra.s3;

public enum S3Folder {
    BANNER("images/banners"),
    USER_PROFILE("images/profiles"),
    PARTY("images/party");

    private final String path;

    S3Folder(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
