package com.partyguham.recruitment.dto.response;

/**
 * 특정 파티의 모집글 목록 조회 응답 DTO
 * 실제 필드는 추후 채워주세요.
 */

import com.partyguham.recruitment.entity.PartyRecruitment;
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

        /**
         * PartyRecruitment 엔티티를 PartyRecruitmentDto로 변환하는 정적 팩토리 메서드
         */
        public static PartyRecruitmentDto from(PartyRecruitment recruitment) {
            // PartyDto 생성
            PartyDto.PartyTypeDto partyTypeDto = PartyDto.PartyTypeDto.builder()
                    .type(recruitment.getParty().getPartyType().getType())
                    .build();

            PartyDto partyDto = PartyDto.builder()
                    .id(recruitment.getParty().getId())
                    .title(recruitment.getParty().getTitle())
                    .image(recruitment.getParty().getImage())
                    .status(recruitment.getParty().getStatus().name())
                    .partyType(partyTypeDto)
                    .build();

            // Position 관계가 생기면 여기에서 PositionDto 매핑을 추가
            PositionDto positionDto = null;

            return PartyRecruitmentDto.builder()
                    .id(recruitment.getId())
                    .content(recruitment.getContent())
                    .recruitingCount(recruitment.getMaxParticipants())
                    .recruitedCount(recruitment.getCurrentParticipants())
                    .status(recruitment.isCompleted() ? "COMPLETED" : "RECRUITING")
                    .createdAt(recruitment.getCreatedAt() != null
                            ? recruitment.getCreatedAt().toString()
                            : null)
                    .party(partyDto)
                    .position(positionDto)
                    .build();
        }

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