package com.partyguham.party.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyErrorCode implements ErrorCode {
    // 파티
    PARTY_NOT_FOUND(404, "P_001", "파티를 찾을 수 없습니다."),
    ACCOUNT_LOCKED(403, "P_002", "파티 권한이 없습니다."),
    PARTY_ALREADY_CLOSED(400, "P_003", "이미 종료된 파티입니다."),
    PARTY_NOT_CLOSED(400, "P_004", "종료된 파티가 아닙니다."),
    PARTY_ALREADY_DELETED(400, "P_005", "이미 삭제된 파티입니다."),

    // 파티 타입
    PARTY_TYPE_NOT_FOUND(403, "PT_001", "파티 타입이 없습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

