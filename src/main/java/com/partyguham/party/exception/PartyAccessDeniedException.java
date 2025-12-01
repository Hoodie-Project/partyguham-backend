package com.partyguham.party.exception;

import org.springframework.http.HttpStatus;

/**
 * 파티 접근 권한이 없을 때 발생하는 예외
 * HTTP 403 (FORBIDDEN)
 */
public class PartyAccessDeniedException extends PartyBusinessException {

    public PartyAccessDeniedException(String message) {
        super(message);
    }

    public PartyAccessDeniedException(Long partyId, Long userId, String reason) {
        super("User " + userId + " cannot access party " + partyId + " : " + reason);
    }

    @Override
    public String getCode() {
        return "PARTY_ACCESS_DENIED";
    }

    @Override
    public int getHttpStatus() {
        return HttpStatus.FORBIDDEN.value();
    }
}