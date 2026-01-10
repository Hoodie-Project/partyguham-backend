package com.partyguham.application.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {

    PARTY_APPLICATION_NOT_FOUND(404, "PA_001", "파티 지원을 찾을 수 없습니다."),
    PARTY_APPLICATION_ALREADY_DELETED(404, "PA_002", "파티 지원을 찾을 수 없습니다."),
    PARTY_APPLICATION_MISMATCHED_PARTY(404, "PA_003", "파티 지원이 파티에 속하지 않습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

