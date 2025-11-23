package com.partyguham.user.profile.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    M, F, U; // MALE("M"), FEMALE("F"), UNKNOWN("U")

    /** JSON → Enum (역직렬화) */
    @JsonCreator
    public static CareerType from(String value) {
        return CareerType.valueOf(value.toUpperCase());
    }

    /** Enum → JSON (직렬화) */
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}