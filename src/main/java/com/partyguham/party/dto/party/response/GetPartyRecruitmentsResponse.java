package com.partyguham.party.dto.party.response;

import com.partyguham.recruitment.dto.response.PartyRecruitmentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyRecruitmentsResponse {

    private Long total;
    private List<PartyRecruitmentDto> partyRecruitments;

    public static GetPartyRecruitmentsResponse from(Long total, List<PartyRecruitmentDto> partyRecruitments) {
        return GetPartyRecruitmentsResponse.builder()
                .total(total)
                .partyRecruitments(partyRecruitments)
                .build();
    }
}

