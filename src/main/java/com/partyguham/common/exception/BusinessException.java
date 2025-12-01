package com.partyguham.common.exception;

/**
 * 공통 비즈니스 예외 베이스 클래스
 * - 모든 비즈니스 예외의 공통 부모
 * - code와 message만 관리
 */
public abstract class BusinessException extends RuntimeException {
    
    protected BusinessException(String message) {
        super(message);
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 각 예외가 자신의 에러 코드를 반환
     * @return 에러 코드 (예: "PARTY_NOT_FOUND")
     */
    public abstract String getCode();

    /**
     * 각 예외가 자신의 HTTP 상태 코드를 반환
     * @return HTTP 상태 코드
     */
    public abstract int getHttpStatus();
}

