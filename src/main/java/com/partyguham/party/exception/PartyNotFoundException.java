package com.partyguham.party.exception;

import org.springframework.http.HttpStatus;

/**
 * 파티를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PartyNotFoundException extends PartyBusinessException {
    
    public PartyNotFoundException(Long partyId) {
        super("파티를 찾을 수 없습니다.");
    }

    @Override
    public String getCode() {
        return "PARTY_NOT_FOUND";
    }

    @Override
    public int getHttpStatus() {
        return HttpStatus.NOT_FOUND.value();
    }
}

