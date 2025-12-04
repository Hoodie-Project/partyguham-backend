package com.partyguham.party.exception;

import com.partyguham.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 포지션을 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PositionNotFoundException extends BusinessException {

    public PositionNotFoundException(Long positionId) {
        super(
                "Position이 존재하지 않습니다: " + positionId,
                "POSITION_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}