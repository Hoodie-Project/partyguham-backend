package com.partyguham.domain.auth.exception;

import com.partyguham.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // JWT 관련
    EXPIRED_TOKEN(401, "JWT_001", "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "JWT_002", "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(401, "JWT_003", "유효하지 않은 토큰입니다."),

    // OTT 관련
    INVALID_OTT_TYPE(401, "OTT_001", "잘못된 OTT 타입입니다."),
    EXPIRED_OTT_TOKEN(401, "OTT_002", "만료되거나 사용된 OTT 토큰입니다."),

    // 공통
    AUTHENTICATION_FAILED(401, "AUTH_001", "인증에 실패하였습니다."),
    OAUTH_PROVIDER_FAILED(401, "OAUTH_001", "지원하지 않는 Oauth 입니다.");
    ;

    private final int status;
    private final String code;
    private final String message;
}

