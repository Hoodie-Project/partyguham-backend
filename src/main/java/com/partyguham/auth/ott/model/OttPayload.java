package com.partyguham.auth.ott.model;

/**
 * OTT에 저장할 페이로드
 * - 필요한 정보만 담는다 (민감정보 X)
 * - Jackson으로 직렬화/역직렬화하여 Redis에 저장
 *
 * 예: OAuth 콜백에서 발급할 때에는 oauthId/email/image 정도만 담음
 */
public record OttPayload(
        OttType type,           // SIGNUP/RECOVER/LINK
        com.partyguham.auth.oauth.entity.Provider provider, // KAKAO/GOOGLE/...
        String externalId,      // 프로바이더가 준 계정 식별자(문자열로 통일)
        String email,           // 있을 때만
        String image,           // 있을 때만
        Long userId             // 연동/복구 등에서 필요할 때만
) { }