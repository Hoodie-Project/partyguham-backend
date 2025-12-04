package com.partyguham.party.exception;

import com.partyguham.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 파티 유저를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 (NOT_FOUND)
 */
public class PartyUserNotFoundException extends BusinessException {

    public PartyUserNotFoundException(Long partyId, Long userId) {
        super(
                "파티 유저를 찾을 수 없습니다. partyId=" + partyId + ", userId=" + userId,
                "PARTY_USER_NOT_EXIST",
                HttpStatus.NOT_FOUND
        );
    }
}