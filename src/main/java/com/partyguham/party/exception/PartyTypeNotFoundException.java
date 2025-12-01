package com.partyguham.party.exception;

import org.springframework.http.HttpStatus;

/**
 * 파티 타입을 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PartyTypeNotFoundException extends PartyBusinessException {
    
    public PartyTypeNotFoundException(Long partyTypeId) {
        super("Party Type이 존재하지 않습니다: " + partyTypeId);
    }

    @Override
    public String getCode() {
        return "PARTY_TYPE_NOT_FOUND";
    }

    @Override
    public int getHttpStatus() {
        return HttpStatus.NOT_FOUND.value();
    }
}
