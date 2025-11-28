package com.partyguham.auth.oauth;

import java.time.LocalDateTime;

/**
 * OAuth 로그인 처리 결과
 */
public record LoginResult(
        LoginResultType type,
        String signupToken,
        String accessToken,
        String refreshToken,
        String recoverToken,
        UserErrorType errorType,
        String email,
        LocalDateTime deletedAt
) {

    public static LoginResult signup(String token) {
        return new LoginResult(LoginResultType.SIGNUP, token,
                null, null, null, null, null, null);
    }

    public static LoginResult login(String at, String rt) {
        return new LoginResult(LoginResultType.LOGIN, null,
                at, rt, null, null, null, null);
    }

    public static LoginResult recover(String token, String email, LocalDateTime deletedAt) {
        return new LoginResult(LoginResultType.RECOVER, null,
                null, null, token, UserErrorType.USER_DELETED_30D, email, deletedAt);
    }

    public static LoginResult error(UserErrorType type) {
        return new LoginResult(LoginResultType.ERROR, null,
                null, null, null, type, null, null);
    }
}