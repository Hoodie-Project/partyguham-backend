package com.partyguham.party.exception;

import com.partyguham.common.exception.NotFoundException;

/**
 * 파티를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PartyNotFoundException extends NotFoundException {

    public PartyNotFoundException() {
        super(
                "요청한 파티가 존재하지 않습니다.",
                "PARTY_NOT_FOUND"
        );
    }
}