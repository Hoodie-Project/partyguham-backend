package com.partyguham.auth.oauth.client;

import com.partyguham.auth.oauth.dto.OauthUser;

/**
 * OAuth 프로바이더별 클라이언트 공통 계약.
 * - buildAuthorizeUrl: 웹 로그인 시작 URL 생성
 * - fetchUserByCode:   웹(Authorization Code) 콜백에서 code로 사용자 정보 조회
 * - fetchUserByAccessToken: 앱에서 provider access_token으로 직접 사용자 정보 조회
 */
public interface OauthClient {

    /** 웹: 로그인/연동 시작 URL 생성 (state 인증, flow에 따라 redirectUri 달라짐) */
    String buildAuthorizeUrl(String state, OAuthFlow flow);

    /** 웹: code → access_token → user (flow는 어떤 redirectUri 썼는지 맞추기 위해 필요) */
    OauthUser fetchUserByCode(String code, OAuthFlow flow);

    /** 앱: provider access_token 으로 바로 user 조회 */
    OauthUser fetchUserByAccessToken(String accessToken);
}