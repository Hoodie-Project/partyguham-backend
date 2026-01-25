package com.partyguham.domain.party.dto.party.response;

import com.partyguham.domain.recruitment.dto.response.PartyRecruitmentResponse;
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
    private List<PartyRecruitmentResponse> partyRecruitments;

    public static GetPartyRecruitmentsResponse from(Long total, List<PartyRecruitmentResponse> partyRecruitments) {
        return GetPartyRecruitmentsResponse.builder()
                .total(total)
                .partyRecruitments(partyRecruitments)
                .build();
    }
}

