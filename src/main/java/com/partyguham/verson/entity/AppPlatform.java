package com.partyguham.verson.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppPlatform {
    ANDROID, IOS;

    @JsonCreator
    public static AppPlatform from(String value) {
        return AppPlatform.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}