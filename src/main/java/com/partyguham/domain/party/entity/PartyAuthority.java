package com.partyguham.domain.party.entity;

import lombok.Getter;

@Getter
public enum  PartyAuthority {
    MASTER(3),
    DEPUTY(2),
    MEMBER(1);

    private final int level;
    PartyAuthority(int level) { this.level = level; }
}

