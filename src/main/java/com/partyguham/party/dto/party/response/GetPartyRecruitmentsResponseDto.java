package com.partyguham.party.dto.party.response;

import com.partyguham.recruitment.dto.response.PartyRecruitmentDto;
import com.partyguham.recruitment.dto.response.PartyRecruitmentSearchDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyRecruitmentsResponseDto {

    private Long total;
    private List<PartyRecruitmentDto> partyRecruitments;
}

