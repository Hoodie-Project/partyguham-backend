package com.partyguham.party.exception;

import com.partyguham.common.exception.BusinessException;

/**
 * Party 도메인 공통 비즈니스 예외 베이스 클래스
 * - 모든 Party 관련 예외의 공통 부모
 * - BusinessException을 상속받아 code와 message만 관리
 */
public abstract class PartyBusinessException extends BusinessException {
    
    protected PartyBusinessException(String message) {
        super(message);
    }

    protected PartyBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

