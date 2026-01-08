package com.partyguham.common.error.exception;

import com.partyguham.common.error.CommonErrorCode;
import com.partyguham.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException() {
        super(CommonErrorCode.UNAUTHORIZED);
    }
}