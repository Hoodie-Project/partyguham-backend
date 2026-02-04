package com.partyguham.domain.application.exception;

import com.partyguham.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {

    PARTY_APPLICATION_NOT_FOUND(404, "PA_001", "파티 지원을 찾을 수 없습니다."),
    PARTY_APPLICATION_ALREADY_DELETED(404, "PA_002", "파티 지원이 삭제 되었습니다."),
    PARTY_APPLICATION_MISMATCHED_PARTY(400, "PA_003", "해당 파티의 지원 내역이 아닙니다."),
    ALREADY_PARTY_MEMBER(409, "PA_004", "이미 해당 파티의 멤버입니다."),
    ALREADY_APPLIED(409, "PA_005", "이미 이 모집에 지원하셨습니다."),
    INVALID_APPLICATION_STATUS(400, "PA_006", "변경할 수 없는 지원 상태입니다."),
    NOT_APPLICATION_OWNER(403, "PA_007", "본인의 지원 내역만 조작할 수 있습니다."),
    PARTY_HOST_NOT_FOUND(404, "PA_008", "파티장 정보를 찾을 수 없습니다.");

    ;

    private final int status;
    private final String code;
    private final String message;
}

