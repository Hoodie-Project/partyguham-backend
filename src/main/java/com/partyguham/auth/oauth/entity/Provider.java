package com.partyguham.auth.oauth.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Provider {
    KAKAO,
    GOOGLE,
    APPLE;

    /** JSON → Enum (역직렬화) */
    @JsonCreator
    public static Provider from(String value) {
        return Provider.valueOf(value.toUpperCase());
    }

    /** Enum → JSON (직렬화) */
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
