package com.partyguham.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 공통 409 Conflict 예외
 * - 리소스 충돌이나 상태 충돌일 때 사용
 */
public class ConflictException extends BusinessException {

    public ConflictException(String message, String code) {

        super(
                message,
                code,
                HttpStatus.CONFLICT
        );
    }

    public ConflictException(String message) {
        super(
                message,
                "CONFLICT",
                HttpStatus.CONFLICT
        );
    }
}

