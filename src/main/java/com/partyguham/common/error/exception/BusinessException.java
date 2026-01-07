package com.partyguham.common.error.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.Getter;

/**
 * 공통 비즈니스 예외 베이스 클래스
 * - 모든 비즈니스 예외의 공통 부모
 * - code와 message만 관리
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}

