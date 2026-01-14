package com.partyguham.user.profile.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserProfileErrorCode implements ErrorCode {

    // Core/Common 관련
    USER_NOT_FOUND(404, "U_001", "사용자를 찾을 수 없습니다."),
    ACCOUNT_LOCKED(403, "U_002", "계정이 잠겨 있습니다."),
    USER_ALREADY_WITHDRAWN(409, "U_003", "이미 탈퇴 처리된 사용자입니다."),
    USER_STATUS_INACTIVE(409, "U_004", "활성화되지 않은 계정 상태입니다."),
    USER_PERMANENTLY_DELETED(409, "U_005", "복구 불가능한 계정입니다."),
    USER_RESTORE_PERIOD_EXPIRED(409, "U_006", "계정 복구 가능 기간이 지났습니다."),
    FCM_TOKEN_NOT_FOUND(404, "U_100", "FCM TOKEN 을 찾을 수 없습니다."),

    DUPLICATE_EMAIL(409, "U_101", "이미 존재하는 이메일 입니다."),
    DUPLICATE_NICKNAME(409, "U_102", "이미 존재하는 닉네임 입니다."),

    // Profile 관련
    PROFILE_NOT_FOUND(404, "U_101", "프로필 정보를 찾을 수 없습니다."),

    ;
    private final int status;
    private final String code;
    private final String message;
}