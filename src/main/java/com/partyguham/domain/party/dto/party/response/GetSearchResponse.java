package com.partyguham.domain.party.dto.party.response;

import com.partyguham.domain.party.dto.party.PartiesDto;
import com.partyguham.domain.recruitment.dto.response.PartyRecruitmentSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSearchResponse {

    private PartySearchDto party;
    private PartyRecruitmentSearchResultDto partyRecruitment;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartySearchDto {
        private Long total;
        private java.util.List<PartiesDto> parties;

        public static PartySearchDto from(Long total, java.util.List<PartiesDto> parties) {
            return PartySearchDto.builder()
                    .total(total)
                    .parties(parties)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyRecruitmentSearchResultDto {
        private Long total;
        private java.util.List<PartyRecruitmentSearchResponse> partyRecruitments;

        public static PartyRecruitmentSearchResultDto from(Long total, java.util.List<PartyRecruitmentSearchResponse> partyRecruitments) {
            return PartyRecruitmentSearchResultDto.builder()
                    .total(total)
                    .partyRecruitments(partyRecruitments)
                    .build();
        }
    }

    public static GetSearchResponse from(
            Long partyTotal,
            java.util.List<PartiesDto> parties,
            Long recruitmentTotal,
            java.util.List<PartyRecruitmentSearchResponse> partyRecruitments
    ) {
        return GetSearchResponse.builder()
                .party(PartySearchDto.from(partyTotal, parties))
                .partyRecruitment(PartyRecruitmentSearchResultDto.from(recruitmentTotal, partyRecruitments))
                .build();
    }
}