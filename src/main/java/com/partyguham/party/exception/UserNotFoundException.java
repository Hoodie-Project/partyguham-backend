package com.partyguham.party.exception;

import com.partyguham.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/**
 * 유저를 찾을 수 없을 때 발생하는 예외 (Party 도메인용)
 * HTTP 404 (NOT_FOUND)
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long userId) {
        super(
                "User가 존재하지 않습니다: " + userId,
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }
}
