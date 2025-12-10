package com.partyguham.application.entity;

public enum PartyApplicationStatus {
    PENDING,      // 검토중
    PROCESSING,   // 지원자 응답대기 (파티장 수락)
    APPROVED,     // 지원자 수락 (양쪽 수락)
    REJECTED,     // 파티장 거절
    DECLINED,     // 지원자 거절(응답 거절)
    CLOSED;       // 모집 마감
}