package com.partyguham.party.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum  PartyAuthority {
    MASTER, // 파티장
    DEPUTY ,// 부파티장
    MEMBER; // 맴버

    @JsonCreator
    public static PartyAuthority from(String value) {
        return PartyAuthority.valueOf(value.toUpperCase());
    }

    // Enum → JSON (직렬화)
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}

