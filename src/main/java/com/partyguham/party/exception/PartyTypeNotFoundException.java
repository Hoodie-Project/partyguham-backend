package com.partyguham.party.exception;

import com.partyguham.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 파티 타입을 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PartyTypeNotFoundException extends BusinessException {

    public PartyTypeNotFoundException(Long partyTypeId) {
        super(
                "Party Type이 존재하지 않습니다: " + partyTypeId,
                "PARTY_TYPE_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}