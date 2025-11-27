package com.partyguham.user.profile.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    M, F, U; // MALE("M"), FEMALE("F"), UNKNOWN("U")

    /** JSON → Enum (역직렬화) */
    @JsonCreator
    public static Gender from(String value) {
        if (value == null) return null;
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid gender: " + value);
        }
    }

    /** Enum → JSON (직렬화) */
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}