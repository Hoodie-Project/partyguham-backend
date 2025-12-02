package com.partyguham.recruitment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 파티 모집글 생성 요청 DTO
 * 실제 필드는 추후 채워주세요.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePartyRecruitmentRequestDto {
    
    private Long positionId;   

    private String content;      

    @JsonProperty("recruiting_count")
    private int recruitingCount;
}


