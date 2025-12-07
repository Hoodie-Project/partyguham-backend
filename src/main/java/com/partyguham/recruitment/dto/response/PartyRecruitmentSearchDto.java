package com.partyguham.recruitment.dto.response;

import com.partyguham.party.entity.PartyStatus;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.catalog.dto.response.PositionResponse;
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
    private Boolean completed;
    private String createdAt;
    private PartyDto party;
    private PositionResponse position;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyDto {
        private Long id;
        private String title;
        private String image;
        private PartyStatus partyStatus;
        private PartyTypeDto partyType;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PartyTypeDto {
            private String type;
        }
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
                .completed(partyRecruitment.getCompleted())
                .createdAt(partyRecruitment.getCreatedAt().toString())
                .party(
                        PartyDto.builder()
                                .id(partyRecruitment.getParty().getId())
                                .title(partyRecruitment.getParty().getTitle())
                                .image(partyRecruitment.getParty().getImage())
                                .partyStatus(partyRecruitment.getParty().getPartyStatus())
                                .partyType(
                                        PartyDto.PartyTypeDto.builder()
                                                .type(partyRecruitment.getParty().getPartyType().getType())
                                                .build()
                                )
                                .build()
                )
                .position(PositionResponse.from(partyRecruitment.getPosition()))
                .build();
    }
}

