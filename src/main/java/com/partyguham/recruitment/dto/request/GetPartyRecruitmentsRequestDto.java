package com.partyguham.recruitment.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;


/**
 * 파티 모집 목록 조회 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPartyRecruitmentsRequestDto {

    @Builder.Default
    private String sort = "createdAt";

    @Builder.Default
    private String order = "ASC";
    
    private String status;
    private String main;
}


