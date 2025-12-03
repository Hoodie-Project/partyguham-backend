package com.partyguham.recruitment.dto.request;

/**
 * 개인화된 파티 모집 목록 조회 요청 DTO
 * 실제 필드는 추후 채워주세요.
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPartyRecruitmentsPersonalizedRequestDto {

    private int page;
    private int limit;

    @Builder.Default
    private String sort = "createdAt";

    @Builder.Default
    private String order = "ASC";
}



