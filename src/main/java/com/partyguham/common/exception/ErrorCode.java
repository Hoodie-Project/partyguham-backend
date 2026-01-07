package com.partyguham.common.exception;

public interface ErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
