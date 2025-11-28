package com.partyguham.auth.oauth;

public enum LoginResultType {
    SIGNUP,   // 가입 필요
    LOGIN,    // 정상 로그인
    RECOVER,  // 복구 플로우
    ERROR     // 에러
}
