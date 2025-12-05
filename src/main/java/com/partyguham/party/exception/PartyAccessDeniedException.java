package com.partyguham.party.exception;

import com.partyguham.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 파티 접근 권한이 없을 때 발생하는 예외
 * HTTP 403 (FORBIDDEN)
 */
public class PartyAccessDeniedException extends BusinessException {

    public PartyAccessDeniedException(String message) {
        super(
                message,
                "PARTY_ACCESS_DENIED",
                HttpStatus.FORBIDDEN
        );
    }

    public PartyAccessDeniedException(Long partyId, Long userId, String reason) {
        super(
                "User " + userId + " cannot access party " + partyId + " : " + reason,
                "PARTY_ACCESS_DENIED",
                HttpStatus.FORBIDDEN
        );
    }
}