package com.partyguham.notification.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    // 알림
    NOTIFICATION_NOT_FOUND(404, "N_001", "파티를 찾을 수 없습니다."),

    // 타입
    NOTIFICATION_TYPE_NOT_FOUND(403, "NT_001", "파티 타입이 없습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

