package com.partyguham.domain.auth.oauth.exception;

import com.partyguham.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OauthAccountErrorCode implements ErrorCode {

    USER_OAUTH_DATA_NOT_FOUND(404, "OA_001", "OAuth 데이터가 없습니다. (provider/externalId 불일치)"),
    OAUTH_CONFLICT(409, "OA_002", "이미 가입된 OAuth 계정입니다."),
    OAUTH_LINK_CONFLICT(409, "OA_003", "이미 다른 유저에 연결된 OAuth 계정입니다."),


    ;
    private final int status;
    private final String code;
    private final String message;
}