package com.partyguham.common.error.exception;

import com.partyguham.common.error.CommonErrorCode;
import com.partyguham.common.error.ErrorCode;

public class BadRequestException extends BusinessException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException() {
        super(CommonErrorCode.BAD_REQUEST);
    }
}

