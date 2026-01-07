package com.partyguham.common.error.exception;

import com.partyguham.common.error.CommonErrorCode;
import com.partyguham.common.error.ErrorCode;

public class ConflictException extends BusinessException {

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException() {
        super(CommonErrorCode.CONFLICT);
    }
}

