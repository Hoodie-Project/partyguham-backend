package com.partyguham.recruitment.dto.response;

import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.catalog.dto.response.PositionResponse;
import com.partyguham.party.dto.party.response.PartyDto;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyRecruitmentSearchDto {
    private Long id;
    private String content;
    private Integer recruitingCount;  
    private Integer recruitedCount;  
    private Boolean completed;
    private String createdAt;
    
    private PartyDto party;
    private PositionResponse position;

    public static PartyRecruitmentSearchDto from(PartyRecruitment partyRecruitment) {
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
                .party(PartyDto.from(partyRecruitment.getParty()))
                .position(PositionResponse.from(partyRecruitment.getPosition()))
                .build();
    }
}

