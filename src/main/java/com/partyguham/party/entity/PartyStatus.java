package com.partyguham.party.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PartyStatus {
    ACTIVE,
    ARCHIVED;

    @JsonCreator
    public static PartyStatus from(String value){
        return PartyStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase();
    }

}
