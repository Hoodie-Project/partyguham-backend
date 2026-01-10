package com.partyguham.party.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyUserErrorCode implements ErrorCode {

    PARTY_USER_NOT_FOUND(404, "PU_001", "파티 유저를 찾을 수 없습니다."),


    INVALID_LEAVE_REQUEST_BY_LEADER(400, "PU_100", "파티장은 파티를 떠날 수 없습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

