package com.partyguham.recruitment.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitmentErrorCode implements ErrorCode {

    PR_NOT_FOUND(404, "PR_001", "모집공고를 찾을 수 없습니다."),
    PR_NOT_BELONG_TO_PARTY(400, "PR_002", "해당 파티에 소속된 공고가 아닙니다."),

    PR_COMPLETED_CONFLICT(409, "PR_100", "이미 완료된 모집공고 입니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

