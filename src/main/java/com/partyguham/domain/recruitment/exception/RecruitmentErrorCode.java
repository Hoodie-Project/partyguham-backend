package com.partyguham.domain.recruitment.exception;

import com.partyguham.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitmentErrorCode implements ErrorCode {

    PR_NOT_FOUND(404, "PR_001", "모집공고를 찾을 수 없습니다."),
    PR_NOT_BELONG_TO_PARTY(400, "PR_002", "해당 파티에 소속된 공고가 아닙니다."),

    PR_COMPLETED_TRUE(409, "PR_101", "이미 완료된 모집공고 입니다."),
    PR_RECRUITMENT_FULL(400, "PR_102", "모집 인원이 이미 가득 찼습니다."),
    PR_INVALID_MAX_PARTICIPANTS(400, "PR_103", "최대 모집 인원은 현재 확정된 참여자 수보다 적게 설정할 수 없습니다.")


    ;
    private final int status;
    private final String code;
    private final String message;
}

