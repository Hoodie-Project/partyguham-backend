package com.partyguham.catalog.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CatalogErrorCode implements ErrorCode {

    LOCATION_NOT_FOUND(404, "CA_100", "지역 데이터를 찾을 수 없습니다."),


    POSITION_NOT_FOUND(404, "CA_200", "포지션 데이터를 찾을 수 없습니다."),

    PERSONALITY_NOT_FOUND(404, "CA_300", "포지션을 찾을 수 없습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

