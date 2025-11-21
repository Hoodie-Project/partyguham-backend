package com.partyguham.banner.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BannerPlatform {
    WEB, APP;

    // JSON -> Enum (소문자 허용)
    @JsonCreator
    public static BannerPlatform from(String value) {
        return BannerPlatform.valueOf(value.toUpperCase());
    }

    // Enum -> JSON (소문자로 내려줌: "web", "app")
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
