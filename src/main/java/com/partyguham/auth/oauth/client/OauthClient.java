package com.partyguham.auth.oauth.client;

import com.partyguham.auth.oauth.dto.OauthUser;

/**
 * OAuth 프로바이더별 클라이언트 공통 계약.
 * - buildAuthorizeUrl: 웹 로그인 시작 URL 생성
 * - fetchUserByCode:   웹(Authorization Code) 콜백에서 code로 사용자 정보 조회
 * - fetchUserByAccessToken: 앱에서 provider access_token으로 직접 사용자 정보 조회
 */
public interface OauthClient {

    /** 웹 시작 URL 생성 (state 포함 권장) */
    String buildAuthorizeUrl(String state);

    /** 웹: code + (옵션)state → access_token 교환 → /userinfo 호출 → OauthUser */
    OauthUser fetchUserByCode(String code, String state);

    /** 앱: provider access_token → /userinfo 호출 → OauthUser */
    OauthUser fetchUserByAccessToken(String accessToken);
}