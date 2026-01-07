package com.partyguham.common.error.exception;

import com.partyguham.common.error.CommonErrorCode;
import com.partyguham.common.error.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ForbiddenException() {
        super(CommonErrorCode.FORBIDDEN);
    }
}