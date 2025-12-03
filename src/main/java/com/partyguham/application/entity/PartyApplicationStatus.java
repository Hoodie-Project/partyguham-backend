package com.partyguham.application.entity;

public enum PartyApplicationStatus {
    PENDING,      // 검토중
    PROCESSING,   // 응답대기
    APPROVED,     // 수락
    REJECTED;     // 거절
}