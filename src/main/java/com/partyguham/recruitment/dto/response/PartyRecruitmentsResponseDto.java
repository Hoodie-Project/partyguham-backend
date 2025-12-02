package com.partyguham.recruitment.dto.response;

/**
 * 특정 파티의 모집글 목록 조회 응답 DTO
 * 실제 필드는 추후 채워주세요.
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRecruitmentsResponseDto {
    private long total;
    private List<PartyRecruitmentDto> partyRecruitments;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartyRecruitmentDto {

        private Long id;
        private String content;
        private int recruitingCount;
        private int recruitedCount;
        private String status;
        private String createdAt;

        private PartyDto party;
        private PositionDto position;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PartyDto {
            private Long id;
            private String title;
            private String image;
            private String status;
            private PartyTypeDto partyType;

            @Getter
            @Setter
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            public static class PartyTypeDto {
                private String type;
            }
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PositionDto {
            private Long id;
            private String main;
            private String sub;
        }
    }
}