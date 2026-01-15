package com.partyguham.catalog.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CatalogErrorCode implements ErrorCode {
    LOCATION_NOT_FOUND(404, "CA_100", "지역 데이터를 찾을 수 없습니다."),

    POSITION_NOT_FOUND(404, "CA_200", "포지션 데이터를 찾을 수 없습니다."),
  
    INVALID_MAIN_POSITION(400, "CA_300", "존재하지 않은 직군입니다."),

    PERSONALITY_QUESTION_NOT_FOUND(404, "CA_301", "존재하지 않는 성향 질문입니다."),
    PERSONALITY_OPTION_NOT_FOUND(404, "CA_302", "존재하지 않는 성향 옵션입니다."),

    ;

    private final int status;
    private final String code;
    private final String message;
}

