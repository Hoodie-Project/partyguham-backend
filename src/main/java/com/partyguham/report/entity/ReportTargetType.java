package com.partyguham.report.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportTargetType {
    USER,
    PARTY,
    PARTY_RECRUITMENT;

    @JsonCreator
    public static ReportTargetType from(String value) {
        return value == null ? null : ReportTargetType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}