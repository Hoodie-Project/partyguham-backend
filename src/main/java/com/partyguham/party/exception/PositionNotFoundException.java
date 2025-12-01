package com.partyguham.party.exception;

import org.springframework.http.HttpStatus;

/**
 * 포지션을 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PositionNotFoundException extends PartyBusinessException {
    
    public PositionNotFoundException(Long positionId) {
        super("Position이 존재하지 않습니다: " + positionId);
    }

    @Override
    public String getCode() {
        return "POSITION_NOT_FOUND";
    }

    @Override
    public int getHttpStatus() {
        return HttpStatus.NOT_FOUND.value();
    }
}

