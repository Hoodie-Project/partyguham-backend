package com.partyguham.recruitment.dto.response;

import com.partyguham.party.entity.PartyStatus;
import com.partyguham.recruitment.entity.PartyRecruitment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 단일 파티 모집글 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRecruitmentResponseDto {
    private Party party;
    private Position position;

    private String content;
    private int recruitingCount;
    private int recruitedCount;
    private int applicationCount;
    private boolean completed;
    private LocalDateTime createdAt;

    /**
     * PartyRecruitment 엔티티를 PartyRecruitmentResponseDto로 변환하는 정적 팩토리 메서드
     */
    public static PartyRecruitmentResponseDto from(PartyRecruitment recruitment) {
        // Party 매핑
        Party.PartyType partyTypeDto = Party.PartyType.builder()
                .type(recruitment.getParty().getPartyType().getType())
                .build();

        Party partyDto = Party.builder()
                .id(recruitment.getParty().getId())
                .title(recruitment.getParty().getTitle())
                .image(recruitment.getParty().getImage())
                .partyStatus(recruitment.getParty().getPartyStatus())
                .partyType(partyTypeDto)
                .build();

        Position positionDto = Position.builder()
                .id(recruitment.getPosition().getId())
                .main(recruitment.getPosition().getMain())
                .sub(recruitment.getPosition().getSub())
                .build();


        int applicationCount = recruitment.getPartyApplications() != null
                ? recruitment.getPartyApplications().size()
                : 0;

        return PartyRecruitmentResponseDto.builder()
                .party(partyDto)
                .position(positionDto)
                .content(recruitment.getContent())
                .recruitingCount(recruitment.getMaxParticipants())
                .recruitedCount(recruitment.getCurrentParticipants())
                .applicationCount(applicationCount)
                .completed(recruitment.getCompleted())
                .createdAt(recruitment.getCreatedAt() != null
                        ? recruitment.getCreatedAt()
                        : null)
                .build();
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Party {
        private Long id;
        private String title;
        private String image;
        private PartyStatus partyStatus;
        private PartyType partyType;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PartyType {
            private String type;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Position {
        private Long id;
        private String main;
        private String sub;
    }
}

