package com.partyguham.party.exception;

import org.springframework.http.HttpStatus;

/**
 * 유저를 찾을 수 없을 때 발생하는 예외 (Party 도메인용)
 * HTTP 404 (NOT_FOUND)
 */
public class UserNotFoundException extends PartyBusinessException {
    
    public UserNotFoundException(Long userId) {
        super("User가 존재하지 않습니다: " + userId);
    }

    @Override
    public String getCode() {
        return "USER_NOT_FOUND";
    }

    @Override
    public int getHttpStatus() {
        return HttpStatus.NOT_FOUND.value();
    }
}

