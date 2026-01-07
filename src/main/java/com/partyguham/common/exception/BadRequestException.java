package com.partyguham.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 공통 400 Bad Request 예외
 * - 잘못된 요청일 때 사용
 */
public class BadRequestException extends BusinessException {

    public BadRequestException(String message, String code) {
        super(
                message,
                code,
                HttpStatus.BAD_REQUEST
        );
    }

    public BadRequestException(String message) {
        super(
                message,
                "BAD_REQUEST",
                HttpStatus.BAD_REQUEST
        );
    }
}

