package com.partyguham.user.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // Core/Common 관련
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
    ACCOUNT_LOCKED(403, "U002", "계정이 잠겨 있습니다."),
    USER_ALREADY_WITHDRAWN(409, "U003", "이미 탈퇴 처리된 사용자입니다."),
    USER_STATUS_INACTIVE(409, "U004", "활성화되지 않은 계정 상태입니다."),
    USER_PERMANENTLY_DELETED(409, "U005", "복구 불가능한 계정입니다."),
    USER_RESTORE_PERIOD_EXPIRED(409, "U006", "계정 복구 가능 기간이 지났습니다."),


    DUPLICATE_EMAIL(409, "U101", "이미 존재하는 이메일 입니다."),
    DUPLICATE_NICKNAME(409, "U102", "이미 존재하는 이메일 입니다."),

    // Profile 관련
    PROFILE_NOT_FOUND(404, "U101", "프로필 정보를 찾을 수 없습니다."),

    ;
    private final int status;
    private final String code;
    private final String message;
}