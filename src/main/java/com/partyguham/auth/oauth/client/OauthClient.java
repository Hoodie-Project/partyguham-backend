package com.partyguham.auth.oauth.client;

import com.partyguham.auth.oauth.dto.OauthUser;

/**
 * OAuth 프로바이더별 클라이언트 공통 계약.
 */
public interface OauthClient {

    /** 웹: 로그인/연동 시작 URL 생성 (state 인증, flow에 따라 redirectUri 달라짐) */
    String buildAuthorizeUrl(String state, OAuthFlow flow);

    /** 웹: code → access_token → user (flow는 어떤 redirectUri 썼는지 맞추기 위해 필요) */
    OauthUser fetchUserByCode(String code, OAuthFlow flow);

    /**  provider access_token 으로 바로 user 조회 */
    OauthUser fetchUserByAccessToken(String accessToken);

    default OauthUser fetchUserByIdToken(String idToken) {
        throw new UnsupportedOperationException("id_token login not supported");
    }
}