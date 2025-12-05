package com.partyguham.party.exception;

import com.partyguham.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 파티를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PartyNotFoundException extends BusinessException {

    public PartyNotFoundException(Long partyId) {
        super(
                "파티를 찾을 수 없습니다. id=" + partyId,
                "PARTY_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}