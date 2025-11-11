package com.partyguham.auth.ott;

/**
 * OTT에 저장할 페이로드
 * - 필요한 정보만 담는다 (민감정보 X)
 * - Jackson으로 직렬화/역직렬화하여 Redis에 저장
 *
 * 예: OAuth 콜백에서 발급할 때에는 oauthId/email/image 정도만 담음
 */
public record OttPayload(
        OttType type,
        Long oauthId,     // OAuth provider가 준 외부 ID (있을 때)
        String email,     // OAuth에서 받은 이메일 (있을 때)
        String image,     // 프로필 이미지 URL (있을 때)
        Long userId       // 이미 존재하는 유저 연동용일 때 사용 (선택)
) { }