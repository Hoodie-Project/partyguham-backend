package com.partyguham.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 공통 404 Not Found 예외
 * - 특정 리소스를 찾을 수 없을 때 사용
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(String message, String code) {
        super(message, code, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(
                message,
                "NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}
