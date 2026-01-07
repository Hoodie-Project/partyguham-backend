package com.partyguham.common.error.exception;

import com.partyguham.common.error.CommonErrorCode;
import com.partyguham.common.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException() {
        super(CommonErrorCode.NOT_FOUND);
    }
}