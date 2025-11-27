package com.partyguham.party.core.dto.party.response;

import com.partyguham.party.recruitment.entity.PartyRecruitment;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyRecruitmentSearchDto {

    private Long id;
    private String content;
    private Integer recruitingCount;  // 모집 중인 인원 수
    private Integer recruitedCount;   // 모집된 인원 수
    private String status;            // active / completed
    private String createdAt;
    private PartyDto party;
    private PositionDto position;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyDto {
        private Long id;
        private String title;
        private String image;
        private String status;
        private PartyTypeDto partyType;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PartyTypeDto {
            private String type;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDto {
        private Long id;
        private String main;
        private String sub;
    }

    public static PartyRecruitmentSearchDto from(PartyRecruitment partyRecruitment) {
        // Status 결정: BaseEntity의 Status 사용, completed 또는 active
        String status = partyRecruitment.getStatus() != null ? 
                partyRecruitment.getStatus().name().toLowerCase() : "active";
        
        // 모집된 인원 수 = currentParticipants
        // 모집 중인 인원 수 = maxParticipants - currentParticipants
        int recruitedCount = partyRecruitment.getCurrentParticipants() != null ? 
                partyRecruitment.getCurrentParticipants() : 0;
        int recruitingCount = partyRecruitment.getMaxParticipants() != null ? 
                (partyRecruitment.getMaxParticipants() - recruitedCount) : 0;

        return PartyRecruitmentSearchDto.builder()
                .id(partyRecruitment.getId())
                .content(partyRecruitment.getContent())
                .recruitingCount(recruitingCount)
                .recruitedCount(recruitedCount)
                .status(status)
                .createdAt(partyRecruitment.getCreatedAt().toString())
                .party(
                        PartyDto.builder()
                                .id(partyRecruitment.getParty().getId())
                                .title(partyRecruitment.getParty().getTitle())
                                .image(partyRecruitment.getParty().getImage())
                                .status(partyRecruitment.getParty().getStatus().name().toLowerCase())
                                .partyType(
                                        PartyDto.PartyTypeDto.builder()
                                                .type(partyRecruitment.getParty().getPartyType().getType())
                                                .build()
                                )
                                .build()
                )
                .position(null) // TODO: PartyRecruitment에 position 필드가 없으므로 null 처리
                .build();
    }
}

