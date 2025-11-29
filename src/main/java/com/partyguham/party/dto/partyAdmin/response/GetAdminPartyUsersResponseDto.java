package com.partyguham.party.dto.partyAdmin.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 파티원 목록 조회 응답 DTO (관리자용)
 */
@Getter
@Builder
public class GetAdminPartyUsersResponseDto {

    private long totalCount;     /** 파티 전체 인원 수 (필터 상관 없이) */
    private long total;     /** 필터 + 페이징 후 인원 수 */
    private List<AdminPartyUserDto> partyUsers;     /** 파티원 목록 */

    @Getter
    @Builder
    public static class AdminPartyUserDto {
        private Long id;
        private String authority; // "master", "member" 등 소문자
        private UserSummary user;
        private PositionSummary position;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String status;
    }

    @Getter
    @Builder
    public static class UserSummary {
        private String nickname;
        private String image;
    }

    @Getter
    @Builder
    public static class PositionSummary {
        private String main; // main 카테고리 (예: '개발자')
        private String sub;  // sub 카테고리 (예: '백엔드')
    }
}