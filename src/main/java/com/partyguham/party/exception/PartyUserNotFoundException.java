package com.partyguham.party.exception;

import org.springframework.http.HttpStatus;

/**
 * 파티 유저를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PartyUserNotFoundException extends PartyBusinessException {
    
    public PartyUserNotFoundException(Long partyId, Long userId) {
        super("파티유저를 찾을 수 없습니다.");
    }

    @Override
    public String getCode() {
        return "PARTY_USER_NOT_EXIST";
    }

    @Override
    public int getHttpStatus() {
        return HttpStatus.NOT_FOUND.value();
    }
}

