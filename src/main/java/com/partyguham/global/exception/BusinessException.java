package com.partyguham.global.exception;

import com.partyguham.global.error.ErrorCode;
import lombok.Getter;

/**
 * 공통 비즈니스 예외 베이스 클래스
 * - 모든 비즈니스 예외의 공통 부모
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

