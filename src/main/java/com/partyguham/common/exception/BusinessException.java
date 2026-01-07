package com.partyguham.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 공통 비즈니스 예외 베이스 클래스
 * - 모든 비즈니스 예외의 공통 부모
 * - code와 message만 관리
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    protected BusinessException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.httpStatus = status;
    }

    /**  예외 원인 추가 - 외부 API 오류 (S3, SMTP, 외부 결제 등), JPA/DB 오류 래핑, 예외 재변환(wrapping) */
    protected BusinessException(String message, String code, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = status;
    }

}

