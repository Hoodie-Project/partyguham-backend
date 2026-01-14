package com.partyguham.catalog.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CatalogErrorCode implements ErrorCode {

    POSITION_NOT_FOUND(404, "CA_001", "포지션을 찾을 수 없습니다."),
    INVALID_MAIN_POSITION(400, "CA_002", "존재하지 않은 직군입니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

